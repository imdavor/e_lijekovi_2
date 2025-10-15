package com.example.e_lijekovi_2

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.SerializationException
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

object LijekoviDataManager {
    private const val TAG = "LijekoviDataManager"
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true // omogućava import starijih verzija ako dodamo nova polja
        coerceInputValues = true // pokušava popraviti neispravne vrijednosti
        isLenient = true // tolerira jednostruke navodnike i ostale greške
    }

    private const val LOCAL_FILE_NAME = "lijekovi_data.json"

    /**
     * Sprema listu lijekova u internu memoriju aplikacije
     */
    fun saveToLocalStorage(context: Context, lijekovi: List<Lijek>): Boolean {
        return try {
            val file = File(context.filesDir, LOCAL_FILE_NAME)
            val jsonString = exportToJson(lijekovi)
            file.writeText(jsonString)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Učitava listu lijekova iz interne memorije aplikacije
     */
    fun loadFromLocalStorage(context: Context): List<Lijek>? {
        return try {
            val file = File(context.filesDir, LOCAL_FILE_NAME)
            if (!file.exists()) {
                return emptyList()
            }
            val jsonString = file.readText()
            if (jsonString.isEmpty()) {
                return emptyList()
            }
            importFromJson(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Sprema listu lijekova u specifičnu datoteku preko URI
     */
    fun saveToFile(context: Context, uri: Uri, lijekovi: List<Lijek>): Boolean {
        return try {
            val jsonString = exportToJson(lijekovi)
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(jsonString.toByteArray())
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Učitava listu lijekova iz specifične datoteke preko URI
     */
    fun loadFromFile(context: Context, uri: Uri): List<Lijek>? {
        return try {
            Log.d(TAG, "Pokušavam učitati datoteku: $uri")

            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val jsonString = inputStream.bufferedReader().use { it.readText() }

                Log.d(TAG, "Učitan JSON string duljine: ${jsonString.length}")
                Log.d(TAG, "Prvih 200 znakova: ${jsonString.take(200)}")

                // Provjeri je li JSON prazan
                if (jsonString.trim().isEmpty()) {
                    Log.e(TAG, "JSON string je prazan!")
                    return null
                }

                // Provjeri počinje li i završava li s [ ]
                val trimmed = jsonString.trim()
                if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) {
                    Log.e(TAG, "JSON ne izgleda kao array! Počinje s: ${trimmed.take(10)}, završava s: ${trimmed.takeLast(10)}")
                    return null
                }

                val result = importFromJson(jsonString)
                Log.d(TAG, "Uspješno učitano ${result.size} lijekova")
                result
            } ?: run {
                Log.e(TAG, "Ne mogu otvoriti input stream za URI: $uri")
                null
            }
        } catch (e: FileNotFoundException) {
            Log.e(TAG, "Datoteka nije pronađena: $uri", e)
            null
        } catch (e: IOException) {
            Log.e(TAG, "Greška pri čitanju datoteke: $uri", e)
            null
        } catch (e: SerializationException) {
            Log.e(TAG, "Greška pri deserializaciji JSON-a", e)
            Log.e(TAG, "JSON možda nije u ispravnom formatu za Lijek klasu")
            null
        } catch (e: SecurityException) {
            Log.e(TAG, "Nema permisije za čitanje datoteke: $uri", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Neočekivana greška pri učitavanju iz datoteke", e)
            Log.e(TAG, "Tip greške: ${e.javaClass.simpleName}")
            Log.e(TAG, "Poruka: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    /**
     * Eksportira listu lijekova u JSON string
     */
    private fun exportToJson(lijekovi: List<Lijek>): String {
        return json.encodeToString(lijekovi)
    }

    /**
     * Importira listu lijekova iz JSON stringa
     */
    private fun importFromJson(jsonString: String): List<Lijek> {
        return try {
            Log.d(TAG, "Pokušavam parsirati JSON...")

            // Prvo očisti JSON string od mogućih problema
            val cleanedJson = cleanJsonString(jsonString)
            Log.d(TAG, "Očišćen JSON duljine: ${cleanedJson.length}")

            // Validacija prije parsiranja
            val validation = validateJsonString(cleanedJson)
            if (!validation.isValid) {
                Log.e(TAG, "JSON validacija neuspješna: ${validation.message}")
                throw SerializationException("Nevažeći JSON format: ${validation.message}")
            }

            val result = json.decodeFromString<List<Lijek>>(cleanedJson)

            // Dodatna provjera rezultata
            result.forEach { lijek ->
                Log.d(TAG, "Učitan lijek: ${lijek.naziv} (ID: ${lijek.id})")
                if (lijek.naziv.isBlank()) {
                    Log.w(TAG, "UPOZORENJE: Lijek s praznim nazivom!")
                }
            }

            result
        } catch (e: SerializationException) {
            Log.e(TAG, "SerializationException pri parsiranju JSON-a", e)
            Log.e(TAG, "Možda nedostaju polja ili su u krivom formatu")
            Log.e(TAG, "JSON fragment oko greške: ${extractErrorContext(jsonString, e.message)}")
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Opća greška pri parsiranju JSON-a", e)
            throw e
        }
    }

    /**
     * Čisti JSON string od čestih problema
     */
    private fun cleanJsonString(jsonString: String): String {
        var cleaned = jsonString.trim()

        // Ukloni BOM (Byte Order Mark) ako postoji
        if (cleaned.startsWith("\uFEFF")) {
            cleaned = cleaned.substring(1)
        }

        // Popravi česti problem sa ']rue' umesto 'true'
        cleaned = cleaned.replace("]rue", "true")
        cleaned = cleaned.replace("]alse", "false")

        // Popravi duplirane znakove na krajevima
        while (cleaned.endsWith("]]") && cleaned.count { it == ']' } > cleaned.count { it == '[' }) {
            cleaned = cleaned.dropLast(1)
        }

        // Provjeri da li postoje nepotrebni znakovi nakon zatvaranja JSON-a
        val lastBracket = cleaned.lastIndexOf(']')
        if (lastBracket != -1 && lastBracket < cleaned.length - 1) {
            val afterBracket = cleaned.substring(lastBracket + 1).trim()
            if (afterBracket.isNotEmpty()) {
                Log.w(TAG, "Pronađeni nepotrebni znakovi nakon JSON-a: '$afterBracket'")
                cleaned = cleaned.substring(0, lastBracket + 1)
            }
        }

        return cleaned
    }

    /**
     * Izvuci kontekst oko greške za debugging
     */
    private fun extractErrorContext(jsonString: String, errorMessage: String?): String {
        return try {
            // Pokušaj pronaći offset iz error message
            val offsetMatch = Regex("offset (\\d+)").find(errorMessage ?: "")
            val offset = offsetMatch?.groupValues?.get(1)?.toIntOrNull()

            if (offset != null && offset < jsonString.length) {
                val start = maxOf(0, offset - 50)
                val end = minOf(jsonString.length, offset + 50)
                val context = jsonString.substring(start, end)
                "...${context}... (around position $offset)"
            } else {
                "Last 100 characters: ${jsonString.takeLast(100)}"
            }
        } catch (e: Exception) {
            "Cannot extract error context: ${e.message}"
        }
    }

    /**
     * Briše sve lokalno spremljene podatke
     */
    @Suppress("unused")
    fun clearLocalStorage(context: Context): Boolean {
        return try {
            val file = File(context.filesDir, LOCAL_FILE_NAME)
            if (file.exists()) {
                file.delete()
            } else {
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Proverava da li postoje lokalno spremljeni podaci
     */
    @Suppress("unused")
    fun hasLocalData(context: Context): Boolean {
        val file = File(context.filesDir, LOCAL_FILE_NAME)
        return file.exists() && file.length() > 0
    }

    /**
     * Validira JSON string prije importa
     */
    fun validateJsonString(jsonString: String): ValidationResult {
        if (jsonString.trim().isEmpty()) {
            return ValidationResult(false, "JSON string je prazan")
        }

        val trimmed = jsonString.trim()
        if (!trimmed.startsWith("[")) {
            return ValidationResult(false, "JSON ne počinje s '[' - nije array")
        }

        if (!trimmed.endsWith("]")) {
            return ValidationResult(false, "JSON ne završava s ']' - nije array")
        }

        return try {
            // Jednostavna provjera - pokušaj parsirati JSON sa Kotlin Serialization
            json.decodeFromString<List<Lijek>>(trimmed)
            ValidationResult(true, "JSON je valjan")
        } catch (e: SerializationException) {
            ValidationResult(false, "Greška deserializacije: ${e.message}")
        } catch (e: Exception) {
            ValidationResult(false, "Neočekivana greška: ${e.message}")
        }
    }

    data class ValidationResult(
        val isValid: Boolean,
        val message: String
    )
}
