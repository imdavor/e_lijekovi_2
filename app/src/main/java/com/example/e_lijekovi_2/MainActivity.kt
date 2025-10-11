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
            onDismiss = { showAddLijek = false },
            onSave = { newLijek ->
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
            onDismiss = { editLijek = null },
            onSave = { updatedLijek ->
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
            lijekovi.forEach { lijek ->
                LijekCard(
                    lijek = lijek,
                    onEdit = { onEditLijek(lijek) },
                    onDelete = { onDeleteLijek(lijek) }
                )
                Spacer(modifier = Modifier.height(8.dp))
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
    onDismiss: () -> Unit,
    onSave: (Lijek) -> Unit
) {
    var naziv by remember { mutableStateOf(lijek?.naziv ?: "") }
    var doza by remember { mutableStateOf(lijek?.doza ?: "") }
    var jutro by remember { mutableStateOf(lijek?.jutro ?: false) }
    var popodne by remember { mutableStateOf(lijek?.popodne ?: false) }
    var vecer by remember { mutableStateOf(lijek?.vecer ?: false) }
    var napomene by remember { mutableStateOf(lijek?.napomene ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (lijek == null) "Dodaj lijek" else "Uredi lijek") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
                    placeholder = { Text("npr. 1 tableta") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Checkboxovi za vrijeme uzimanja
                Text(
                    "Vrijeme uzimanja:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { jutro = !jutro },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.Checkbox(
                        checked = jutro,
                        onCheckedChange = { jutro = it }
                    )
                    Icon(
                        Icons.Default.WbSunny,
                        contentDescription = "Jutro",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Jutro (08:00)")
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { popodne = !popodne },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.Checkbox(
                        checked = popodne,
                        onCheckedChange = { popodne = it }
                    )
                    Icon(
                        Icons.Default.WbTwilight,
                        contentDescription = "Popodne",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Popodne (14:00)")
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { vecer = !vecer },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.Checkbox(
                        checked = vecer,
                        onCheckedChange = { vecer = it }
                    )
                    Icon(
                        Icons.Default.NightsStay,
                        contentDescription = "Večer",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Večer (20:00)")
                }

                OutlinedTextField(
                    value = napomene,
                    onValueChange = { napomene = it },
                    label = { Text("Napomene") },
                    placeholder = { Text("Dodatne informacije...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (naziv.isNotBlank() && doza.isNotBlank()) {
                        val newLijek = lijek?.copy(
                            naziv = naziv,
                            doza = doza,
                            jutro = jutro,
                            popodne = popodne,
                            vecer = vecer,
                            napomene = napomene
                        ) ?: Lijek(
                            id = 0, // Bit će postavljen u pozivnoj funkciji
                            naziv = naziv,
                            doza = doza,
                            jutro = jutro,
                            popodne = popodne,
                            vecer = vecer,
                            napomene = napomene
                        )
                        onSave(newLijek)
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
