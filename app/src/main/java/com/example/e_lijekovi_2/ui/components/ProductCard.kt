@file:Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE", "UNUSED_IMPORT", "REDUNDANT_QUALIFIER", "UNUSED_VALUE")

package com.example.e_lijekovi_2.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_lijekovi_2.DobaDana
import com.example.e_lijekovi_2.IntervalnoUzimanje
import com.example.e_lijekovi_2.Lijek
import com.example.e_lijekovi_2.TipUzimanja

// Prilagođeni model proizvoda
// Po potrebi proširi ili promijeni polja

data class Product(
    val id: Int,
    val name: String,
    val price: String,
    val subtitle: String = "",
    val colorHex: String = "#F1F8E9" // fallback boja
)

@Composable
fun ProductCard(
    product: Product,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    var added by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (added) 1.04f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 250f),
    )

    Card(
        modifier = modifier
            .width(170.dp)
            .wrapContentHeight()
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
        ) {
            // Placeholder za sliku (zamijeni Coil/AsyncImage po potrebi)
            Surface(
                modifier = Modifier
                    .height(110.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                tonalElevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "Slika",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = product.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2
            )
            if (product.subtitle.isNotEmpty()) {
                Text(
                    text = product.subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = product.price,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Button(
                    onClick = {
                        added = true
                        onAdd()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.AddShoppingCart, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun LijekCard(
    lijek: Lijek,
    onTake: () -> Unit,
    modifier: Modifier = Modifier
) {
    // remove global jeUzet that hides per-dose availability; rely on mozeUzeti for each button enablement
    // val jeUzet = lijek.jeUzetZaDanas()
    // restore compliance stats text
    val complianceStats = when (lijek.tipUzimanja) {
        TipUzimanja.INTERVALNO -> lijek.intervalnoUzimanje?.getComplianceStats(7)
        else -> null
    }
    val complianceText = complianceStats?.let { "Uzimanje: ${it.complianceRate.toInt()}% u zadnjih 7 dana" } ?: ""

    val mozeUzeti = when (lijek.tipUzimanja) {
        TipUzimanja.INTERVALNO -> {
            val ih = lijek.intervalnoUzimanje
            val next = ih?.sljedeceVrijeme()
            ih != null && next != null && lijek.trenutnoStanje > 0 && ih.complianceHistory.none { it.scheduledTime == next && it.date == IntervalnoUzimanje.createDateFormat().format(java.util.Date()) }
        }
        TipUzimanja.STANDARDNO -> {
            val moguJutro = lijek.jutro && (lijek.dozeZaDan[DobaDana.JUTRO] != true) && lijek.trenutnoStanje > 0
            val moguPodne = lijek.popodne && (lijek.dozeZaDan[DobaDana.POPODNE] != true) && lijek.trenutnoStanje > 0
            val moguVecer = lijek.vecer && (lijek.dozeZaDan[DobaDana.VECER] != true) && lijek.trenutnoStanje > 0
            (moguJutro || moguPodne || moguVecer)
        }
    }

    // Compute last-taking text: prefer actual complianceHistory; for STANDARDNO if none, show which doze are still available today
    val zadnjeUzimanjeTekst = run {
        val zadnje = lijek.complianceHistory.lastOrNull { it.actualTime != null }
        if (zadnje != null) {
            "Zadnje: ${zadnje.date} u ${zadnje.actualTime}"
        } else {
            if (lijek.tipUzimanja == TipUzimanja.STANDARDNO) {
                val available = mutableListOf<String>()
                if (lijek.jutro && (lijek.dozeZaDan[DobaDana.JUTRO] != true)) available.add("Jutro ${lijek.vrijemeJutro}")
                if (lijek.popodne && (lijek.dozeZaDan[DobaDana.POPODNE] != true)) available.add("Podne ${lijek.vrijemePopodne}")
                if (lijek.vecer && (lijek.dozeZaDan[DobaDana.VECER] != true)) available.add("Večer ${lijek.vrijemeVecer}")
                if (available.isNotEmpty()) "Dostupno: ${available.joinToString(", ")}" else "Nema podataka"
            } else {
                "Nema podataka"
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(), // Uklonjen padding na dnu, padding treba dodati samo zadnjoj kartici u prikazu liste
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = run {
                // parse unitsPerDose from `doza` (first integer found), default 1
                val unitsPerDose = lijek.doza.trim().let { d -> Regex("\\d+").find(d)?.value?.toIntOrNull() ?: 1 }

                val dosesPerDay = when (lijek.tipUzimanja) {
                    TipUzimanja.STANDARDNO -> {
                        val stdTimes = listOf(lijek.jutro, lijek.popodne, lijek.vecer).count { it }
                        if (stdTimes > 0) stdTimes.toDouble() else 1.0
                    }
                    TipUzimanja.INTERVALNO -> {
                        val interval = lijek.intervalnoUzimanje?.intervalSati ?: 0
                        if (interval > 0) 24.0 / interval else 1.0
                    }
                }

                val dailyConsumption = unitsPerDose * dosesPerDay
                val daysRemaining = if (dailyConsumption > 0.0) lijek.trenutnoStanje.toDouble() / dailyConsumption else Double.POSITIVE_INFINITY

                val isYellow = (lijek.trenutnoStanje <= 7) || (dosesPerDay >= 2.0 && lijek.trenutnoStanje <= 14) || (daysRemaining <= 7.0)

                if (isYellow) Color(0xFFFFF59D) else MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Kvadratić sa slikom (ikonom)
            Surface(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(10.dp)),
                tonalElevation = 2.dp,
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                // Show icon and small dose pattern underneath to visually indicate which times are still available today
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = "Lijek ikona",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(32.dp)
                    )

                    // New: render three small circles instead of text like "1x0x1"
                    if (lijek.tipUzimanja == TipUzimanja.STANDARDNO) {
                        // Indicator should always show schedule, not whether dose was taken.
                        val activeJ = lijek.jutro
                        val activeP = lijek.popodne
                        val activeV = lijek.vecer

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val activeColor = Color(0xFF2E7D32) // green
                            val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)

                            Box(modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (activeJ) activeColor else inactiveColor))

                            Box(modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (activeP) activeColor else inactiveColor))

                            Box(modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (activeV) activeColor else inactiveColor))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            // Tekstualni podaci i gumb
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (lijek.tipUzimanja == TipUzimanja.INTERVALNO) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Intervalni lijek",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp).padding(end = 4.dp)
                        )
                    }
                    Text(
                        text = lijek.naziv,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        modifier = Modifier.weight(1f)
                    )
                    if (lijek.cijena.isNotBlank()) {
                        val cijenaFormatted = try {
                            lijek.cijena.replace(',', '.').toFloat().let { String.format("%.2f €", it) }
                        } catch (_: Exception) {
                            lijek.cijena + " €"
                        }
                        Text(
                            text = cijenaFormatted.replace('.', ','),
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            modifier = Modifier
                        )
                    }
                }
                if (lijek.napomene.isNotBlank()) {
                    Text(
                        text = lijek.napomene,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = zadnjeUzimanjeTekst,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (complianceText.isNotBlank()) {
                    Text(
                        text = complianceText,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Stanje lijevo
                    Text(
                        text = "${lijek.trenutnoStanje} od ${lijek.pakiranje}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Gumb desno
                    Button(
                        onClick = onTake,
                        enabled = mozeUzeti,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                    ) {
                        Text("✓ Uzmi")
                    }
                }
            }
        }
    }
}
