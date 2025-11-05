package com.example.e_lijekovi_2

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.SerializationException
import java.io.File
import java.io.FileNotFoundException

@Serializable
data class PurchaseRecord(
    val id: Long,
    val date: String, // dd-MM-yyyy
    val amount: Double, // in EUR
    val currency: String = "€",
    val note: String? = null,
    val lijekId: Int? = null
)

object PurchaseManager {
    private const val TAG = "PurchaseManager"
    private const val PURCHASES_FILE = "purchases_data.json"

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    fun saveToLocalStorage(context: Context, purchases: List<PurchaseRecord>): Boolean {
        return try {
            val file = File(context.filesDir, PURCHASES_FILE)
            val jsonString = json.encodeToString(purchases)
            file.writeText(jsonString)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed saving purchases", e)
            false
        }
    }

    fun loadFromLocalStorage(context: Context): List<PurchaseRecord> {
        return try {
            val file = File(context.filesDir, PURCHASES_FILE)
            if (!file.exists()) return emptyList()
            val jsonString = file.readText()
            if (jsonString.isBlank()) return emptyList()
            json.decodeFromString(jsonString)
        } catch (e: FileNotFoundException) {
            Log.w(TAG, "Purchases file not found: ${e.message}")
            emptyList()
        } catch (e: SerializationException) {
            Log.e(TAG, "Failed to parse purchases JSON", e)
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error loading purchases", e)
            emptyList()
        }
    }

    @Suppress("unused")
    fun saveToFile(context: Context, uri: Uri, purchases: List<PurchaseRecord>): Boolean {
        return try {
            val jsonString = json.encodeToString(purchases)
            context.contentResolver.openOutputStream(uri)?.use { os ->
                os.write(jsonString.toByteArray())
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed export purchases", e)
            false
        }
    }

    @Suppress("unused")
    fun loadFromFile(context: Context, uri: Uri): List<PurchaseRecord>? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                if (jsonString.isBlank()) return emptyList()
                json.decodeFromString(jsonString)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed import purchases", e)
            null
        }
    }
}
