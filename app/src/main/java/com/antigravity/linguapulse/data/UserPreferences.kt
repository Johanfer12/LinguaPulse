package com.antigravity.linguapulse.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Preferencias locales de la app.
 *
 * Antes el intervalo de notificaciones solo vivia en memoria, asi que se perdia
 * al cerrar la app y el WorkManager quedaba desincronizado con lo que mostraba
 * la pantalla de Ajustes. Ahora se persiste en SharedPreferences.
 */
class UserPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** Intervalo entre notificaciones, en minutos. */
    var notificationIntervalMinutes: Long
        get() = prefs.getLong(KEY_INTERVAL_MINUTES, DEFAULT_INTERVAL_MINUTES)
        set(value) = prefs.edit().putLong(KEY_INTERVAL_MINUTES, value).apply()

    /** Si la app debe buscar actualizaciones al abrirse. */
    var autoCheckUpdates: Boolean
        get() = prefs.getBoolean(KEY_AUTO_UPDATES, true)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_UPDATES, value).apply()

    /** Marca de tiempo de la ultima comprobacion de actualizaciones. */
    var lastUpdateCheck: Long
        get() = prefs.getLong(KEY_LAST_UPDATE_CHECK, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_UPDATE_CHECK, value).apply()

    companion object {
        private const val PREFS_NAME = "linguapulse_prefs"
        private const val KEY_INTERVAL_MINUTES = "notification_interval_minutes"
        private const val KEY_AUTO_UPDATES = "auto_check_updates"
        private const val KEY_LAST_UPDATE_CHECK = "last_update_check"

        /** WorkManager no admite periodos menores a 15 minutos. */
        const val MIN_INTERVAL_MINUTES = 15L
        const val DEFAULT_INTERVAL_MINUTES = 60L

        /** Opciones ofrecidas en Ajustes (minutos -> etiqueta). */
        val INTERVAL_OPTIONS: List<Pair<Long, String>> = listOf(
            30L to "Cada 30 minutos",
            60L to "Cada hora (Recomendado)",
            120L to "Cada 2 horas",
            240L to "Cada 4 horas",
            480L to "Cada 8 horas",
            1440L to "1 vez al dia"
        )

        fun labelFor(minutes: Long): String =
            INTERVAL_OPTIONS.firstOrNull { it.first == minutes }?.second
                ?: if (minutes < 60) "Cada $minutes minutos" else "Cada ${minutes / 60} horas"
    }
}
