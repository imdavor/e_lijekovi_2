@file:OptIn(InternalSerializationApi::class)

package com.example.e_lijekovi_2

import kotlinx.serialization.Serializable
import kotlinx.serialization.InternalSerializationApi
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
    val date: String, // Datum uzimanja (yyyy-MM-dd)
    // New: snapshot of price per package at moment of taking (npr. "12,50") - nullable for backward compatibility
    val priceAtTake: String? = null
)

@OptIn(InternalSerializationApi::class)
@Serializable
data class IntervalnoUzimanje(
    val intervalSati: Int, // Svakih koliko sati
    val startDateTime: String = "", // Početni datum i vrijeme (yyyy-MM-dd HH:mm), prazno za trenutno vrijeme
    val trajanjeDana: Int = 7, // Koliko dana se uzima lijek
    val complianceHistory: List<UzimanjeRecord> = emptyList(), // Povijest uzimanja za compliance tracking
    val ukupnoUzimanja: Int = 0 // Ukupno planiranih uzimanja za terapiju
) {
    // Companion object sa konstante formatere umjesto instance varijabli
    companion object {
        private const val DATE_TIME_PATTERN = "dd-MM-yyyy HH:mm"
        private const val DATE_PATTERN = "dd-MM-yyyy"
        private const val TIME_PATTERN = "HH:mm"

        // Helper funkcije koje kreiraju formatere kad god su potrebni
        @JvmStatic
        fun createDateTimeFormat() = SimpleDateFormat(DATE_TIME_PATTERN, Locale.getDefault())
        @JvmStatic
        fun createDateFormat() = SimpleDateFormat(DATE_PATTERN, Locale.getDefault())
        @JvmStatic
        fun createTimeFormat() = SimpleDateFormat(TIME_PATTERN, Locale.getDefault())
    }

    // Dobij početno vrijeme kao Calendar
    private fun getStartCalendar(): Calendar {
        val calendar = Calendar.getInstance()
        if (startDateTime.isNotEmpty()) {
            try {
                calendar.time = createDateTimeFormat().parse(startDateTime) ?: Date()
            } catch (_: Exception) {
                // Fallback na trenutno vrijeme ako parsing fail-a
            }
        }
        return calendar
    }

    // Generiraj sva planirana vremena uzimanja za određeni dan
    fun generirajVremenaZaDan(date: String): List<String> {
        val vremena = mutableListOf<String>()
        val targetDate = createDateFormat().parse(date) ?: return emptyList()
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
            vremena.add(createTimeFormat().format(dayCal.time))
            dayCal.add(Calendar.HOUR_OF_DAY, intervalSati)
        }

        return vremena
    }

    // Sljedeće vrijeme uzimanja
    fun sljedeceVrijeme(): String? {
        val sada = Calendar.getInstance()
        val today = createDateFormat().format(Date())
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

    // Dobij compliance statistike
    fun getComplianceStats(days: Int = 30): ComplianceStats {
        val cutoffDate = Calendar.getInstance()
        cutoffDate.add(Calendar.DAY_OF_MONTH, -days)
        val cutoffString = createDateFormat().format(cutoffDate.time)

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
}

// Serializable entry for price history
@Serializable
data class PriceEntry(
    val timestamp: String, // format: dd-MM-yyyy HH:mm (uses IntervalnoUzimanje.createDateTimeFormat())
    val price: String // raw price string as entered (e.g., "12,34")
)

@OptIn(InternalSerializationApi::class)
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
    val doza: String = "",
    val pakiranje: Int = 30,
    var trenutnoStanje: Int = 30,
    val cijena: String = "",
    // New field: historical price entries (kept as list of snapshots). Backwards compatible default empty list.
    val cijenaHistorija: List<PriceEntry> = emptyList(),
    val tipUzimanja: TipUzimanja = TipUzimanja.STANDARDNO,
    val intervalnoUzimanje: IntervalnoUzimanje? = null,
    val complianceHistory: List<UzimanjeRecord> = emptyList(),
    val jutro: Boolean = false,
    val popodne: Boolean = false,
    val vecer: Boolean = false,
    val vrijemeJutro: String = "08:00",
    val vrijemePopodne: String = "14:00",
    val vrijemeVecer: String = "20:00",
    val dozeZaDan: MutableMap<DobaDana, Boolean> = mutableMapOf(),
    val napomene: String = "",
    val boja: String = "#4CAF50",
    val sortOrderJutro: Int = 0,
    val sortOrderPopodne: Int = 0,
    val sortOrderVecer: Int = 0
) {
    // Provjeri je li neki lijek za danas već uzet
    fun jeUzetZaDanas(): Boolean {
        return when (tipUzimanja) {
            TipUzimanja.INTERVALNO -> {
                intervalnoUzimanje?.let { interval ->
                    val today = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
                    interval.complianceHistory.any { it.date == today && it.actualTime != null }
                } ?: false
            }
            TipUzimanja.STANDARDNO -> {
                // Ako postoje zapisi u complianceHistory za današnji datum, smatra se da je lijek (ili barem jedna doza) uzet danas
                val today = IntervalnoUzimanje.createDateFormat().format(Date())
                complianceHistory.any { it.date == today && it.actualTime != null }
            }
        }
    }

    fun mozeUzeti(dobaDana: DobaDana?, vrijeme: String? = null, datum: String? = null): Boolean {
        // Onemogući dvostruko uzimanje za isti termin
        return when (tipUzimanja) {
            TipUzimanja.STANDARDNO -> {
                if (dobaDana == null) return false
                // Preferiramo provjeru prema complianceHistory (datum + scheduledTime) umjesto dozeZaDan map,
                // jer je to otpornije na propuste alarmnog resetiranja.
                val today = IntervalnoUzimanje.createDateFormat().format(Date())
                val scheduled = when (dobaDana) {
                    DobaDana.JUTRO -> vrijemeJutro
                    DobaDana.POPODNE -> vrijemePopodne
                    DobaDana.VECER -> vrijemeVecer
                }

                val alreadyTakenByHistory = complianceHistory.any { it.date == today && it.scheduledTime == scheduled && it.actualTime != null }
                if (alreadyTakenByHistory) return false

                // Backward-compatible fallback: ako nema zapis u complianceHistory, provjeri dozeZaDan map
                val mapFlag = dozeZaDan[dobaDana] == true
                !mapFlag && trenutnoStanje > 0
            }
            TipUzimanja.INTERVALNO -> vrijeme != null && complianceHistory.none { it.scheduledTime == vrijeme && it.date == datum } && trenutnoStanje > 0
        }
    }

    fun uzmiLijek(dobaDana: DobaDana?, vrijeme: String? = null, datum: String? = null, actualTime: String? = null): Lijek {
        if (!mozeUzeti(dobaDana, vrijeme, datum)) return this
        val novoStanje = (trenutnoStanje - 1).coerceAtLeast(0)
        return when (tipUzimanja) {
            TipUzimanja.STANDARDNO -> {
                val noveDoze = dozeZaDan.toMutableMap()
                if (dobaDana != null) noveDoze[dobaDana] = true

                // Record a UzimanjeRecord for standard doses
                val today = IntervalnoUzimanje.createDateFormat().format(Date())
                val scheduled = when (dobaDana) {
                    DobaDana.JUTRO -> vrijemeJutro
                    DobaDana.POPODNE -> vrijemePopodne
                    DobaDana.VECER -> vrijemeVecer
                    else -> vrijeme ?: IntervalnoUzimanje.createTimeFormat().format(Date())
                }
                val actual = actualTime ?: IntervalnoUzimanje.createTimeFormat().format(Date())

                // Determine lateness (>30 min)
                val isLate = try {
                    val sp = scheduled.split(":")
                    val ap = actual.split(":")
                    val scheduledMins = sp[0].toInt() * 60 + sp[1].toInt()
                    val actualMins = ap[0].toInt() * 60 + ap[1].toInt()
                    (actualMins - scheduledMins) > 30
                } catch (_: Exception) {
                    false
                }

                val newRecord = UzimanjeRecord(
                    scheduledTime = scheduled,
                    actualTime = actual,
                    isLate = isLate,
                    date = today,
                    priceAtTake = this.cijena.takeIf { it.isNotBlank() }
                )

                copy(
                    trenutnoStanje = novoStanje,
                    dozeZaDan = noveDoze,
                    complianceHistory = (complianceHistory + newRecord).takeLast(90)
                )
            }
            TipUzimanja.INTERVALNO -> {
                val now = actualTime ?: IntervalnoUzimanje.createTimeFormat().format(Date())
                val today = datum ?: IntervalnoUzimanje.createDateFormat().format(Date())
                val isLate = vrijeme != null && now > vrijeme
                val noviRecord = UzimanjeRecord(
                    scheduledTime = vrijeme ?: now,
                    actualTime = now,
                    isLate = isLate,
                    date = today,
                    priceAtTake = this.cijena.takeIf { it.isNotBlank() }
                )
                copy(trenutnoStanje = novoStanje, complianceHistory = (complianceHistory + noviRecord).takeLast(90))
            }
        }
    }
}
