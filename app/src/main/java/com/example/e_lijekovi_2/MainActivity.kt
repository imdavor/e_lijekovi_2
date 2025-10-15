package com.example.e_lijekovi_2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.zIndex
import androidx.compose.animation.core.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Context
import androidx.compose.animation.animateColorAsState
import com.example.e_lijekovi_2.ui.theme.E_lijekovi_2Theme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            E_lijekovi_2Theme {
                PocetniEkran(context = this)
            }
        }
    }
}

// Komponenta za zaglavlje vremenske grupe
@Composable
fun TimeGroupHeader(
    label: String,
    time: String,
    count: Int,
    emoji: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = emoji,
                    style = MaterialTheme.typography.headlineMedium
                )

                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = time,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = androidx.compose.foundation.shape.CircleShape
            ) {
                Text(
                    text = count.toString(),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

// Komponenta za standardnu karticu lijeka
@Composable
fun LijekCard(
    lijek: Lijek,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onRefill: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLowStock = lijek.trenutnoStanje <= 7
    var offsetX by remember { mutableStateOf(0f) }
    val maxSwipeDistance = 200f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .offset { IntOffset(offsetX.roundToInt(), 0) }
            .pointerInput(lijek.id) {
                detectDragGesturesAfterLongPress(
                    onDragEnd = {
                        when {
                            offsetX < -maxSwipeDistance -> {
                                // Swipe lijevo = brisanje
                                onDelete()
                                offsetX = 0f
                            }
                            offsetX > maxSwipeDistance -> {
                                // Swipe desno = dodaj terapiju
                                onRefill()
                                offsetX = 0f
                            }
                            else -> offsetX = 0f
                        }
                    },
                    onDragCancel = { offsetX = 0f }
                ) { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    // Ograniči swipe na maksimalnu udaljenost
                    offsetX = offsetX.coerceIn(-maxSwipeDistance * 1.5f, maxSwipeDistance * 1.5f)
                }
            }
            .clickable { onEdit() }, // Klik na cijelu karticu otvara edit
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                offsetX < -50f -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f) // Crveno za brisanje
                offsetX > 50f -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) // Plavo za terapiju
                isLowStock -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = lijek.naziv,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (isLowStock) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.padding(0.dp)
                    ) {
                        Text(
                            text = "⚠️ NARUČI",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onError,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                text = lijek.doza,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Prikaz trenutnog stanja
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "💊 Stanje:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${lijek.trenutnoStanje}/${lijek.pakiranje}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = if (isLowStock)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary
                )
            }

            if (lijek.napomene.isNotEmpty()) {
                Text(
                    text = lijek.napomene,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Prikaz swipe hintova
            if (offsetX < -50f) {
                Text(
                    text = "← Swipe za brisanje",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp)
                )
            } else if (offsetX > 50f) {
                Text(
                    text = "Swipe za novu terapiju →",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

// Komponenta za intervalne lijekove
@Composable
fun IntervalLijekCard(
    lijek: Lijek,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDoseTaken: (String, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onEdit() }, // Klik na cijelu karticu otvara edit
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Uklonjen Row s IconButton za edit
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = lijek.naziv,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                lijek.intervalnoUzimanje?.let { interval ->
                    Text(
                        text = "⏰ Svaki ${interval.intervalSati}h",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    val nextTime = interval.sljedeceVrijeme()
                    if (nextTime != null) {
                        Text(
                            text = "🕐 Sljedeće: $nextTime",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            lijek.intervalnoUzimanje?.let { interval ->
                val todayTimes = interval.generirajVremenaZaDanas()
                if (todayTimes.isNotEmpty()) {
                    Text(
                        text = "Danas:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    todayTimes.forEach { scheduledTime ->
                        val isTaken = interval.jeDozuUzetaDanas(scheduledTime)
                        val currentTime = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
                        val isLate = !isTaken && currentTime > scheduledTime

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = scheduledTime,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = when {
                                        isTaken -> MaterialTheme.colorScheme.primary
                                        isLate -> MaterialTheme.colorScheme.error
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                )

                                when {
                                    isTaken -> Text("✅", style = MaterialTheme.typography.bodySmall)
                                    isLate -> Text("⏰", style = MaterialTheme.typography.bodySmall)
                                    else -> Text("⏳", style = MaterialTheme.typography.bodySmall)
                                }
                            }

                            if (!isTaken) {
                                Button(
                                    onClick = { onDoseTaken(scheduledTime, null) },
                                    modifier = Modifier.height(32.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp)
                                ) {
                                    Text(
                                        "Uzmi",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }

            lijek.intervalnoUzimanje?.let { interval ->
                val stats = interval.getComplianceStats(7)
                if (stats.totalScheduled > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "7 dana: ${stats.totalTaken}/${stats.totalScheduled}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${String.format("%.0f", stats.complianceRate)}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = when {
                                stats.complianceRate >= 90 -> MaterialTheme.colorScheme.primary
                                stats.complianceRate >= 75 -> Color(0xFFFF9800)
                                else -> MaterialTheme.colorScheme.error
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// Dialog za dodavanje/uređivanje lijeka
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LijekDialog(
    lijek: Lijek?,
    existingLijekovi: List<Lijek>,
    onDismiss: () -> Unit,
    onSave: (Lijek) -> Unit
) {
    var naziv by remember { mutableStateOf(lijek?.naziv ?: "") }
    var doza by remember { mutableStateOf(lijek?.doza ?: "") }
    var jutro by remember { mutableStateOf(lijek?.jutro ?: false) }
    var popodne by remember { mutableStateOf(lijek?.popodne ?: false) }
    var vecer by remember { mutableStateOf(lijek?.vecer ?: false) }
    var pakiranje by remember { mutableStateOf(lijek?.pakiranje?.toString() ?: "30") }
    var trenutnoStanje by remember { mutableStateOf(lijek?.trenutnoStanje?.toString() ?: "30") }

    // Nova polja za terapiju
    var showTerapijaSection by remember { mutableStateOf(false) }
    var intervalSati by remember { mutableStateOf("8") }
    var trajanjeDana by remember { mutableStateOf("7") }
    var startDatum by remember { mutableStateOf("") }
    var startVrijeme by remember { mutableStateOf("08:00") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (lijek == null) "Dodaj lijek" else "Uredi lijek") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = naziv,
                    onValueChange = { naziv = it },
                    label = { Text("Naziv lijeka") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = doza,
                    onValueChange = { doza = it },
                    label = { Text("Doza") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = pakiranje,
                        onValueChange = { pakiranje = it },
                        label = { Text("Pakiranje") },
                        placeholder = { Text("30") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = trenutnoStanje,
                        onValueChange = { trenutnoStanje = it },
                        label = { Text("Trenutno stanje") },
                        placeholder = { Text("30") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Text("Vrijeme uzimanja:", fontWeight = FontWeight.Medium)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Toggle kocka za jutro
                    Card(
                        onClick = { jutro = !jutro },
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = if (jutro) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (jutro) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = if (jutro) 6.dp else 2.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🌞", fontSize = 24.sp)
                            Text("Jutro", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    // Toggle kocka za podne
                    Card(
                        onClick = { popodne = !popodne },
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = if (popodne) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (popodne) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = if (popodne) 6.dp else 2.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("☀️", fontSize = 24.sp)
                            Text("Podne", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    // Toggle kocka za večer
                    Card(
                        onClick = { vecer = !vecer },
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = if (vecer) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (vecer) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = if (vecer) 6.dp else 2.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🌙", fontSize = 24.sp)
                            Text("Večer", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                // Dodano za novu terapiju
                if (lijek != null) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "⏰ Dodaj novu intervalnu terapiju",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                IconButton(
                                    onClick = { showTerapijaSection = !showTerapijaSection }
                                ) {
                                    Icon(
                                        if (showTerapijaSection) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = if (showTerapijaSection) "Sakrij" else "Prikaži"
                                    )
                                }
                            }

                            AnimatedVisibility(visible = showTerapijaSection) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        "Konfiguriraj novu intervalnu terapiju:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = intervalSati,
                                            onValueChange = { intervalSati = it },
                                            label = { Text("Interval (sati)") },
                                            placeholder = { Text("8") },
                                            modifier = Modifier.weight(1f)
                                        )
                                        OutlinedTextField(
                                            value = trajanjeDana,
                                            onValueChange = { trajanjeDana = it },
                                            label = { Text("Trajanje (dana)") },
                                            placeholder = { Text("7") },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = startDatum,
                                            onValueChange = { startDatum = it },
                                            label = { Text("Početni datum") },
                                            placeholder = { Text("dd-MM-yyyy (opciono)") },
                                            modifier = Modifier.weight(1f)
                                        )
                                        OutlinedTextField(
                                            value = startVrijeme,
                                            onValueChange = { startVrijeme = it },
                                            label = { Text("Početno vrijeme") },
                                            placeholder = { Text("08:00") },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            val intervalSatiInt = intervalSati.toIntOrNull() ?: 8
                                            val trajanjeDanaInt = trajanjeDana.toIntOrNull() ?: 7

                                            val startDateTime = if (startDatum.isNotBlank()) {
                                                "$startDatum $startVrijeme"
                                            } else {
                                                // Trenutni datum s unesenim vremenom
                                                val today = java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault()).format(java.util.Date())
                                                "$today $startVrijeme"
                                            }

                                            val novaIntervalnaTerapija = IntervalnoUzimanje(
                                                intervalSati = intervalSatiInt,
                                                startDateTime = startDateTime,
                                                trajanjeDana = trajanjeDanaInt,
                                                complianceHistory = emptyList()
                                            )

                                            val updatedLijek = lijek.copy(
                                                tipUzimanja = TipUzimanja.INTERVALNO,
                                                intervalnoUzimanje = novaIntervalnaTerapija,
                                                // Resetuj standardna vremena kad prebacujemo na intervalno
                                                jutro = false,
                                                popodne = false,
                                                vecer = false
                                            )

                                            onSave(updatedLijek)
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Text("🎯 Pokreni intervalnu terapiju")
                                    }

                                    // Pomoć i objašnjenja
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp)
                                        ) {
                                            Text(
                                                "💡 Objašnjenje:",
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                "• Interval: Svakih koliko sati se uzima lijek\n" +
                                                "• Trajanje: Koliko dana traje terapija\n" +
                                                "• Datum: Ako se ostavi prazno, počinje danas\n" +
                                                "• Vrijeme: Prvo uzimanje u danu",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Prikaz trenutne intervalne terapije ako postoji
                    if (lijek.tipUzimanja == TipUzimanja.INTERVALNO && lijek.intervalnoUzimanje != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    "📋 Trenutna intervalna terapija:",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                val interval = lijek.intervalnoUzimanje!!
                                Text("⏰ Svakih ${interval.intervalSati} sati")
                                Text("📅 Trajanje: ${interval.trajanjeDana} dana")
                                if (interval.startDateTime.isNotEmpty()) {
                                    Text("🚀 Počinje: ${interval.startDateTime}")
                                }
                                val stats = interval.getComplianceStats(7)
                                if (stats.totalScheduled > 0) {
                                    Text("📊 Compliance (7 dana): ${stats.totalTaken}/${stats.totalScheduled} (${String.format("%.0f", stats.complianceRate)}%)")
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (naziv.isNotBlank() && doza.isNotBlank()) {
                        val noviLijek = Lijek(
                            id = lijek?.id ?: 0,
                            naziv = naziv.trim(),
                            doza = doza.trim(),
                            napomene = lijek?.napomene ?: "", // Keep existing napomene or empty string
                            jutro = jutro,
                            popodne = popodne,
                            vecer = vecer,
                            pakiranje = pakiranje.toIntOrNull() ?: 30,
                            trenutnoStanje = trenutnoStanje.toIntOrNull() ?: 30,
                            // Kopiraj ostala postojeća polja ako uređujemo lijek
                            boja = lijek?.boja ?: "#4CAF50",
                            tipUzimanja = lijek?.tipUzimanja ?: TipUzimanja.STANDARDNO,
                            vrijemeJutro = lijek?.vrijemeJutro ?: "08:00",
                            vrijemePopodne = lijek?.vrijemePopodne ?: "14:00",
                            vrijemeVecer = lijek?.vrijemeVecer ?: "20:00",
                            intervalnoUzimanje = lijek?.intervalnoUzimanje,
                            sortOrderJutro = lijek?.sortOrderJutro ?: 0,
                            sortOrderPopodne = lijek?.sortOrderPopodne ?: 0,
                            sortOrderVecer = lijek?.sortOrderVecer ?: 0
                        )
                        onSave(noviLijek)
                    }
                }
            ) {
                Text("Spremi")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Odustani")
            }
        }
    )
}

@Composable
fun StatisticsScreen(
    lijekovi: List<Lijek>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Statistike i compliance",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "📊 Osnovne statistike",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text("Ukupno lijekova: ${lijekovi.size}")
                Text("Jutarnji lijekovi: ${lijekovi.count { it.jutro }}")
                Text("Popodnevni lijekovi: ${lijekovi.count { it.popodne }}")
                Text("Večernji lijekovi: ${lijekovi.count { it.vecer }}")
                Text("Intervalni lijekovi: ${lijekovi.count { it.tipUzimanja == TipUzimanja.INTERVALNO }}")
            }
        }
    }
}

@Composable
fun SettingsScreen(
    onExportImport: () -> Unit,
    onTestImport: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Postavke",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = onExportImport,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Upravljanje podacima")
        }

        Button(
            onClick = onTestImport,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("🔍 Test Import funkcionalnosti")
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "ℹ️ Pomoć za Import",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    "• Datoteka mora biti .json format\n" +
                    "• JSON mora biti array [ ... ]\n" +
                    "• Struktura mora odgovarati Lijek klasi\n" +
                    "• Koristite 'Test Import' za provjeru",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun AboutScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "O aplikaciji",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "e-LijekoviHR",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text("Verzija: 1.0")
                Text("Hrvatska aplikacija za praćenje lijekova")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Značajke:")
                Text("• Praćenje standardnih lijekova")
                Text("• Napredni intervalni doziranje")
                Text("• Compliance statistike")
                Text("• Export/Import podataka")
            }
        }
    }
}

@Composable
fun HomeScreen(
    lijekovi: List<Lijek>,
    onEditLijek: (Lijek) -> Unit,
    onDeleteLijek: (Lijek) -> Unit,
    onRefillLijek: (Lijek) -> Unit,
    onReorder: (DobaDana, Int, Int) -> Unit, // (grupa, fromId, toId)
    onDoseTaken: (Lijek, String, String?) -> Unit = { _, _, _ -> }, // (lijek, scheduledTime, actualTime)
    modifier: Modifier = Modifier
) {
    if (lijekovi.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.LocalPharmacy, // Zamenio Medication sa LocalPharmacy
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Nemate dodane lijekove",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Pritisnite + za dodavanje novog lijeka",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Grupiraj lijekove
            val jutarnjiLijekovi = lijekovi.filter { it.jutro && it.tipUzimanja == TipUzimanja.STANDARDNO }
                .sortedBy { it.sortOrderJutro }
            val popodnevniLijekovi = lijekovi.filter { it.popodne && it.tipUzimanja == TipUzimanja.STANDARDNO }
                .sortedBy { it.sortOrderPopodne }
            val vecernjiLijekovi = lijekovi.filter { it.vecer && it.tipUzimanja == TipUzimanja.STANDARDNO }
                .sortedBy { it.sortOrderVecer }
            val intervalniLijekovi = lijekovi.filter { it.tipUzimanja == TipUzimanja.INTERVALNO }

            // 🌞 JUTRO grupa
            if (jutarnjiLijekovi.isNotEmpty()) {
                TimeGroupHeader(
                    label = "Jutro",
                    time = "08:00",
                    count = jutarnjiLijekovi.size,
                    emoji = "🌞"
                )
                Spacer(modifier = Modifier.height(8.dp))

                jutarnjiLijekovi.forEachIndexed { index, lijek ->
                    var dy by remember(lijek.id) { mutableStateOf(0f) }
                    val threshold = 30f
                    WalletStyleCard(
                        lijek = lijek,
                        onEdit = { onEditLijek(lijek) },
                        onDelete = { onDeleteLijek(lijek) },
                        onRefill = { onRefillLijek(lijek) },
                        modifier = Modifier
                            .pointerInput(lijek.id) {
                                detectDragGesturesAfterLongPress(
                                    onDragEnd = { dy = 0f },
                                    onDragCancel = { dy = 0f }
                                ) { change, dragAmount ->
                                    change.consume()
                                    dy += dragAmount.y
                                    if (dy <= -threshold && index > 0) {
                                        val prevId = jutarnjiLijekovi[index - 1].id
                                        onReorder(DobaDana.JUTRO, lijek.id, prevId)
                                        dy = 0f
                                    } else if (dy >= threshold && index < jutarnjiLijekovi.size - 1) {
                                        val nextId = jutarnjiLijekovi[index + 1].id
                                        onReorder(DobaDana.JUTRO, lijek.id, nextId)
                                        dy = 0f
                                    }
                                }
                            }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 🌅 PODNE grupa
            if (popodnevniLijekovi.isNotEmpty()) {
                TimeGroupHeader(
                    label = "Podne",
                    time = "14:00",
                    count = popodnevniLijekovi.size,
                    emoji = "🌅"
                )
                Spacer(modifier = Modifier.height(8.dp))

                popodnevniLijekovi.forEachIndexed { index, lijek ->
                    var dy by remember(lijek.id) { mutableStateOf(0f) }
                    val threshold = 30f
                    WalletStyleCard(
                        lijek = lijek,
                        onEdit = { onEditLijek(lijek) },
                        onDelete = { onDeleteLijek(lijek) },
                        onRefill = { onRefillLijek(lijek) },
                        modifier = Modifier
                            .pointerInput(lijek.id) {
                                detectDragGesturesAfterLongPress(
                                    onDragEnd = { dy = 0f },
                                    onDragCancel = { dy = 0f }
                                ) { change, dragAmount ->
                                    change.consume()
                                    dy += dragAmount.y
                                    if (dy <= -threshold && index > 0) {
                                        val prevId = popodnevniLijekovi[index - 1].id
                                        onReorder(DobaDana.POPODNE, lijek.id, prevId)
                                        dy = 0f
                                    } else if (dy >= threshold && index < popodnevniLijekovi.size - 1) {
                                        val nextId = popodnevniLijekovi[index + 1].id
                                        onReorder(DobaDana.POPODNE, lijek.id, nextId)
                                        dy = 0f
                                    }
                                }
                            }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 🌙 VEČER grupa
            if (vecernjiLijekovi.isNotEmpty()) {
                TimeGroupHeader(
                    label = "Večer",
                    time = "20:00",
                    count = vecernjiLijekovi.size,
                    emoji = "🌙"
                )
                Spacer(modifier = Modifier.height(8.dp))

                vecernjiLijekovi.forEachIndexed { index, lijek ->
                    var dy by remember(lijek.id) { mutableStateOf(0f) }
                    val threshold = 30f
                    WalletStyleCard(
                        lijek = lijek,
                        onEdit = { onEditLijek(lijek) },
                        onDelete = { onDeleteLijek(lijek) },
                        onRefill = { onRefillLijek(lijek) },
                        modifier = Modifier
                            .pointerInput(lijek.id) {
                                detectDragGesturesAfterLongPress(
                                    onDragEnd = { dy = 0f },
                                    onDragCancel = { dy = 0f }
                                ) { change, dragAmount ->
                                    change.consume()
                                    dy += dragAmount.y
                                    if (dy <= -threshold && index > 0) {
                                        val prevId = vecernjiLijekovi[index - 1].id
                                        onReorder(DobaDana.VECER, lijek.id, prevId)
                                        dy = 0f
                                    } else if (dy >= threshold && index < vecernjiLijekovi.size - 1) {
                                        val nextId = vecernjiLijekovi[index + 1].id
                                        onReorder(DobaDana.VECER, lijek.id, nextId)
                                        dy = 0f
                                    }
                                }
                            }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ⏰ INTERVALNO grupa
            if (intervalniLijekovi.isNotEmpty()) {
                TimeGroupHeader(
                    label = "Intervalno",
                    time = "Po rasporedu",
                    count = intervalniLijekovi.size,
                    emoji = "⏰"
                )
                Spacer(modifier = Modifier.height(8.dp))

                intervalniLijekovi.forEach { lijek ->
                    IntervalLijekCard(
                        lijek = lijek,
                        onEdit = { onEditLijek(lijek) },
                        onDelete = { onDeleteLijek(lijek) },
                        onDoseTaken = { scheduledTime, actualTime ->
                            onDoseTaken(lijek, scheduledTime, actualTime)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

// Google Wallet stil draggable kartice lijeka
@Composable
fun DraggableLijekCard(
    lijek: Lijek,
    isDragging: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onRefill: () -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val isLowStock = lijek.trenutnoStanje <= 7
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val maxSwipeDistance = 200f

    // Animacije za drag state
    val scale by animateFloatAsState(
        targetValue = if (isDragging) 1.05f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_scale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isDragging) 16.dp else 4.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_elevation"
    )

    val rotation by animateFloatAsState(
        targetValue = if (isDragging) (offsetX * 0.01f).coerceIn(-3f, 3f) else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_rotation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(elevation, RoundedCornerShape(12.dp))
            .graphicsLayer {
                translationX = offsetX
                translationY = offsetY
                rotationZ = rotation
            }
            .zIndex(if (isDragging) 1f else 0f)
            .pointerInput(lijek.id) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onDragStart()
                    },
                    onDragEnd = {
                        when {
                            offsetX < -maxSwipeDistance -> {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onDelete()
                            }
                            offsetX > maxSwipeDistance -> {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onRefill()
                            }
                        }
                        offsetX = 0f
                        offsetY = 0f
                        onDragEnd()
                    },
                    onDragCancel = {
                        offsetX = 0f
                        offsetY = 0f
                        onDragEnd()
                    }
                ) { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y

                    // Ograniči horizontalni swipe
                    offsetX = offsetX.coerceIn(-maxSwipeDistance * 1.5f, maxSwipeDistance * 1.5f)
                    // Ograniči vertikalni drag
                    offsetY = offsetY.coerceIn(-100f, 100f)
                }
            }
            .clickable(enabled = !isDragging) { onEdit() },
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isDragging -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                offsetX < -50f -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                offsetX > 50f -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                isLowStock -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box {
            // Glavni sadržaj kartice
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Drag handle indicator (visible tijekom dragginga)
                AnimatedVisibility(
                    visible = isDragging,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(4.dp)
                                .background(
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                    RoundedCornerShape(2.dp)
                                )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = lijek.naziv,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (isLowStock) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier.padding(0.dp)
                        ) {
                            Text(
                                text = "⚠️ NARUČI",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onError,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = lijek.doza,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Prikaz trenutnog stanja
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "💊 Stanje:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${lijek.trenutnoStanje}/${lijek.pakiranje}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = if (isLowStock)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.primary
                    )
                }

                if (lijek.napomene.isNotEmpty()) {
                    Text(
                        text = lijek.napomene,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Swipe action hints
                AnimatedVisibility(
                    visible = offsetX < -50f || offsetX > 50f,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = when {
                            offsetX < -50f -> "← Swipe za brisanje"
                            offsetX > 50f -> "Swipe za dopunu →"
                            else -> ""
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = when {
                            offsetX < -50f -> MaterialTheme.colorScheme.error
                            offsetX > 50f -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Drag handle (visible uvijek, ali transparentan kad nije drag)
            if (isDragging) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        Icons.Default.DragHandle,
                        contentDescription = "Drag handle",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

// Google Wallet inspired card component
@Composable
fun WalletStyleCard(
    lijek: Lijek,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onRefill: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLowStock = lijek.trenutnoStanje <= 7
    var offsetX by remember { mutableStateOf(0f) }
    val maxSwipeDistance = 200f

    // Gradijent boje ovisno o stanju
    val cardGradient = when {
        isLowStock -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFEBEE), // Light red
                Color(0xFFFFCDD2)  // Slightly darker red
            )
        )
        else -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFF3E5F5), // Light purple
                Color(0xFFE1BEE7)  // Light lavender
            )
        )
    }

    // Animirane boje za swipe akcije
    val swipeColor by animateColorAsState(
        targetValue = when {
            offsetX < -50f -> Color(0xFFFF5252) // Red for delete
            offsetX > 50f -> Color(0xFF4CAF50)   // Green for refill
            else -> Color.Transparent
        },
        animationSpec = spring(),
        label = "swipe_color"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .offset { IntOffset(offsetX.roundToInt(), 0) }
            .pointerInput(lijek.id) {
                detectDragGesturesAfterLongPress(
                    onDragEnd = {
                        when {
                            offsetX < -maxSwipeDistance -> {
                                onDelete()
                                offsetX = 0f
                            }
                            offsetX > maxSwipeDistance -> {
                                onRefill()
                                offsetX = 0f
                            }
                            else -> offsetX = 0f
                        }
                    },
                    onDragCancel = { offsetX = 0f }
                ) { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetX = offsetX.coerceIn(-maxSwipeDistance * 1.5f, maxSwipeDistance * 1.5f)
                }
            }
            .clickable { onEdit() },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(cardGradient)
                .fillMaxWidth()
        ) {
            // Swipe indicator background
            if (offsetX != 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            swipeColor.copy(alpha = 0.2f),
                            RoundedCornerShape(20.dp)
                        )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header with medicine name and status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = lijek.naziv,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = lijek.doza,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    // Status indicator
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isLowStock)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = if (isLowStock) "⚠️ NARUČI" else "✓ OK",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Medicine info row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Stock info
                    InfoChip(
                        icon = "💊",
                        label = "Stanje",
                        value = "${lijek.trenutnoStanje}/${lijek.pakiranje}",
                        isWarning = isLowStock
                    )

                    // Schedule info
                    val scheduleText = buildString {
                        if (lijek.jutro) append("🌞 ")
                        if (lijek.popodne) append("☀️ ")
                        if (lijek.vecer) append("🌙 ")
                        if (lijek.tipUzimanja == TipUzimanja.INTERVALNO) {
                            append("⏰ ${lijek.intervalnoUzimanje?.intervalSati}h")
                        }
                    }.ifEmpty { "📋 Nije definirano" }

                    InfoChip(
                        icon = "",
                        label = "Raspored",
                        value = scheduleText,
                        isWarning = false
                    )
                }

                // Swipe hints
                AnimatedVisibility(
                    visible = offsetX != 0f,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = if (offsetX < 0) Arrangement.Start else Arrangement.End
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = swipeColor.copy(alpha = 0.9f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (offsetX < -50f) "🗑️ Obriši" else "➕ Dopuni",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoChip(
    icon: String,
    label: String,
    value: String,
    isWarning: Boolean = false
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isWarning)
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (icon.isNotEmpty()) {
                Text(
                    text = icon,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (isWarning)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurface,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PocetniEkran(context: Context? = null) {
    val lijekovi = remember { mutableStateListOf<Lijek>() }
    var idCounter by rememberSaveable { mutableIntStateOf(0) }
    var editLijek by remember { mutableStateOf<Lijek?>(null) }
    var showAddLijek by remember { mutableStateOf(false) }
    var showExportImportDialog by remember { mutableStateOf(false) }
    var showMessage by remember { mutableStateOf<String?>(null) }
    var currentScreen by remember { mutableStateOf("home") }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val saveData = {
        context?.let { ctx ->
            LijekoviDataManager.saveToLocalStorage(ctx, lijekovi)
        }
    }

    // Reordering helper
    val onReorder: (DobaDana, Int, Int) -> Unit = onReorder@{ grupa, fromId, toId ->
        // Filtriraj grupu i složi po sortOrderu
        val groupList = when (grupa) {
            DobaDana.JUTRO -> lijekovi.filter { it.jutro && it.tipUzimanja == TipUzimanja.STANDARDNO }
                .sortedBy { it.sortOrderJutro }
            DobaDana.POPODNE -> lijekovi.filter { it.popodne && it.tipUzimanja == TipUzimanja.STANDARDNO }
                .sortedBy { it.sortOrderPopodne }
            DobaDana.VECER -> lijekovi.filter { it.vecer && it.tipUzimanja == TipUzimanja.STANDARDNO }
                .sortedBy { it.sortOrderVecer }
        }.toMutableList()

        val fromIndex = groupList.indexOfFirst { it.id == fromId }
        val toIndex = groupList.indexOfFirst { it.id == toId }
        if (fromIndex == -1 || toIndex == -1) return@onReorder

        val moved = groupList.removeAt(fromIndex)
        groupList.add(toIndex, moved)

        // Ažuriraj sortOrder polja u originalnoj listi
        groupList.forEachIndexed { idx, l ->
            val globalIndex = lijekovi.indexOfFirst { it.id == l.id }
            if (globalIndex != -1) {
                val updated = when (grupa) {
                    DobaDana.JUTRO -> l.copy(sortOrderJutro = idx)
                    DobaDana.POPODNE -> l.copy(sortOrderPopodne = idx)
                    DobaDana.VECER -> l.copy(sortOrderVecer = idx)
                }
                lijekovi[globalIndex] = updated
            }
        }
        saveData()
    }

    LaunchedEffect(Unit) {
        context?.let { ctx ->
            val loadedLijekovi = LijekoviDataManager.loadFromLocalStorage(ctx)
            if (loadedLijekovi != null && loadedLijekovi.isNotEmpty()) {
                lijekovi.clear()
                lijekovi.addAll(loadedLijekovi)
                idCounter = (loadedLijekovi.maxOfOrNull { it.id } ?: -1) + 1
            }
        }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            context?.let { ctx ->
                val success = LijekoviDataManager.saveToFile(ctx, it, lijekovi)
                showMessage = if (success) "Podaci uspješno eksportirani!" else "Greška pri exportu!"
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            context?.let { ctx ->
                try {
                    val importedLijekovi = LijekoviDataManager.loadFromFile(ctx, it)
                    if (importedLijekovi != null) {
                        lijekovi.clear()
                        lijekovi.addAll(importedLijekovi)
                        idCounter = (importedLijekovi.maxOfOrNull { lijek -> lijek.id } ?: -1) + 1
                        saveData()
                        currentScreen = "home"
                        showMessage = "✅ Podaci uspješno importirani!\n\nUčitano ${importedLijekovi.size} lijekova."
                    } else {
                        showMessage = """
                            ❌ Greška pri importu podataka!
                            
                            Mogući uzroci:
                            • Datoteka nije valjani JSON format
                            • JSON ne odgovara strukturi aplikacije  
                            • Datoteka je oštećena ili prazna
                            • Nema dozvolu za čitanje datoteke
                            
                            💡 Savjet: Pokušajte exportirati podatke iz aplikacije pa ih importirati nazad da testirate format.
                            
                            🔍 Za detaljne informacije provjerite Logcat (filtriraj: LijekoviDataManager)
                        """.trimIndent()
                    }
                } catch (e: Exception) {
                    showMessage = """
                        ❌ Neočekivana greška pri importu!
                        
                        Greška: ${e.message ?: "Nepoznata greška"}
                        
                        💡 Provjerite:
                        • Je li datoteka ispravno eksportirana iz aplikacije
                        • Imate li dozvolu za čitanje datoteke
                        • Nije li datoteka oštećena
                    """.trimIndent()
                }
            }
        }
    }

    showMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { showMessage = null },
            title = { Text("Obavijest") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { showMessage = null }) {
                    Text("U redu")
                }
            }
        )
    }

    if (showExportImportDialog) {
        AlertDialog(
            onDismissRequest = { showExportImportDialog = false },
            title = { Text("Upravljanje podacima") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Odaberite akciju:")
                }
            },
            confirmButton = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            showExportImportDialog = false
                            exportLauncher.launch("lijekovi_backup.json")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Exportaj podatke")
                    }
                    Button(
                        onClick = {
                            showExportImportDialog = false
                            importLauncher.launch(arrayOf("application/json"))
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Importaj podatke")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportImportDialog = false }) {
                    Text("Odustani")
                }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "e-LijekoviHR",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    Divider(modifier = Modifier.padding(bottom = 8.dp))

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Početna") },
                        label = { Text("Početna") },
                        selected = currentScreen == "home",
                        onClick = {
                            currentScreen = "home"
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Analytics, contentDescription = "Statistike") },
                        label = { Text("Statistike") },
                        selected = currentScreen == "statistics",
                        onClick = {
                            currentScreen = "statistics"
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Postavke") },
                        label = { Text("Postavke") },
                        selected = currentScreen == "settings",
                        onClick = {
                            currentScreen = "settings"
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Info, contentDescription = "O aplikaciji") },
                        label = { Text("O aplikaciji") },
                        selected = currentScreen == "about",
                        onClick = {
                            currentScreen = "about"
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            when (currentScreen) {
                                "home" -> "Moji lijekovi"
                                "statistics" -> "Statistike"
                                "settings" -> "Postavke"
                                "about" -> "O aplikaciji"
                                else -> "e-LijekoviHR"
                            }
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch { drawerState.open() }
                            }
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            floatingActionButton = {
                if (currentScreen == "home") {
                    FloatingActionButton(
                        onClick = { showAddLijek = true }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Dodaj lijek")
                    }
                }
            }
        ) { paddingValues ->
            when (currentScreen) {
                "home" -> {
                    HomeScreen(
                        lijekovi = lijekovi,
                        onEditLijek = { editLijek = it },
                        onDeleteLijek = { lijek ->
                            lijekovi.remove(lijek)
                            saveData()
                        },
                        onRefillLijek = { lijek ->
                            // Dodaj pakiranje na trenutno stanje lijeka
                            val index = lijekovi.indexOfFirst { it.id == lijek.id }
                            if (index != -1) {
                                val updatedLijek = lijekovi[index].copy(
                                    trenutnoStanje = lijekovi[index].trenutnoStanje + lijekovi[index].pakiranje
                                )
                                lijekovi[index] = updatedLijek
                                saveData()
                                showMessage = "✅ Dodano je ${lijek.pakiranje} komada lijeka '${lijek.naziv}'.\n\nNovo stanje: ${updatedLijek.trenutnoStanje} komada"
                            }
                        },
                        onReorder = onReorder,
                        onDoseTaken = { lijek, scheduledTime, actualTime ->
                            // Handle interval therapy dose taking
                            val index = lijekovi.indexOfFirst { it.id == lijek.id }
                            if (index != -1 && lijek.intervalnoUzimanje != null) {
                                val updatedInterval = lijek.intervalnoUzimanje.oznaciDozuUzeta(scheduledTime, actualTime)
                                val updatedLijek = lijek.copy(
                                    intervalnoUzimanje = updatedInterval,
                                    // Reduce current stock when dose is taken
                                    trenutnoStanje = maxOf(0, lijek.trenutnoStanje - 1)
                                )
                                lijekovi[index] = updatedLijek
                                saveData()
                                showMessage = "✅ Doza lijeka '${lijek.naziv}' označena kao uzeta u $scheduledTime\n\nPreostalo: ${updatedLijek.trenutnoStanje} komada"
                            }
                        },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
                "statistics" -> {
                    StatisticsScreen(
                        lijekovi = lijekovi,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
                "settings" -> {
                    SettingsScreen(
                        onExportImport = { showExportImportDialog = true },
                        onTestImport = {
                            // Test import funkcionalnosti
                            context?.let { ctx ->
                                val testUri = android.net.Uri.parse("android.resource://${ctx.packageName}/raw/test_lijekovi.json")
                                try {
                                    val importedLijekovi = LijekoviDataManager.loadFromFile(ctx, testUri)
                                    if (importedLijekovi != null) {
                                        lijekovi.clear()
                                        lijekovi.addAll(importedLijekovi)
                                        idCounter = (importedLijekovi.maxOfOrNull { lijek -> lijek.id } ?: -1) + 1
                                        saveData()
                                        showMessage = "✅ Test podaci uspješno importirani!\n\nUčitano ${importedLijekovi.size} lijekova."
                                    } else {
                                        showMessage = "❌ Greška pri učitavanju test podataka!"
                                    }
                                } catch (e: Exception) {
                                    showMessage = "❌ Neočekivana greška: ${e.message}"
                                }
                            }
                        },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
                "about" -> {
                    AboutScreen(modifier = Modifier.padding(paddingValues))
                }
            }
        }
    }

    if (showAddLijek) {
        LijekDialog(
            lijek = null,
            existingLijekovi = lijekovi,
            onDismiss = { showAddLijek = false },
            onSave = { newLijek ->
                val duplicate = lijekovi.any {
                    it.naziv.lowercase() == newLijek.naziv.lowercase()
                }

                if (duplicate) {
                    showMessage = "Lijek s nazivom '${newLijek.naziv}' već postoji!"
                    return@LijekDialog
                }

                val lijekWithId = newLijek.copy(id = idCounter++)
                lijekovi.add(lijekWithId)
                saveData()
                showAddLijek = false
            }
        )
    }

    editLijek?.let { lijek ->
        LijekDialog(
            lijek = lijek,
            existingLijekovi = lijekovi.filter { it.id != lijek.id },
            onDismiss = { editLijek = null },
            onSave = { updatedLijek ->
                val duplicate = lijekovi.any {
                    it.id != lijek.id && it.naziv.lowercase() == updatedLijek.naziv.lowercase()
                }

                if (duplicate) {
                    showMessage = "Lijek s nazivom '${updatedLijek.naziv}' već postoji!"
                    return@LijekDialog
                }

                val index = lijekovi.indexOfFirst { it.id == lijek.id }
                if (index != -1) {
                    lijekovi[index] = updatedLijek
                    saveData()
                }
                editLijek = null
            }
        )
    }
}
