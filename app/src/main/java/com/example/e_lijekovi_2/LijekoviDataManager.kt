package com.example.e_lijekovi_2

import android.content.Context
import android.net.Uri
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object LijekoviDataManager {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true // omogućava import starijih verzija ako dodamo nova polja
    }

    /**
     * Eksportira listu lijekova u JSON format
     * Automatski uključuje sva polja iz Lijek data klase
     */
    fun exportToJson(lijekovi: List<Lijek>): String {
        return json.encodeToString(lijekovi)
    }

    /**
     * Importira listu lijekova iz JSON formata
     * Automatski prepoznaje sva polja, čak i ako su dodana nova
     */
    fun importFromJson(jsonString: String): List<Lijek> {
        return json.decodeFromString(jsonString)
    }

    /**
     * Sprema listu lijekova u datoteku (koristi Uri za pristup)
     */
    fun saveToFile(context: Context, uri: Uri, lijekovi: List<Lijek>): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                val jsonString = exportToJson(lijekovi)
                outputStream.write(jsonString.toByteArray())
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Učitava listu lijekova iz datoteke (koristi Uri za pristup)
     */
    fun loadFromFile(context: Context, uri: Uri): List<Lijek>? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                importFromJson(jsonString)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
