package com.example.e_lijekovi_2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.e_lijekovi_2.ui.theme.E_lijekovi_2Theme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.FloatingActionButton
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Context
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.foundation.clickable
import androidx.compose.material3.Divider
import kotlinx.coroutines.launch
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PocetniEkran(context: Context? = null) {
    val lijekovi = remember { mutableStateListOf<Lijek>() }
    var idCounter by rememberSaveable { mutableStateOf(0) }
    var editLijek by remember { mutableStateOf<Lijek?>(null) }
    var showAddLijek by remember { mutableStateOf(false) }
    var showExportImportDialog by remember { mutableStateOf(false) }
    var showMessage by remember { mutableStateOf<String?>(null) }

    // Navigation state
    var currentScreen by remember { mutableStateOf("home") }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Funkcija za automatsko spremanje podataka
    val saveData = {
        context?.let { ctx ->
            LijekoviDataManager.saveToLocalStorage(ctx, lijekovi)
        }
    }

    // Učitaj podatke prilikom pokretanja aplikacije
    LaunchedEffect(Unit) {
        context?.let { ctx ->
            val loadedLijekovi = LijekoviDataManager.loadFromLocalStorage(ctx)
            if (loadedLijekovi != null && loadedLijekovi.isNotEmpty()) {
                lijekovi.clear()
                lijekovi.addAll(loadedLijekovi)
                // Postavi idCounter na vrijednost veću od najvećeg ID-a
                idCounter = (loadedLijekovi.maxOfOrNull { it.id } ?: -1) + 1
            }
        }
    }

    // Launcher za export (kreiranje/spremanje datoteke)
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

    // Launcher za import (otvaranje datoteke)
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            context?.let { ctx ->
                val importedLijekovi = LijekoviDataManager.loadFromFile(ctx, it)
                if (importedLijekovi != null) {
                    lijekovi.clear()
                    lijekovi.addAll(importedLijekovi)
                    // Ažuriraj idCounter da bude veći od najvećeg ID-a
                    idCounter = (importedLijekovi.maxOfOrNull { lijek -> lijek.id } ?: -1) + 1
                    // Automatski spremi u lokalnu memoriju
                    saveData()
                    currentScreen = "home" // Prebaci na ekran liste
                    showMessage = "Podaci uspješno importirani! Učitano ${importedLijekovi.size} lijekova."
                } else {
                    showMessage = "Greška pri importu podataka!"
                }
            }
        }
    }

    // Dijalog za poruke
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

    // Dijalog za odabir Export/Import
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
                    // Header
                    Text(
                        text = "e-LijekoviHR",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    Divider(modifier = Modifier.padding(bottom = 8.dp))

                    // Navigation Items
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

    // Dijalog za dodavanje novog lijeka
    if (showAddLijek) {
        LijekDialog(
            lijek = null,
            existingLijekovi = lijekovi,
            onDismiss = { showAddLijek = false },
            onSave = { newLijek ->
                // Kontrola duplikata (case-insensitive)
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

    // Dijalog za uređivanje postojećeg lijeka
    editLijek?.let { lijek ->
        LijekDialog(
            lijek = lijek,
            existingLijekovi = lijekovi.filter { it.id != lijek.id },
            onDismiss = { editLijek = null },
            onSave = { updatedLijek ->
                // Kontrola duplikata (case-insensitive), ali ignoriraj trenutni lijek
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

@Composable
fun HomeScreen(
    lijekovi: List<Lijek>,
    onEditLijek: (Lijek) -> Unit,
    onDeleteLijek: (Lijek) -> Unit,
    modifier: Modifier = Modifier
) {
    // State za praćenje trenutno povučene kartice
    var draggedLijek by remember { mutableStateOf<Lijek?>(null) }
    var draggedTimeGroup by remember { mutableStateOf<DobaDana?>(null) }
    var draggedOverIndex by remember { mutableStateOf<Int?>(null) }
    var draggedFromIndex by remember { mutableStateOf<Int?>(null) }

    // Funkcija za spremanje novog redoslijeda
    val saveReorderedList = { timeGroup: DobaDana, reorderedList: List<Lijek> ->
        if (lijekovi is androidx.compose.runtime.snapshots.SnapshotStateList) {
            reorderedList.forEachIndexed { index, lijek ->
                val oldIndex = lijekovi.indexOfFirst { it.id == lijek.id }
                if (oldIndex != -1) {
                    val updatedLijek = when (timeGroup) {
                        DobaDana.JUTRO -> lijek.copy(sortOrderJutro = index)
                        DobaDana.POPODNE -> lijek.copy(sortOrderPopodne = index)
                        DobaDana.VECER -> lijek.copy(sortOrderVecer = index)
                    }
                    lijekovi[oldIndex] = updatedLijek
                }
            }
        }
    }

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
            // Grupiraj i sortiraj lijekove po vremenu uzimanja
            val jutarnjiLijekovi = lijekovi.filter { it.jutro }
                .sortedBy { it.sortOrderJutro }
            val popodnevniLijekovi = lijekovi.filter { it.popodne }
                .sortedBy { it.sortOrderPopodne }
            val vecernjiLijekovi = lijekovi.filter { it.vecer }
                .sortedBy { it.sortOrderVecer }

            // 🌞 JUTRO grupa
            if (jutarnjiLijekovi.isNotEmpty()) {
                TimeGroupHeader(
                    icon = Icons.Default.WbSunny,
                    label = "Jutro",
                    time = "08:00",
                    count = jutarnjiLijekovi.size,
                    emoji = "🌞"
                )

                jutarnjiLijekovi.forEachIndexed { index, lijek ->
                    // Prikaži liniju prije kartice ako je ovo ciljna pozicija
                    if (draggedTimeGroup == DobaDana.JUTRO &&
                        draggedOverIndex == index &&
                        draggedFromIndex != index &&
                        draggedLijek?.id != lijek.id) {
                        DropIndicatorLine()
                    }

                    ReorderableLijekCard(
                        lijek = lijek,
                        onEdit = { onEditLijek(lijek) },
                        onDelete = { onDeleteLijek(lijek) },
                        isDraggable = true,
                        isDragging = draggedLijek?.id == lijek.id,
                        isHoveredOver = draggedTimeGroup == DobaDana.JUTRO && draggedOverIndex == index,
                        onDragStart = {
                            draggedLijek = lijek
                            draggedTimeGroup = DobaDana.JUTRO
                            draggedFromIndex = index
                        },
                        onDragEnd = { offsetY ->
                            draggedLijek?.let { dragged ->
                                if (draggedTimeGroup == DobaDana.JUTRO && draggedFromIndex != null) {
                                    val currentList = jutarnjiLijekovi.toMutableList()
                                    val fromIndex = draggedFromIndex!!

                                    // Bolja kalkulacija - koristi stvarni offset u pikselima
                                    val itemHeightPx = 128 * 3 // približno 120dp kartice + 8dp razmak u pikselima
                                    val moveCount = (offsetY / itemHeightPx).toInt()
                                    var toIndex = fromIndex + moveCount

                                    // Limitiraj na validne indekse
                                    toIndex = toIndex.coerceIn(0, currentList.size - 1)

                                    if (fromIndex != toIndex) {
                                        val item = currentList.removeAt(fromIndex)
                                        currentList.add(toIndex, item)
                                        saveReorderedList(DobaDana.JUTRO, currentList)
                                    }
                                }
                            }
                            draggedLijek = null
                            draggedTimeGroup = null
                            draggedOverIndex = null
                            draggedFromIndex = null
                        },
                        onDragPositionChange = { offsetY ->
                            if (draggedFromIndex != null) {
                                // Bolja kalkulacija za hover indikator
                                val itemHeightPx = 128 * 3
                                val moveCount = (offsetY / itemHeightPx).toInt()
                                val newIndex = (draggedFromIndex!! + moveCount)
                                    .coerceIn(0, jutarnjiLijekovi.size - 1)
                                draggedOverIndex = newIndex
                            }
                        }
                    )

                    // Prikaži liniju nakon zadnje kartice
                    if (draggedTimeGroup == DobaDana.JUTRO &&
                        index == jutarnjiLijekovi.size - 1 &&
                        draggedOverIndex == jutarnjiLijekovi.size &&
                        draggedFromIndex != index) {
                        DropIndicatorLine()
                    }

                    if (index < jutarnjiLijekovi.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
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

                popodnevniLijekovi.forEachIndexed { index, lijek ->
                    if (draggedTimeGroup == DobaDana.POPODNE &&
                        draggedOverIndex == index &&
                        draggedFromIndex != index &&
                        draggedLijek?.id != lijek.id) {
                        DropIndicatorLine()
                    }

                    ReorderableLijekCard(
                        lijek = lijek,
                        onEdit = { onEditLijek(lijek) },
                        onDelete = { onDeleteLijek(lijek) },
                        isDraggable = true,
                        isDragging = draggedLijek?.id == lijek.id,
                        isHoveredOver = draggedTimeGroup == DobaDana.POPODNE && draggedOverIndex == index,
                        onDragStart = {
                            draggedLijek = lijek
                            draggedTimeGroup = DobaDana.POPODNE
                            draggedFromIndex = index
                        },
                        onDragEnd = { offsetY ->
                            draggedLijek?.let { dragged ->
                                if (draggedTimeGroup == DobaDana.POPODNE && draggedFromIndex != null) {
                                    val currentList = popodnevniLijekovi.toMutableList()
                                    val fromIndex = draggedFromIndex!!

                                    val itemHeightPx = 128 * 3
                                    val moveCount = (offsetY / itemHeightPx).toInt()
                                    var toIndex = fromIndex + moveCount
                                    toIndex = toIndex.coerceIn(0, currentList.size - 1)

                                    if (fromIndex != toIndex) {
                                        val item = currentList.removeAt(fromIndex)
                                        currentList.add(toIndex, item)
                                        saveReorderedList(DobaDana.POPODNE, currentList)
                                    }
                                }
                            }
                            draggedLijek = null
                            draggedTimeGroup = null
                            draggedOverIndex = null
                            draggedFromIndex = null
                        },
                        onDragPositionChange = { offsetY ->
                            if (draggedFromIndex != null) {
                                val itemHeightPx = 128 * 3
                                val moveCount = (offsetY / itemHeightPx).toInt()
                                val newIndex = (draggedFromIndex!! + moveCount)
                                    .coerceIn(0, popodnevniLijekovi.size - 1)
                                draggedOverIndex = newIndex
                            }
                        }
                    )

                    if (draggedTimeGroup == DobaDana.POPODNE &&
                        index == popodnevniLijekovi.size - 1 &&
                        draggedOverIndex == popodnevniLijekovi.size &&
                        draggedFromIndex != index) {
                        DropIndicatorLine()
                    }

                    if (index < popodnevniLijekovi.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
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

                vecernjiLijekovi.forEachIndexed { index, lijek ->
                    if (draggedTimeGroup == DobaDana.VECER &&
                        draggedOverIndex == index &&
                        draggedFromIndex != index &&
                        draggedLijek?.id != lijek.id) {
                        DropIndicatorLine()
                    }

                    ReorderableLijekCard(
                        lijek = lijek,
                        onEdit = { onEditLijek(lijek) },
                        onDelete = { onDeleteLijek(lijek) },
                        isDraggable = true,
                        isDragging = draggedLijek?.id == lijek.id,
                        isHoveredOver = draggedTimeGroup == DobaDana.VECER && draggedOverIndex == index,
                        onDragStart = {
                            draggedLijek = lijek
                            draggedTimeGroup = DobaDana.VECER
                            draggedFromIndex = index
                        },
                        onDragEnd = { offsetY ->
                            draggedLijek?.let { dragged ->
                                if (draggedTimeGroup == DobaDana.VECER && draggedFromIndex != null) {
                                    val currentList = vecernjiLijekovi.toMutableList()
                                    val fromIndex = draggedFromIndex!!

                                    val itemHeightPx = 128 * 3
                                    val moveCount = (offsetY / itemHeightPx).toInt()
                                    var toIndex = fromIndex + moveCount
                                    toIndex = toIndex.coerceIn(0, currentList.size - 1)

                                    if (fromIndex != toIndex) {
                                        val item = currentList.removeAt(fromIndex)
                                        currentList.add(toIndex, item)
                                        saveReorderedList(DobaDana.VECER, currentList)
                                    }
                                }
                            }
                            draggedLijek = null
                            draggedTimeGroup = null
                            draggedOverIndex = null
                            draggedFromIndex = null
                        },
                        onDragPositionChange = { offsetY ->
                            if (draggedFromIndex != null) {
                                val itemHeightPx = 128 * 3
                                val moveCount = (offsetY / itemHeightPx).toInt()
                                val newIndex = (draggedFromIndex!! + moveCount)
                                    .coerceIn(0, vecernjiLijekovi.size - 1)
                                draggedOverIndex = newIndex
                            }
                        }
                    )

                    if (draggedTimeGroup == DobaDana.VECER &&
                        index == vecernjiLijekovi.size - 1 &&
                        draggedOverIndex == vecernjiLijekovi.size &&
                        draggedFromIndex != index) {
                        DropIndicatorLine()
                    }

                    if (index < vecernjiLijekovi.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

// Nova komponenta: Header za grupu lijekova po vremenu
@Composable
fun TimeGroupHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    time: String,
    count: Int,
    emoji: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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

            // Badge sa brojem lijekova
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

@Composable
fun StatisticsScreen(
    lijekovi: List<Lijek>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Statistike lijekova",
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
                Text("Ukupno lijekova: ${lijekovi.size}")
                Text("Jutarnji lijekovi: ${lijekovi.count { it.jutro }}")
                Text("Popodnevni lijekovi: ${lijekovi.count { it.popodne }}")
                Text("Večernji lijekovi: ${lijekovi.count { it.vecer }}")
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

        Text(
            "e-LijekoviHR v1.0",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            "Aplikacija za praćenje uzimanja lijekova.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun LijekCard(
    lijek: Lijek,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
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
                }

                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Uredi")
                }
            }

            if (lijek.napomene.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = lijek.napomene,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (lijek.jutro) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.WbSunny,
                            contentDescription = "Jutro",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = lijek.vrijemeJutro,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                if (lijek.popodne) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.WbTwilight,
                            contentDescription = "Popodne",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = lijek.vrijemePopodne,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                if (lijek.vecer) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.NightsStay,
                            contentDescription = "Večer",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = lijek.vrijemeVecer,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LijekDialog(
    lijek: Lijek?,
    existingLijekovi: List<Lijek> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (Lijek) -> Unit
) {
    var naziv by remember { mutableStateOf(lijek?.naziv ?: "") }
    var doza by remember { mutableStateOf(lijek?.doza ?: "") }
    var jutro by remember { mutableStateOf(lijek?.jutro ?: false) }
    var popodne by remember { mutableStateOf(lijek?.popodne ?: false) }
    var vecer by remember { mutableStateOf(lijek?.vecer ?: false) }
    var napomene by remember { mutableStateOf(lijek?.napomene ?: "") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (lijek == null) "Dodaj lijek" else "Uredi lijek") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = naziv,
                    onValueChange = { naziv = it },
                    label = { Text("Naziv lijeka") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showError && naziv.isBlank()
                )

                OutlinedTextField(
                    value = doza,
                    onValueChange = { doza = it },
                    label = { Text("Doza") },
                    placeholder = { Text("npr. 1 tableta, 5mg") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showError && doza.isBlank()
                )

                // 🎨 Kocke za odabir doba dana (prema TODO-u)
                Text(
                    "Vrijeme uzimanja:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Jutro kocka
                    DobaBox(
                        selected = jutro,
                        onClick = { jutro = !jutro },
                        icon = Icons.Default.WbSunny,
                        label = "Jutro",
                        time = "08:00",
                        emoji = "🌞"
                    )

                    // Popodne kocka
                    DobaBox(
                        selected = popodne,
                        onClick = { popodne = !popodne },
                        icon = Icons.Default.WbTwilight,
                        label = "Popodne",
                        time = "14:00",
                        emoji = "🌅"
                    )

                    // Večer kocka
                    DobaBox(
                        selected = vecer,
                        onClick = { vecer = !vecer },
                        icon = Icons.Default.NightsStay,
                        label = "Večer",
                        time = "20:00",
                        emoji = "🌙"
                    )
                }

                if (showError && !jutro && !popodne && !vecer) {
                    Text(
                        "Odaberite barem jedno vrijeme uzimanja",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                OutlinedTextField(
                    value = napomene,
                    onValueChange = { napomene = it },
                    label = { Text("Napomene") },
                    placeholder = { Text("Dodatne informacije...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )

                if (showError && errorMessage.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Validacija
                    if (naziv.isBlank() || doza.isBlank() || (!jutro && !popodne && !vecer)) {
                        showError = true
                        errorMessage = "Sva polja moraju biti popunjena i barem jedno vrijeme odabrano"
                        return@TextButton
                    }

                    val newLijek = lijek?.copy(
                        naziv = naziv,
                        doza = doza,
                        jutro = jutro,
                        popodne = popodne,
                        vecer = vecer,
                        napomene = napomene
                    ) ?: Lijek(
                        id = 0,
                        naziv = naziv,
                        doza = doza,
                        jutro = jutro,
                        popodne = popodne,
                        vecer = vecer,
                        napomene = napomene
                    )
                    onSave(newLijek)
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

// Nova komponenta: Kocka za odabir doba dana
@Composable
fun DobaBox(
    selected: Boolean,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    time: String,
    emoji: String
) {
    Card(
        modifier = Modifier
            .size(100.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (selected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = time,
                style = MaterialTheme.typography.labelSmall,
                color = if (selected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Nova komponenta: Kartica lijeka s drag-and-drop mogućnošću
@Composable
fun ReorderableLijekCard(
    lijek: Lijek,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    isDraggable: Boolean = false,
    isDragging: Boolean = false,
    isHoveredOver: Boolean = false,
    onDragStart: () -> Unit = {},
    onDragEnd: (Float) -> Unit = {},
    onDragPositionChange: (Float) -> Unit = {}
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .zIndex(if (isDragging) 1f else 0f)
            .then(
                if (isDraggable) {
                    Modifier.pointerInput(Unit) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = {
                                onDragStart()
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                offsetX += dragAmount.x
                                offsetY += dragAmount.y
                                onDragPositionChange(offsetY)
                            },
                            onDragEnd = {
                                onDragEnd(offsetY)
                                offsetX = 0f
                                offsetY = 0f
                            },
                            onDragCancel = {
                                onDragEnd(0f)
                                offsetX = 0f
                                offsetY = 0f
                            }
                        )
                    }
                } else Modifier
            )
            .clickable { if (!isDragging) onEdit() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDragging) 8.dp else 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isDragging -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                isHoveredOver -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Drag handle indicator
                if (isDraggable) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Drag handle",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                }

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
                }

                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Uredi")
                }
            }

            if (lijek.napomene.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = lijek.napomene,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (lijek.jutro) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.WbSunny,
                            contentDescription = "Jutro",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = lijek.vrijemeJutro,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                if (lijek.popodne) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.WbTwilight,
                            contentDescription = "Popodne",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = lijek.vrijemePopodne,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                if (lijek.vecer) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.NightsStay,
                            contentDescription = "Večer",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = lijek.vrijemeVecer,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

// Nova komponenta: Placeholder za drop područje
@Composable
fun DropPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Ovdje ispustite za premještanje",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

// Nova komponenta: Linija indikatora za drop poziciju
@Composable
fun DropIndicatorLine() {
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .height(2.dp)
            .padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.primary
    )
}

