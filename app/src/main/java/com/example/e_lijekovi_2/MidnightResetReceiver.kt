package com.example.e_lijekovi_2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
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
            for (i in 0 until lijekovi.size) {
                val l = lijekovi[i]
                if (l.dozeZaDan.isNotEmpty()) {
                    lijekovi[i] = l.copy(dozeZaDan = mutableMapOf())
                    changed = true
                }
            }

            if (changed) {
                val saved = LijekoviDataManager.saveToLocalStorage(context, lijekovi)
                Log.d(TAG, "Per-day flags obrisani, spremljeno: $saved")
                NotificationScheduler.sendImmediateNotification(context, "Reset uzimanja", "Dnevni flagovi su resetirani u ponoć", 4001)
                try {
                    val prefs = context.getSharedPreferences("e_lijekovi_prefs", Context.MODE_PRIVATE)
                    val today = IntervalnoUzimanje.createDateFormat().format(Date())
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
