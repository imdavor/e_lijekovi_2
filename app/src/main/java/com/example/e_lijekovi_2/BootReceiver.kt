package com.example.e_lijekovi_2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Reschedule reminders from SharedPreferences
            val prefs = context.getSharedPreferences("e_lijekovi_prefs", Context.MODE_PRIVATE)
            val jutro = prefs.getString("reminder_jutro", null)
            val podne = prefs.getString("reminder_podne", null)
            val vecer = prefs.getString("reminder_vecer", null)

            jutro?.let {
                val parts = it.split(":")
                if (parts.size >= 2) NotificationScheduler.scheduleDailyReminder(context, parts[0].toInt(), parts[1].toInt(), 1001, "Jutarnji podsjetnik", "Vrijeme je za jutarnje lijekove")
            }
            podne?.let {
                val parts = it.split(":")
                if (parts.size >= 2) NotificationScheduler.scheduleDailyReminder(context, parts[0].toInt(), parts[1].toInt(), 1002, "Podnevni podsjetnik", "Vrijeme je za podnevne lijekove")
            }
            vecer?.let {
                val parts = it.split(":")
                if (parts.size >= 2) NotificationScheduler.scheduleDailyReminder(context, parts[0].toInt(), parts[1].toInt(), 1003, "Večernji podsjetnik", "Vrijeme je za večernje lijekove")
            }
        }
    }
}

