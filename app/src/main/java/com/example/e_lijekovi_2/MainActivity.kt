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
import androidx.compose.ui.tooling.preview.Preview
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.material3.Checkbox
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.sp
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
import androidx.compose.material.icons.filled.Help
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
                        icon = { Icon(Icons.Default.Help, contentDescription = "Pomoć") },
                        label = { Text("Pomoć") },
                        selected = currentScreen == "help",
                        onClick = {
                            currentScreen = "help"
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

                    Spacer(modifier = Modifier.weight(1f))

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Footer
                    Text(
                        text = "Verzija 1.0.0",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    ) {
        when {
            editLijek != null -> {
                EditLijekaEkran(
                    lijek = editLijek!!,
                    existingLijekovi = lijekovi,
                    onSpremi = {
                        editLijek = null
                    },
                    onOdustani = { editLijek = null }
                )
            }
            showAddLijek -> {
                DodajLijekEkran(
                    onDodaj = { naziv, dobaDana, pakiranje, trenutnoStanje ->
                        lijekovi.add(
                            Lijek(
                                id = idCounter++,
                                naziv = naziv,
                                dobaDana = dobaDana,
                                pakiranje = pakiranje,
                                trenutnoStanje = trenutnoStanje
                            )
                        )
                        showAddLijek = false
                    },
                    onOdustani = { showAddLijek = false },
                    existingLijekovi = lijekovi
                )
            }
            else -> {
                when (currentScreen) {
                    "home" -> HomeScreen(
                        lijekovi = lijekovi,
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onEditLijek = { editLijek = it },
                        onAddLijek = { showAddLijek = true },
                        onShowExportImport = { showExportImportDialog = true }
                    )
                    "statistics" -> StatisticsScreen(
                        lijekovi = lijekovi,
                        onMenuClick = { scope.launch { drawerState.open() } }
                    )
                    "settings" -> SettingsScreen(
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onExportImport = { showExportImportDialog = true }
                    )
                    "help" -> HelpScreen(
                        onMenuClick = { scope.launch { drawerState.open() } }
                    )
                    "about" -> AboutScreen(
                        onMenuClick = { scope.launch { drawerState.open() } }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    lijekovi: List<Lijek>,
    onMenuClick: () -> Unit,
    onEditLijek: (Lijek) -> Unit,
    onAddLijek: () -> Unit,
    onShowExportImport: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista lijekova") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddLijek) {
                Icon(Icons.Default.Add, contentDescription = "Dodaj lijek")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            if (lijekovi.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Medication,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "Nema dodanih lijekova",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Dodaj prvi lijek klikom na + gumb",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LijekoviGrupiraniPoDobaDana(
                    lijekovi = lijekovi,
                    onLijekClick = onEditLijek,
                    onUzmiSve = { dob ->
                        lijekovi.filter { it.dobaDana.contains(dob) }.forEach { it.uzmiLijek() }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    lijekovi: List<Lijek>,
    onMenuClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistike") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Ukupno lijekova
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Ukupno lijekova",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "${lijekovi.size}",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Medication,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Niska zaliha
            val nikaZalihaCount = lijekovi.count { it.trebaLiNaruciti() }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (nikaZalihaCount > 0)
                        MaterialTheme.colorScheme.errorContainer
                    else
                        MaterialTheme.colorScheme.secondaryContainer
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Niska zaliha",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "$nikaZalihaCount",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (nikaZalihaCount > 0)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.secondary
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = if (nikaZalihaCount > 0)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            // Lijekovi po terminima
            Text(
                text = "Raspodjela po terminima",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )

            DobaDana.entries.forEach { dob ->
                val count = lijekovi.count { it.dobaDana.contains(dob) }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when(dob) {
                                    DobaDana.JUTRO -> Icons.Default.WbSunny
                                    DobaDana.POPODNE -> Icons.Default.WbTwilight
                                    DobaDana.VECER -> Icons.Default.NightsStay
                                },
                                contentDescription = dob.name,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = dob.name.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Text(
                            text = "$count",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onMenuClick: () -> Unit,
    onExportImport: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Postavke") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Podaci",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExportImport() },
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Backup i restore",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Exportaj ili importaj podatke",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = "Izgled",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Tema",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Svijetla tema (automatski prema sistemu)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = "Notifikacije",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Podsjetnici",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Uskoro dostupno",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(onMenuClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pomoć") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Često postavljana pitanja",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            HelpItem(
                question = "Kako dodati novi lijek?",
                answer = "Kliknite na + gumb u donjem desnom kutu ekrana. Unesite naziv lijeka, količinu u pakiranju, trenutno stanje i odaberite termine uzimanja."
            )

            HelpItem(
                question = "Kako uzeti lijek?",
                answer = "Kliknite na karticu lijeka ili koristite 'Uzmi sve' gumb za cijelu grupu lijekova iz određenog termina."
            )

            HelpItem(
                question = "Što znači crvena kartica?",
                answer = "Crvena kartica označava da je zaliha lijeka niska (7 ili manje tableta). Vrijeme je da naručite novo pakiranje."
            )

            HelpItem(
                question = "Kako izvesti podatke?",
                answer = "Idite na Postavke > Backup i restore > Exportaj podatke. Podaci će biti spremljeni u JSON formatu."
            )

            HelpItem(
                question = "Mogu li dodati lijek za više termina?",
                answer = "Da! Prilikom dodavanja ili uređivanja lijeka možete označiti više termina (jutro, popodne, večer). Lijek će biti prikazan u svim odabranim grupama."
            )
        }
    }
}

@Composable
fun HelpItem(question: String, answer: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = question,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = answer,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onMenuClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("O aplikaciji") },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Icon(
                imageVector = Icons.Default.Medication,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "e-LijekoviHR",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Verzija 1.0.0",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "O aplikaciji",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "e-LijekoviHR je aplikacija za jednostavno praćenje uzimanja lijekova i upravljanje zalihama.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        text = "Značajke:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    FeatureItem("📊 Praćenje stanja lijekova")
                    FeatureItem("⏰ Organizacija po terminima uzimanja")
                    FeatureItem("⚠️ Upozorenja za nisku zalihu")
                    FeatureItem("💾 Export i import podataka")
                    FeatureItem("🎨 Moderan Material Design")

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        text = "Razvijeno s ❤️ korištenjem Kotlin i Jetpack Compose",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "© 2025 e-LijekoviHR",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun FeatureItem(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun DodajLijekEkran(
    onDodaj: (String, List<DobaDana>, Int, Int) -> Unit,
    onOdustani: () -> Unit,
    existingLijekovi: List<Lijek> = emptyList()
) {
    var naziv by remember { mutableStateOf("") }
    var pakiranje by remember { mutableStateOf("30") }
    var trenutnoStanje by remember { mutableStateOf("0") }
    var dobaJutro by remember { mutableStateOf(false) }
    var dobaPopodne by remember { mutableStateOf(false) }
    var dobaVecer by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Dodaj novi lijek", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = naziv,
            onValueChange = {
                naziv = it
                errorMessage = null // Resetuj error kad korisnik tipka
            },
            label = { Text("Naziv lijeka") },
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage != null,
            supportingText = if (errorMessage != null) {
                { Text(errorMessage!!, color = MaterialTheme.colorScheme.error) }
            } else null
        )

        OutlinedTextField(
            value = pakiranje,
            onValueChange = { pakiranje = it },
            label = { Text("Pakiranje (komada)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = trenutnoStanje,
            onValueChange = { trenutnoStanje = it },
            label = { Text("Trenutno stanje") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Odaberi doba dana za uzimanje:", style = MaterialTheme.typography.bodyLarge)

        // Kocke za odabir doba dana
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Jutro kocka
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { dobaJutro = !dobaJutro },
                colors = CardDefaults.cardColors(
                    containerColor = if (dobaJutro)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (dobaJutro) 8.dp else 2.dp
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = "Jutro",
                        modifier = Modifier.size(32.dp),
                        tint = if (dobaJutro)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Jutro",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (dobaJutro) FontWeight.Bold else FontWeight.Normal,
                        color = if (dobaJutro)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Popodne kocka
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { dobaPopodne = !dobaPopodne },
                colors = CardDefaults.cardColors(
                    containerColor = if (dobaPopodne)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (dobaPopodne) 8.dp else 2.dp
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WbTwilight,
                        contentDescription = "Popodne",
                        modifier = Modifier.size(32.dp),
                        tint = if (dobaPopodne)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Popodne",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (dobaPopodne) FontWeight.Bold else FontWeight.Normal,
                        color = if (dobaPopodne)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Večer kocka
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { dobaVecer = !dobaVecer },
                colors = CardDefaults.cardColors(
                    containerColor = if (dobaVecer)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (dobaVecer) 8.dp else 2.dp
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.NightsStay,
                        contentDescription = "Večer",
                        modifier = Modifier.size(32.dp),
                        tint = if (dobaVecer)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Večer",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (dobaVecer) FontWeight.Bold else FontWeight.Normal,
                        color = if (dobaVecer)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    if (naziv.isBlank()) {
                        errorMessage = "Naziv lijeka ne može biti prazan"
                        return@Button
                    }

                    // Provjera duplikata (case-insensitive)
                    val duplicateExists = existingLijekovi.any {
                        it.naziv.trim().lowercase() == naziv.trim().lowercase()
                    }

                    if (duplicateExists) {
                        errorMessage = "Lijek s nazivom '$naziv' već postoji"
                        return@Button
                    }

                    val odabranaDobaDana = mutableListOf<DobaDana>()
                    if (dobaJutro) odabranaDobaDana.add(DobaDana.JUTRO)
                    if (dobaPopodne) odabranaDobaDana.add(DobaDana.POPODNE)
                    if (dobaVecer) odabranaDobaDana.add(DobaDana.VECER)

                    if (odabranaDobaDana.isEmpty()) {
                        errorMessage = "Odaberi barem jedno doba dana"
                        return@Button
                    }

                    onDodaj(
                        naziv.trim(),
                        odabranaDobaDana,
                        pakiranje.toIntOrNull() ?: 30,
                        trenutnoStanje.toIntOrNull() ?: 0
                    )
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Dodaj")
            }

            Button(
                onClick = onOdustani,
                modifier = Modifier.weight(1f),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color.Gray
                )
            ) {
                Text("Odustani")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LijekoviGrupiraniPoDobaDana(
    lijekovi: List<Lijek>,
    onLijekClick: (Lijek) -> Unit,
    onUzmiSve: (DobaDana) -> Unit
) {
    val grupirani = DobaDana.entries.associateWith { dob -> lijekovi.filter { it.dobaDana.contains(dob) } }

    Column(modifier = Modifier.fillMaxWidth()) {
        DobaDana.entries.forEach { dob ->
            if (grupirani[dob]?.isNotEmpty() == true) {
                // Header za svako doba dana s ikonom
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
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
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = when(dob) {
                                    DobaDana.JUTRO -> Icons.Default.WbSunny
                                    DobaDana.POPODNE -> Icons.Default.WbTwilight
                                    DobaDana.VECER -> Icons.Default.NightsStay
                                },
                                contentDescription = dob.name,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = dob.name.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Button(
                            onClick = { onUzmiSve(dob) },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Uzmi sve",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Uzmi sve")
                        }
                    }
                }

                // Kartice za lijekove
                grupirani[dob]?.forEach { lijek ->
                    Card(
                        onClick = { onLijekClick(lijek) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (lijek.trebaLiNaruciti())
                                MaterialTheme.colorScheme.errorContainer
                            else
                                MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Ikona lijeka
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Medication,
                                        contentDescription = "Lijek",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = lijek.naziv,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text(
                                            text = "Stanje: ${lijek.trenutnoStanje}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (lijek.trebaLiNaruciti())
                                                MaterialTheme.colorScheme.error
                                            else
                                                MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "• Pakiranje: ${lijek.pakiranje}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            if (lijek.trebaLiNaruciti()) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Naruči lijek",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(36.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Uredi",
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun EditLijekaEkran(
    lijek: Lijek,
    existingLijekovi: List<Lijek> = emptyList(),
    onSpremi: () -> Unit,
    onOdustani: () -> Unit
) {
    var naziv by remember { mutableStateOf(lijek.naziv) }
    var pakiranje by remember { mutableStateOf(lijek.pakiranje.toString()) }
    var trenutnoStanje by remember { mutableStateOf(lijek.trenutnoStanje.toString()) }
    var dobaDana by remember { mutableStateOf(lijek.dobaDana) }
    var dobaJutro by remember { mutableStateOf(dobaDana.contains(DobaDana.JUTRO)) }
    var dobaPopodne by remember { mutableStateOf(dobaDana.contains(DobaDana.POPODNE)) }
    var dobaVecer by remember { mutableStateOf(dobaDana.contains(DobaDana.VECER)) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Uredi lijek", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = naziv,
            onValueChange = {
                naziv = it
                errorMessage = null
            },
            label = { Text("Naziv lijeka") },
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage != null,
            supportingText = if (errorMessage != null) {
                { Text(errorMessage!!, color = MaterialTheme.colorScheme.error) }
            } else null
        )

        OutlinedTextField(
            value = pakiranje,
            onValueChange = { pakiranje = it },
            label = { Text("Pakiranje (komada)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = trenutnoStanje,
            onValueChange = { trenutnoStanje = it },
            label = { Text("Trenutno stanje") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Odaberi doba dana za uzimanje:", style = MaterialTheme.typography.bodyLarge)
        // Kocke za odabir doba dana
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Jutro kocka
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { dobaJutro = !dobaJutro },
                colors = CardDefaults.cardColors(
                    containerColor = if (dobaJutro)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (dobaJutro) 8.dp else 2.dp
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = "Jutro",
                        modifier = Modifier.size(32.dp),
                        tint = if (dobaJutro)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Jutro",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (dobaJutro) FontWeight.Bold else FontWeight.Normal,
                        color = if (dobaJutro)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Popodne kocka
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { dobaPopodne = !dobaPopodne },
                colors = CardDefaults.cardColors(
                    containerColor = if (dobaPopodne)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (dobaPopodne) 8.dp else 2.dp
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WbTwilight,
                        contentDescription = "Popodne",
                        modifier = Modifier.size(32.dp),
                        tint = if (dobaPopodne)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Popodne",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (dobaPopodne) FontWeight.Bold else FontWeight.Normal,
                        color = if (dobaPopodne)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Večer kocka
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { dobaVecer = !dobaVecer },
                colors = CardDefaults.cardColors(
                    containerColor = if (dobaVecer)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (dobaVecer) 8.dp else 2.dp
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.NightsStay,
                        contentDescription = "Večer",
                        modifier = Modifier.size(32.dp),
                        tint = if (dobaVecer)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Večer",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (dobaVecer) FontWeight.Bold else FontWeight.Normal,
                        color = if (dobaVecer)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = {
                if (naziv.isBlank()) {
                    errorMessage = "Naziv lijeka ne može biti prazan"
                    return@Button
                }

                // Provjera duplikata - ne smije biti isti kao drugi lijek (osim trenutnog)
                val duplicateExists = existingLijekovi.any {
                    it.id != lijek.id &&
                    it.naziv.trim().lowercase() == naziv.trim().lowercase()
                }

                if (duplicateExists) {
                    errorMessage = "Lijek s nazivom '$naziv' već postoji"
                    return@Button
                }

                val odabranaDobaDana = mutableListOf<DobaDana>()
                if (dobaJutro) odabranaDobaDana.add(DobaDana.JUTRO)
                if (dobaPopodne) odabranaDobaDana.add(DobaDana.POPODNE)
                if (dobaVecer) odabranaDobaDana.add(DobaDana.VECER)

                if (odabranaDobaDana.isEmpty()) {
                    errorMessage = "Odaberi barem jedno doba dana"
                    return@Button
                }

                lijek.naziv = naziv.trim()
                lijek.pakiranje = pakiranje.toIntOrNull() ?: 30
                lijek.trenutnoStanje = trenutnoStanje.toIntOrNull() ?: 0
                lijek.dobaDana = odabranaDobaDana
                onSpremi()
            }) {
                Text("Spremi")
            }
            Button(onClick = onOdustani) {
                Text("Odustani")
            }
        }
    }
}

@Composable
fun DobaDanaDropdown(selected: DobaDana, onSelected: (DobaDana) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Button(onClick = { expanded = true }) {
            Text("Doba dana: ${selected.name.lowercase().replaceFirstChar { it.uppercase() }}")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DobaDana.entries.forEach { dob ->
                DropdownMenuItem(
                    text = { Text(dob.name.lowercase().replaceFirstChar { it.uppercase() }) },
                    onClick = {
                        onSelected(dob)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PocetniEkranPreview() {
    E_lijekovi_2Theme {
        PocetniEkran(context = null)
    }
}