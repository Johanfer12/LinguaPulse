package com.antigravity.linguapulse

import android.app.Application
import com.antigravity.linguapulse.data.AppDatabase
import com.antigravity.linguapulse.data.CardRepository
import com.antigravity.linguapulse.data.UserPreferences
import com.antigravity.linguapulse.notifications.CardNotificationWorker
import com.antigravity.linguapulse.notifications.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class LinguaPulseApp : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { CardRepository(database.flashcardDao()) }
    val preferences by lazy { UserPreferences(this) }

    override fun onCreate() {
        super.onCreate()

        // Todo el arranque pesado ocurre fuera del hilo principal: crear el canal
        // de notificaciones y abrir Room en onCreate() retrasaba el primer frame.
        applicationScope.launch {
            NotificationHelper.createNotificationChannel(this@LinguaPulseApp)
            repository.ensureInitialDataLoaded()

            // KEEP: respeta el trabajo ya programado en lugar de reiniciar el
            // temporizador cada vez que se abre la app.
            CardNotificationWorker.schedulePeriodic(
                context = this@LinguaPulseApp,
                intervalMinutes = preferences.notificationIntervalMinutes,
                replaceExisting = false
            )
        }
    }
}
