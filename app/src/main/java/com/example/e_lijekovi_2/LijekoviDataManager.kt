package com.example.e_lijekovi_2

import android.content.Context
import android.net.Uri
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object LijekoviDataManager {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true // omogućava import starijih verzija ako dodamo nova polja
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
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val jsonString = inputStream.bufferedReader().readText()
                importFromJson(jsonString)
            }
        } catch (e: Exception) {
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
        return Json.decodeFromString<List<Lijek>>(jsonString)
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
}
