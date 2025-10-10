package com.example.e_lijekovi_2

import kotlinx.serialization.Serializable
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
@Serializable
enum class DobaDana {
    JUTRO, POPODNE, VECER
}

@Serializable
data class Lijek(
    val id: Int,
    var naziv: String,
    var dobaDana: List<DobaDana>, // promijenjeno iz DobaDana u List<DobaDana>
    var pakiranje: Int = 30, // broj komada u pakiranju
    var trenutnoStanje: Int = 0, // trenutna količina
    var slikaUrl: String = "" // URL ili resource ID slike
) {
    fun trebaLiNaruciti(): Boolean = trenutnoStanje <= 7

    fun dodajPakiranje() {
        trenutnoStanje += pakiranje
    }

    fun uzmiLijek() {
        if (trenutnoStanje > 0) {
            trenutnoStanje--
        }
    }
}
