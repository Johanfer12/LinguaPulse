package com.antigravity.linguapulse.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import com.antigravity.linguapulse.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

/**
 * Comprobacion e instalacion de actualizaciones publicadas por GitHub Actions.
 *
 * Cada push al branch por defecto genera un release con el APK firmado y un
 * update.json. La app compara el versionCode remoto con el suyo y, si hay uno
 * mayor, descarga el APK y lanza el instalador del sistema.
 */
object UpdateManager {

    private const val CONNECT_TIMEOUT_MS = 12_000
    private const val READ_TIMEOUT_MS = 20_000
    private const val APK_FILE_NAME = "LinguaPulse-update.apk"

    private val updateJsonUrl: String
        get() = "https://github.com/${BuildConfig.GITHUB_OWNER}/${BuildConfig.GITHUB_REPO}" +
                "/releases/latest/download/update.json"

    private val releasesApiUrl: String
        get() = "https://api.github.com/repos/${BuildConfig.GITHUB_OWNER}/${BuildConfig.GITHUB_REPO}" +
                "/releases/latest"

    /**
     * Consulta la ultima version publicada.
     *
     * Intenta primero el update.json del release (barato y sin limite de
     * peticiones) y cae a la API de GitHub si ese asset no existe todavia.
     */
    suspend fun fetchLatestRelease(): Result<ReleaseInfo> = withContext(Dispatchers.IO) {
        runCatching { readUpdateJson() }
            .recoverCatching { readFromGitHubApi() }
    }

    /** true si la version publicada es mas nueva que la instalada. */
    fun isNewer(release: ReleaseInfo): Boolean = release.versionCode > BuildConfig.VERSION_CODE

    private fun readUpdateJson(): ReleaseInfo {
        val json = JSONObject(httpGet(updateJsonUrl, accept = "application/json"))
        return ReleaseInfo(
            versionCode = json.getInt("versionCode"),
            versionName = json.optString("versionName", "desconocida"),
            apkUrl = json.getString("apkUrl"),
            notes = json.optString("notes", "")
        )
    }

    private fun readFromGitHubApi(): ReleaseInfo {
        val json = JSONObject(httpGet(releasesApiUrl, accept = "application/vnd.github+json"))
        val tag = json.optString("tag_name", "").removePrefix("v")
        val assets = json.getJSONArray("assets")

        var apkUrl: String? = null
        for (i in 0 until assets.length()) {
            val asset = assets.getJSONObject(i)
            if (asset.optString("name").endsWith(".apk", ignoreCase = true)) {
                apkUrl = asset.optString("browser_download_url")
                break
            }
        }
        requireNotNull(apkUrl) { "El release publicado no incluye un APK." }

        // El versionCode viaja en el cuerpo del release como "versionCode: N".
        val versionCode = Regex("versionCode[^0-9]*([0-9]+)")
            .find(json.optString("body", ""))
            ?.groupValues
            ?.get(1)
            ?.toIntOrNull()
            ?: (BuildConfig.VERSION_CODE + 1)

        return ReleaseInfo(
            versionCode = versionCode,
            versionName = tag.ifBlank { "desconocida" },
            apkUrl = apkUrl,
            notes = json.optString("name", "")
        )
    }

    private fun httpGet(url: String, accept: String): String {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = CONNECT_TIMEOUT_MS
            readTimeout = READ_TIMEOUT_MS
            instanceFollowRedirects = true
            setRequestProperty("Accept", accept)
            setRequestProperty("User-Agent", "LinguaPulse/" + BuildConfig.VERSION_NAME)
        }
        try {
            if (connection.responseCode !in 200..299) {
                throw IllegalStateException("HTTP ${connection.responseCode} al consultar actualizaciones")
            }
            return connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    /**
     * Descarga el APK al almacenamiento privado de la app.
     *
     * @param onProgress recibe el avance entre 0 y 1, o null cuando el servidor
     *        no informa el tamano total.
     */
    suspend fun downloadApk(
        context: Context,
        release: ReleaseInfo,
        onProgress: (Float?) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        runCatching {
            val target = File(context.cacheDir, APK_FILE_NAME)
            if (target.exists()) target.delete()

            val connection = (URL(release.apkUrl).openConnection() as HttpURLConnection).apply {
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
                instanceFollowRedirects = true
                setRequestProperty("User-Agent", "LinguaPulse/" + BuildConfig.VERSION_NAME)
            }

            try {
                if (connection.responseCode !in 200..299) {
                    throw IllegalStateException("HTTP ${connection.responseCode} al descargar el APK")
                }

                val total = connection.contentLength.toLong()
                var downloaded = 0L
                var lastReported = -1

                connection.inputStream.use { input ->
                    target.outputStream().use { output ->
                        val buffer = ByteArray(16 * 1024)
                        while (true) {
                            val read = input.read(buffer)
                            if (read == -1) break
                            output.write(buffer, 0, read)
                            downloaded += read

                            if (total > 0) {
                                // Se notifica en pasos de 1% para no saturar la UI.
                                val percent = ((downloaded * 100) / total).toInt()
                                if (percent != lastReported) {
                                    lastReported = percent
                                    onProgress(percent / 100f)
                                }
                            } else {
                                onProgress(null)
                            }
                        }
                    }
                }
            } finally {
                connection.disconnect()
            }

            target
        }
    }

    /** Lanza el instalador del sistema para el APK descargado. */
    fun installApk(context: Context, apk: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            context.packageName + ".updates",
            apk
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    /** true si el sistema ya permite instalar APKs desde la app. */
    fun canRequestInstall(context: Context): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.packageManager.canRequestPackageInstalls()
        } else {
            true
        }

    /** Abre los ajustes de "instalar apps desconocidas" para esta app. */
    fun openInstallPermissionSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(
                Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                Uri.parse("package:" + context.packageName)
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}
