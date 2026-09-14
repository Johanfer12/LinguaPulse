package com.antigravity.linguapulse.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.antigravity.linguapulse.MainActivity
import com.antigravity.linguapulse.R
import com.antigravity.linguapulse.data.Categories
import com.antigravity.linguapulse.data.Flashcard

object NotificationHelper {

    const val CHANNEL_ID = "linguapulse_daily_srs"
    const val NOTIFICATION_ID = 1001
    const val ACTION_REVIEW = "com.antigravity.linguapulse.ACTION_REVIEW_CARD"
    const val EXTRA_CARD_ID = "extra_card_id"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notification_channel_name)
            val descriptionText = context.getString(R.string.notification_channel_desc)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showCardNotification(context: Context, card: Flashcard) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            action = ACTION_REVIEW
            putExtra(EXTRA_CARD_ID, card.id)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            card.id.toInt(),
            intent,
            pendingIntentFlags
        )

        val title = if (card.category == Categories.B2B_SALES) {
            "🚀 B2B Tech: ${card.termEn}"
        } else {
            "💡 Daily English: ${card.termEn}"
        }

        // Exact requested format: Palabra y significado
        val contentText = "${card.termEn}: ${card.meaningEs}"

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(contentText)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("${card.termEn}\n👉 ${card.meaningEs}\n\nToca para revisar si ya te la aprendiste.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.ic_notification,
                "Revisar Tarjeta",
                pendingIntent
            )

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        } catch (e: SecurityException) {
            // Android 13+ POST_NOTIFICATIONS permission not yet granted
            e.printStackTrace()
        }
    }
}
