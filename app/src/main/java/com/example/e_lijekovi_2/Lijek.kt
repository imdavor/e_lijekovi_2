@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)

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
data class IntervalnoUzimanje(
    val intervalSati: Int, // Svakih koliko sati
    val prvoUzimanjeSat: Int, // Sat prvog uzimanja (0-23)
    val prvoUzimanjeMinute: Int = 0, // Minute prvog uzimanja (0-59)
    val trajanjeDana: Int = 7, // Koliko dana se uzima lijek
    val datumPocetka: String = "" // Umjesto direktne evaluacije, koristit ćemo prazan string kao default
) {
    // Generiraj vremena uzimanja za današnji dan
    fun generirajVremenaZaDanas(): List<String> {
        val vremena = mutableListOf<String>()
        val calendar = Calendar.getInstance()

        // Postavi vrijeme na početak današnjeg dana s prvim uzimanjem
        calendar.set(Calendar.HOUR_OF_DAY, prvoUzimanjeSat)
        calendar.set(Calendar.MINUTE, prvoUzimanjeMinute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val krajDana = Calendar.getInstance()
        krajDana.set(Calendar.HOUR_OF_DAY, 23)
        krajDana.set(Calendar.MINUTE, 59)
        krajDana.set(Calendar.SECOND, 59)

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        while (calendar.timeInMillis <= krajDana.timeInMillis) {
            vremena.add(timeFormat.format(calendar.time))
            calendar.add(Calendar.HOUR_OF_DAY, intervalSati)
        }

        return vremena
    }

    // Sljedeće vrijeme uzimanja
    fun sljedeceVrijeme(): String? {
        val sada = Calendar.getInstance()
        val vremenaZaDanas = generirajVremenaZaDanas()
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

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
}

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
