package com.example.e_lijekovi_2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Context
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
    icon: ImageVector,
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
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lijek.naziv,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = lijek.doza,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (lijek.napomene.isNotEmpty()) {
                    Text(
                        text = lijek.napomene,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Uredi")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Obriši")
                }
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
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
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

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Uredi")
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
@Composable
fun LijekDialog(
    lijek: Lijek?,
    existingLijekovi: List<Lijek>,
    onDismiss: () -> Unit,
    onSave: (Lijek) -> Unit
) {
    var naziv by remember { mutableStateOf(lijek?.naziv ?: "") }
    var doza by remember { mutableStateOf(lijek?.doza ?: "") }
    var napomene by remember { mutableStateOf(lijek?.napomene ?: "") }
    var jutro by remember { mutableStateOf(lijek?.jutro ?: false) }
    var popodne by remember { mutableStateOf(lijek?.popodne ?: false) }
    var vecer by remember { mutableStateOf(lijek?.vecer ?: false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (lijek == null) "Dodaj lijek" else "Uredi lijek") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                OutlinedTextField(
                    value = napomene,
                    onValueChange = { napomene = it },
                    label = { Text("Napomene") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Vrijeme uzimanja:", fontWeight = FontWeight.Medium)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = jutro,
                            onCheckedChange = { jutro = it }
                        )
                        Text("Jutro")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = popodne,
                            onCheckedChange = { popodne = it }
                        )
                        Text("Popodne")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = vecer,
                            onCheckedChange = { vecer = it }
                        )
                        Text("Večer")
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
                            napomene = napomene.trim(),
                            jutro = jutro,
                            popodne = popodne,
                            vecer = vecer
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
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
    onReorder: (DobaDana, Int, Int) -> Unit, // (grupa, fromId, toId)
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
                    Icons.Default.Medication,
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
                    icon = Icons.Default.WbSunny,
                    label = "Jutro",
                    time = "08:00",
                    count = jutarnjiLijekovi.size,
                    emoji = "🌞"
                )
                Spacer(modifier = Modifier.height(8.dp))

                jutarnjiLijekovi.forEachIndexed { index, lijek ->
                    var dy by remember(lijek.id) { mutableStateOf(0f) }
                    val threshold = 30f
                    LijekCard(
                        lijek = lijek,
                        onEdit = { onEditLijek(lijek) },
                        onDelete = { onDeleteLijek(lijek) },
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

            // 🌅 POPODNE grupa
            if (popodnevniLijekovi.isNotEmpty()) {
                TimeGroupHeader(
                    icon = Icons.Default.WbTwilight,
                    label = "Popodne",
                    time = "14:00",
                    count = popodnevniLijekovi.size,
                    emoji = "🌅"
                )
                Spacer(modifier = Modifier.height(8.dp))

                popodnevniLijekovi.forEachIndexed { index, lijek ->
                    var dy by remember(lijek.id) { mutableStateOf(0f) }
                    val threshold = 30f
                    LijekCard(
                        lijek = lijek,
                        onEdit = { onEditLijek(lijek) },
                        onDelete = { onDeleteLijek(lijek) },
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
                    icon = Icons.Default.NightsStay,
                    label = "Večer",
                    time = "20:00",
                    count = vecernjiLijekovi.size,
                    emoji = "🌙"
                )
                Spacer(modifier = Modifier.height(8.dp))

                vecernjiLijekovi.forEachIndexed { index, lijek ->
                    var dy by remember(lijek.id) { mutableStateOf(0f) }
                    val threshold = 30f
                    LijekCard(
                        lijek = lijek,
                        onEdit = { onEditLijek(lijek) },
                        onDelete = { onDeleteLijek(lijek) },
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
                    icon = Icons.Default.Schedule,
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
                            // Ovo će biti obrađeno u parentu kroz ažuriranje state-a
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PocetniEkran(context: Context? = null) {
    val lijekovi = remember { mutableStateListOf<Lijek>() }
    var idCounter by rememberSaveable { mutableStateOf(0) }
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
                val importedLijekovi = LijekoviDataManager.loadFromFile(ctx, it)
                if (importedLijekovi != null) {
                    lijekovi.clear()
                    lijekovi.addAll(importedLijekovi)
                    idCounter = (importedLijekovi.maxOfOrNull { lijek -> lijek.id } ?: -1) + 1
                    saveData()
                    currentScreen = "home"
                    showMessage = "Podaci uspješno importirani! Učitano ${importedLijekovi.size} lijekova."
                } else {
                    showMessage = "Greška pri importu podataka!"
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
                        icon = { Icon(Icons.Default.BarChart, contentDescription = "Statistike") },
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
                        onReorder = onReorder,
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
