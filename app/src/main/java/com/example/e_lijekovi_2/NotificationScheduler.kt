package com.example.e_lijekovi_2

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.util.*

object NotificationScheduler {
    const val CHANNEL_ID = "e_lijekovi_reminders"

    fun scheduleDailyReminder(context: Context, hour: Int, minute: Int, requestCode: Int, title: String, message: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
            putExtra("notificationId", requestCode)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        // If time already passed for today, schedule for tomorrow
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        // Use set() (non-exact) to avoid requiring SCHEDULE_EXACT_ALARM permission
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    fun cancelReminder(context: Context, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    @Suppress("unused")
    fun scheduleDailyReminder(context: Context, time: String, label: String) {
        val parts = time.split(":")
        if (parts.size < 2) return
        val hour = parts[0].toIntOrNull() ?: return
        val minute = parts[1].toIntOrNull() ?: return

        when (label) {
            "Jutro" -> scheduleDailyReminder(context, hour, minute, 1001, "Jutarnji podsjetnik", "Vrijeme je za jutarnje lijekove")
            "Podne" -> scheduleDailyReminder(context, hour, minute, 1002, "Podnevni podsjetnik", "Vrijeme je za podnevne lijekove")
            "Večer" -> scheduleDailyReminder(context, hour, minute, 1003, "Večernji podsjetnik", "Vrijeme je za večernje lijekove")
            else -> scheduleDailyReminder(context, hour, minute, 1000, "Podsjetnik", "Vrijeme je za lijekove")
        }
    }

    @Suppress("unused")
    fun cancelReminder(context: Context, label: String) {
        when (label) {
            "Jutro" -> cancelReminder(context, 1001)
            "Podne" -> cancelReminder(context, 1002)
            "Večer" -> cancelReminder(context, 1003)
            else -> cancelReminder(context, 1000)
        }
    }

    /**
     * Send an immediate (one-off) notification with given title/message.
     * Performs POST_NOTIFICATIONS permission check on Android 13+.
     */
    fun sendImmediateNotification(context: Context, title: String, message: String, notificationId: Int = 2000) {
        // Check notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Permission not granted; do not post
                return
            }
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        // Use NotificationManagerCompat to post notification (avoids platform cast issues in editor/runtime)
        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }

    /**
     * Send a single aggregated notification indicating the therapy for a time-of-day was taken.
     * label is expected to be one of: "Jutro", "Podne", "Večer" (falls back to a generic message).
     */
    fun sendTherapyTakenNotification(context: Context, label: String) {
        val (title, message, id) = when (label) {
            "Jutro" -> Triple("Jutarnja terapija uzeta", "Sva jutarnja terapija je označena kao uzeta.", 3001)
            "Podne" -> Triple("Podnevna terapija uzeta", "Sva podnevna terapija je označena kao uzeta.", 3002)
            "Večer" -> Triple("Večernja terapija uzeta", "Sva večernja terapija je označena kao uzeta.", 3003)
            else -> Triple("Terapija uzeta", "Sva terapija je označena kao uzeta.", 3000)
        }
        sendImmediateNotification(context, title, message, id)
    }
}
