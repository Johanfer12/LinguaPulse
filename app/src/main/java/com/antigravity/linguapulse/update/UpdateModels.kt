package com.antigravity.linguapulse.update

/** Informacion de la ultima version publicada en GitHub Releases. */
data class ReleaseInfo(
    val versionCode: Int,
    val versionName: String,
    val apkUrl: String,
    val notes: String
)

/** Estado del flujo de actualizacion mostrado en la UI. */
sealed interface UpdateState {
    /** Nada en curso. */
    data object Idle : UpdateState

    /** Consultando GitHub. */
    data object Checking : UpdateState

    /** La app ya esta en la ultima version publicada. */
    data class UpToDate(val checkedAt: Long) : UpdateState

    /** Hay una version nueva disponible. */
    data class Available(val release: ReleaseInfo) : UpdateState

    /** Descargando el APK (progreso 0..1, o null si es indeterminado). */
    data class Downloading(val release: ReleaseInfo, val progress: Float?) : UpdateState

    /** APK listo: se lanzo el instalador del sistema. */
    data class ReadyToInstall(val release: ReleaseInfo) : UpdateState

    /** Algo fallo (sin red, release sin APK, etc.). */
    data class Failed(val message: String) : UpdateState
}
