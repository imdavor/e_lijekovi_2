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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource

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

// REMOVE this data class Lijek, use the main model instead
// import com.example.e_lijekovi_2.Lijek

@Composable
fun LijekCard(
    lijek: com.example.e_lijekovi_2.Lijek,
    onTake: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mozeUzeti = lijek.mozeUzeti(null)
    val jeUzet = lijek.jeUzetZaDanas()
    val zadnjeUzimanje = lijek.complianceHistory.lastOrNull { it.actualTime != null }
    val zadnjeUzimanjeTekst = zadnjeUzimanje?.let { "Zadnje: ${it.date} u ${it.actualTime}" } ?: "Nema podataka"
    // Ispravno dohvaćanje compliance statistike za intervalne lijekove
    val complianceStats = when (lijek.tipUzimanja) {
        com.example.e_lijekovi_2.TipUzimanja.INTERVALNO -> lijek.intervalnoUzimanje?.getComplianceStats(7)
        else -> null
    }
    val complianceText = complianceStats?.let { "Uzimanje: ${it.complianceRate.toInt()}% u zadnjih 7 dana" } ?: ""

    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(), // Uklonjen padding na dnu, padding treba dodati samo zadnjoj kartici u prikazu liste
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (lijek.trenutnoStanje <= 7) Color(0xFFFFF59D) else MaterialTheme.colorScheme.surface
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
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = "Lijek ikona",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            // Tekstualni podaci i gumb
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (lijek.tipUzimanja == com.example.e_lijekovi_2.TipUzimanja.INTERVALNO) {
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
                    if (!lijek.cijena.isNullOrBlank()) {
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
                        enabled = mozeUzeti && !jeUzet,
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
