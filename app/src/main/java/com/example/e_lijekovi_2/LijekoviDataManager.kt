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
                throw SerializationException("JSON validacija neuspješna: ${validation.message}")
            }

            val lijekovi = json.decodeFromString<List<Lijek>>(cleanedJson)
            Log.d(TAG, "Uspješno parsirano ${lijekovi.size} lijekova")

            lijekovi
        } catch (e: SerializationException) {
            Log.e(TAG, "Greška pri deserializaciji JSON-a: ${e.message}")
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Neočekivana greška pri parsiranju JSON-a: ${e.message}")
            throw SerializationException("Neočekivana greška pri parsiranju", e)
        }
    }

    /**
     * Čisti JSON string od mogućih problema
     */
    private fun cleanJsonString(jsonString: String): String {
        return jsonString
            .trim()
            .replace("\uFEFF", "") // Remove BOM if present
            .replace("\\n", "")
            .replace("\\r", "")
    }

    /**
     * Validira JSON string prije parsiranja
     */
    private fun validateJsonString(jsonString: String): ValidationResult {
        val trimmed = jsonString.trim()

        if (trimmed.isEmpty()) {
            return ValidationResult(false, "JSON string je prazan")
        }

        if (!trimmed.startsWith("[")) {
            return ValidationResult(false, "JSON ne počinje s '[' - nije array")
        }

        if (!trimmed.endsWith("]")) {
            return ValidationResult(false, "JSON ne završava s ']' - nije array")
        }

        // Osnovne provjere strukture
        val openBrackets = trimmed.count { it == '[' }
        val closeBrackets = trimmed.count { it == ']' }

        if (openBrackets != closeBrackets) {
            return ValidationResult(false, "Neispravna struktura zagrada: $openBrackets otvorenih, $closeBrackets zatvorenih")
        }

        return ValidationResult(true, "JSON izgleda ispravno")
    }

    /**
     * Rezultat validacije JSON-a
     */
    data class ValidationResult(
        val isValid: Boolean,
        val message: String
    )
}
