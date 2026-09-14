package com.antigravity.linguapulse.notifications

import android.content.Context
import androidx.work.*
import com.antigravity.linguapulse.data.AppDatabase
import com.antigravity.linguapulse.data.CardRepository
import com.antigravity.linguapulse.data.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class CardNotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val database = AppDatabase.getDatabase(applicationContext, this)
            val repository = CardRepository(database.flashcardDao())
            repository.ensureInitialDataLoaded()

            val card = repository.getRandomCardForNotification()
            if (card != null) {
                NotificationHelper.showCardNotification(applicationContext, card)
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "LinguaPulseNotificationWork"

        /**
         * Programa el envio periodico de tarjetas.
         *
         * WorkManager no acepta periodos menores a 15 minutos, asi que cualquier
         * valor mas corto se eleva a ese minimo.
         *
         * @param replaceExisting true cuando el usuario cambia la frecuencia (reinicia
         *        el temporizador); false al arrancar la app, para no reiniciar en cada
         *        apertura el trabajo ya programado.
         */
        fun schedulePeriodic(
            context: Context,
            intervalMinutes: Long = UserPreferences.DEFAULT_INTERVAL_MINUTES,
            replaceExisting: Boolean = true
        ) {
            val safeInterval = intervalMinutes.coerceAtLeast(UserPreferences.MIN_INTERVAL_MINUTES)

            val periodicWork = PeriodicWorkRequestBuilder<CardNotificationWorker>(
                safeInterval, TimeUnit.MINUTES
            )
                .setConstraints(Constraints.Builder().build())
                .setInitialDelay(safeInterval, TimeUnit.MINUTES)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 5, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                if (replaceExisting) {
                    ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE
                } else {
                    ExistingPeriodicWorkPolicy.KEEP
                },
                periodicWork
            )
        }

        fun cancelPeriodic(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }

        fun triggerImmediate(context: Context) {
            val oneTimeWork = OneTimeWorkRequestBuilder<CardNotificationWorker>().build()
            WorkManager.getInstance(context).enqueue(oneTimeWork)
        }
    }
}
