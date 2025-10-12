@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)

package com.example.e_lijekovi_2

import kotlinx.serialization.Serializable
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Contextual
import java.util.*
import java.text.SimpleDateFormat

@Serializable
enum class DobaDana {
    JUTRO, POPODNE, VECER
}

@Serializable
enum class TipUzimanja {
    STANDARDNO, // Jutro/popodne/večer
    INTERVALNO  // Svakih X sati
}

@OptIn(InternalSerializationApi::class)
@Serializable
data class UzimanjeRecord(
    val scheduledTime: String, // Planirano vrijeme uzimanja (HH:mm)
    val actualTime: String? = null, // Stvarno vrijeme uzimanja (HH:mm), null ako nije uzet
    val isLate: Boolean = false, // Je li uzet kasno (>30 min nakon planiranog)
    val date: String // Datum uzimanja (yyyy-MM-dd)
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class IntervalnoUzimanje(
    val intervalSati: Int, // Svakih koliko sati
    val startDateTime: String = "", // Početni datum i vrijeme (yyyy-MM-dd HH:mm), prazno za trenutno vrijeme
    val trajanjeDana: Int = 7, // Koliko dana se uzima lijek
    val complianceHistory: List<UzimanjeRecord> = emptyList() // Povijest uzimanja za compliance tracking
) {
    companion object {
        private val dateTimeFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
        private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    }

    // Dobij početno vrijeme kao Calendar
    private fun getStartCalendar(): Calendar {
        val calendar = Calendar.getInstance()
        if (startDateTime.isNotEmpty()) {
            try {
                calendar.time = dateTimeFormat.parse(startDateTime) ?: Date()
            } catch (e: Exception) {
                // Fallback na trenutno vrijeme ako parsing fail-a
            }
        }
        return calendar
    }

    // Generiraj sva planirana vremena uzimanja za određeni dan
    fun generirajVremenaZaDan(date: String): List<String> {
        val vremena = mutableListOf<String>()
        val targetDate = dateFormat.parse(date) ?: return emptyList()
        val startCal = getStartCalendar()

        // Provjeri je li terapija aktivna za ovaj dan
        val daysDiff = ((targetDate.time - startCal.time.time) / (1000 * 60 * 60 * 24)).toInt()
        if (daysDiff < 0 || daysDiff >= trajanjeDana) return emptyList()

        // Postavi calendar na početak target dana s vremenom prvog uzimanja
        val dayCal = Calendar.getInstance()
        dayCal.time = targetDate
        dayCal.set(Calendar.HOUR_OF_DAY, startCal.get(Calendar.HOUR_OF_DAY))
        dayCal.set(Calendar.MINUTE, startCal.get(Calendar.MINUTE))
        dayCal.set(Calendar.SECOND, 0)
        dayCal.set(Calendar.MILLISECOND, 0)

        val krajDana = Calendar.getInstance()
        krajDana.time = targetDate
        krajDana.set(Calendar.HOUR_OF_DAY, 23)
        krajDana.set(Calendar.MINUTE, 59)
        krajDana.set(Calendar.SECOND, 59)

        while (dayCal.timeInMillis <= krajDana.timeInMillis) {
            vremena.add(timeFormat.format(dayCal.time))
            dayCal.add(Calendar.HOUR_OF_DAY, intervalSati)
        }

        return vremena
    }

    // Generiraj vremena uzimanja za današnji dan
    fun generirajVremenaZaDanas(): List<String> {
        val today = dateFormat.format(Date())
        return generirajVremenaZaDan(today)
    }

    // Sljedeće vrijeme uzimanja
    fun sljedeceVrijeme(): String? {
        val sada = Calendar.getInstance()
        val today = dateFormat.format(Date())
        val vremenaZaDanas = generirajVremenaZaDan(today)

        for (vrijeme in vremenaZaDanas) {
            val vrijemeArray = vrijeme.split(":")
            val vrijemeCalendar = Calendar.getInstance()
            vrijemeCalendar.set(Calendar.HOUR_OF_DAY, vrijemeArray[0].toInt())
            vrijemeCalendar.set(Calendar.MINUTE, vrijemeArray[1].toInt())
            vrijemeCalendar.set(Calendar.SECOND, 0)
            vrijemeCalendar.set(Calendar.MILLISECOND, 0)

            if (vrijemeCalendar.timeInMillis > sada.timeInMillis) {
                return vrijeme
            }
        }
        return null // Nema više uzimanja danas
    }

    // Označi dozu kao uzeta
    fun oznaciDozuUzeta(scheduledTime: String, actualTime: String? = null): IntervalnoUzimanje {
        val today = dateFormat.format(Date())
        val actual = actualTime ?: timeFormat.format(Date())

        // Provjeri je li kasno (>30 min)
        val isLateTaking = if (actualTime != null) {
            val scheduledParts = scheduledTime.split(":")
            val actualParts = actual.split(":")
            val scheduledMins = scheduledParts[0].toInt() * 60 + scheduledParts[1].toInt()
            val actualMins = actualParts[0].toInt() * 60 + actualParts[1].toInt()
            (actualMins - scheduledMins) > 30
        } else {
            // Ako se uzima sada, provjeri u odnosu na planirano
            val scheduledParts = scheduledTime.split(":")
            val now = Calendar.getInstance()
            val scheduledMins = scheduledParts[0].toInt() * 60 + scheduledParts[1].toInt()
            val nowMins = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
            (nowMins - scheduledMins) > 30
        }

        val newRecord = UzimanjeRecord(
            scheduledTime = scheduledTime,
            actualTime = actual,
            isLate = isLateTaking,
            date = today
        )

        // Dodaj novi record, zadrži samo zadnjih 90 dana
        val updatedHistory = (complianceHistory + newRecord).takeLast(90)

        return copy(complianceHistory = updatedHistory)
    }

    // Dobij compliance statistike
    fun getComplianceStats(days: Int = 30): ComplianceStats {
        val cutoffDate = Calendar.getInstance()
        cutoffDate.add(Calendar.DAY_OF_MONTH, -days)
        val cutoffString = dateFormat.format(cutoffDate.time)

        val relevantRecords = complianceHistory.filter { it.date >= cutoffString }

        val totalScheduled = relevantRecords.size
        val totalTaken = relevantRecords.count { it.actualTime != null }
        val totalLate = relevantRecords.count { it.isLate }
        val onTime = totalTaken - totalLate

        val complianceRate = if (totalScheduled > 0) (totalTaken.toFloat() / totalScheduled * 100) else 0f
        val onTimeRate = if (totalTaken > 0) (onTime.toFloat() / totalTaken * 100) else 0f

        return ComplianceStats(
            totalScheduled = totalScheduled,
            totalTaken = totalTaken,
            totalLate = totalLate,
            complianceRate = complianceRate,
            onTimeRate = onTimeRate,
            periodDays = days
        )
    }

    // Provjeri je li doza već uzeta danas
    fun jeDozuUzetaDanas(scheduledTime: String): Boolean {
        val today = dateFormat.format(Date())
        return complianceHistory.any { it.date == today && it.scheduledTime == scheduledTime && it.actualTime != null }
    }

    // Dobij prosječno kašnjenje u minutama
    fun getAverageDelayMinutes(): Double {
        val takenRecords = complianceHistory.filter { it.actualTime != null && it.isLate }
        if (takenRecords.isEmpty()) return 0.0

        var totalDelay = 0
        for (record in takenRecords) {
            val scheduledParts = record.scheduledTime.split(":")
            val actualParts = record.actualTime?.split(":") ?: continue
            val scheduledMins = scheduledParts[0].toInt() * 60 + scheduledParts[1].toInt()
            val actualMins = actualParts[0].toInt() * 60 + actualParts[1].toInt()
            totalDelay += (actualMins - scheduledMins)
        }

        return totalDelay.toDouble() / takenRecords.size
    }
}

@Serializable
data class ComplianceStats(
    val totalScheduled: Int,
    val totalTaken: Int,
    val totalLate: Int,
    val complianceRate: Float, // Postotak uzimanja
    val onTimeRate: Float, // Postotak na vrijeme
    val periodDays: Int
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class Lijek(
    val id: Int,
    val naziv: String,
    val doza: String,
    val tipUzimanja: TipUzimanja = TipUzimanja.STANDARDNO,

    // Za standardno uzimanje
    val jutro: Boolean = false,
    val popodne: Boolean = false,
    val vecer: Boolean = false,
    val vrijemeJutro: String = "08:00",
    val vrijemePopodne: String = "14:00",
    val vrijemeVecer: String = "20:00",

    // Za intervalno uzimanje
    val intervalnoUzimanje: IntervalnoUzimanje? = null,

    val napomene: String = "",
    val boja: String = "#4CAF50", // Default zelena boja

    // Pakiranje - broj tableta/doza u jednom pakiranju
    val pakiranje: Int = 30, // Default 30 tableta po pakiranju

    // Za redoslijed prikaza unutar vremenske grupe
    val sortOrderJutro: Int = 0,
    val sortOrderPopodne: Int = 0,
    val sortOrderVecer: Int = 0
) {
    // Generiraj sva vremena uzimanja za današnji dan
    fun generirajVremenaZaDanas(): List<String> {
        return when (tipUzimanja) {
            TipUzimanja.STANDARDNO -> {
                val vremena = mutableListOf<String>()
                if (jutro) vremena.add(vrijemeJutro)
                if (popodne) vremena.add(vrijemePopodne)
                if (vecer) vremena.add(vrijemeVecer)
                vremena.sorted()
            }
            TipUzimanja.INTERVALNO -> {
                intervalnoUzimanje?.generirajVremenaZaDanas() ?: emptyList()
            }
        }
    }

    // Sljedeće vrijeme uzimanja
    fun sljedeceVrijeme(): String? {
        val vremenaZaDanas = generirajVremenaZaDanas()
        val sada = Calendar.getInstance()

        for (vrijeme in vremenaZaDanas) {
            val vrijemeArray = vrijeme.split(":")
            val vrijemeCalendar = Calendar.getInstance()
            vrijemeCalendar.set(Calendar.HOUR_OF_DAY, vrijemeArray[0].toInt())
            vrijemeCalendar.set(Calendar.MINUTE, vrijemeArray[1].toInt())
            vrijemeCalendar.set(Calendar.SECOND, 0)
            vrijemeCalendar.set(Calendar.MILLISECOND, 0)

            if (vrijemeCalendar.timeInMillis > sada.timeInMillis) {
                return vrijeme
            }
        }
        return null // Nema više uzimanja danas
    }

    // Provjeri je li vrijeme za uzimanje lijeka (±15 minuta)
    fun jeVrijemeZaUzimanje(): Boolean {
        val sada = Calendar.getInstance()
        val vremenaZaDanas = generirajVremenaZaDanas()

        for (vrijeme in vremenaZaDanas) {
            val vrijemeArray = vrijeme.split(":")
            val vrijemeCalendar = Calendar.getInstance()
            vrijemeCalendar.set(Calendar.HOUR_OF_DAY, vrijemeArray[0].toInt())
            vrijemeCalendar.set(Calendar.MINUTE, vrijemeArray[1].toInt())
            vrijemeCalendar.set(Calendar.SECOND, 0)
            vrijemeCalendar.set(Calendar.MILLISECOND, 0)

            val razlikaMillis = kotlin.math.abs(sada.timeInMillis - vrijemeCalendar.timeInMillis)
            val razlikaMinuta = razlikaMillis / (1000 * 60) // Convert to minutes

            if (razlikaMinuta <= 15) {
                return true
            }
        }
        return false
    }
}
