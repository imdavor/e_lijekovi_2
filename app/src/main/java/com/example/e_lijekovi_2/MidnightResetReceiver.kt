package com.example.e_lijekovi_2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.Calendar
import java.util.Date

/**
 * Receiver koji se poziva točno u ponoć (zakazan AlarmManager-om) kako bi obrisao per-day flagove
 * (dozeZaDan) i pohranio promjene u lokalni storage. Nakon izvršenja ponovno zakazuje sljedeći
 * reset za sljedeću ponoć.
 */
class MidnightResetReceiver : BroadcastReceiver() {
    companion object { private const val TAG = "MidnightResetReceiver" }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Midnight reset primljen - pokrećem čišćenje per-day flagova")

        try {
            val lijekovi = LijekoviDataManager.loadFromLocalStorage(context)?.toMutableList() ?: mutableListOf()
            var changed = false

            val today = IntervalnoUzimanje.createDateFormat().format(Date())
            // compute yesterday string once
            val cal = Calendar.getInstance()
            cal.time = Date()
            cal.add(Calendar.DAY_OF_MONTH, -1)
            val yesterday = IntervalnoUzimanje.createDateFormat().format(cal.time)

            for (i in 0 until lijekovi.size) {
                val l = lijekovi[i]
                var updated = l
                var localChanged = false

                // Clear per-day flags if present
                if (l.dozeZaDan.isNotEmpty()) {
                    updated = updated.copy(dozeZaDan = mutableMapOf())
                    localChanged = true
                }

                // Move any complianceHistory entries that are dated 'today' to 'yesterday'
                // This preserves the record but prevents them from blocking today's availability after reset
                if (l.complianceHistory.isNotEmpty()) {
                    val newHistory = l.complianceHistory.map { rec ->
                        if (rec.date == today) {
                            localChanged = true
                            rec.copy(date = yesterday)
                        } else rec
                    }
                    if (newHistory !== l.complianceHistory) {
                        updated = updated.copy(complianceHistory = newHistory)
                    }
                }

                if (localChanged) {
                    lijekovi[i] = updated
                    changed = true
                }
            }

            if (changed) {
                val saved = LijekoviDataManager.saveToLocalStorage(context, lijekovi)
                Log.d(TAG, "Per-day flags obrisani i prilagođena povijest, spremljeno: $saved")
                NotificationScheduler.sendImmediateNotification(context, "Reset uzimanja", "Dnevni flagovi su resetirani u ponoć", 4001)
                try {
                    val prefs = context.getSharedPreferences("e_lijekovi_prefs", Context.MODE_PRIVATE)
                    prefs.edit().putString("last_daily_reset", today).apply()
                } catch (e: Exception) {
                    Log.w(TAG, "Ne mogu zapisati last_daily_reset: ${e.message}")
                }
                // Notify any running app process to reload data (so in-memory state updates)
                try {
                    val reloadIntent = Intent("com.example.e_lijekovi_2.ACTION_PER_DAY_FLAGS_RESET")
                    // send a normal broadcast which the app can register for at runtime
                    context.sendBroadcast(reloadIntent)
                } catch (e: Exception) {
                    Log.w(TAG, "Ne mogu poslati reset broadcast: ${e.message}")
                }
            } else {
                Log.d(TAG, "Nije pronađen nijedan per-day flag za brisanje")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Greška pri resetu per-day flagova: ${e.message}", e)
        } finally {
            // Ponovno zakazujemo sljedeći reset
            try {
                NotificationScheduler.scheduleMidnightReset(context)
            } catch (e: Exception) {
                Log.e(TAG, "Ne mogu ponovno zakazati midnight reset: ${e.message}", e)
            }
        }
    }
}
