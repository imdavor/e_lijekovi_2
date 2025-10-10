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

@Composable
fun PocetniEkran(context: Context? = null) {
    val lijekovi = remember { mutableStateListOf<Lijek>() }
    var idCounter by rememberSaveable { mutableStateOf(0) }
    var editLijek by remember { mutableStateOf<Lijek?>(null) }
    var showAddLijek by remember { mutableStateOf(false) }
    var showExportImportDialog by remember { mutableStateOf(false) }
    var showMessage by remember { mutableStateOf<String?>(null) }

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

    when {
        editLijek != null -> {
            EditLijekaEkran(
                lijek = editLijek!!,
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
                onOdustani = { showAddLijek = false }
            )
        }
        else -> {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                floatingActionButton = {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FloatingActionButton(
                            onClick = { showExportImportDialog = true }
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = "Export/Import")
                        }
                        FloatingActionButton(
                            onClick = { showAddLijek = true }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Dodaj lijek")
                        }
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Lista lijekova", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))

                    LijekoviGrupiraniPoDobaDana(
                        lijekovi = lijekovi,
                        onLijekClick = { lijek -> editLijek = lijek },
                        onUzmiSve = { dob ->
                            lijekovi.filter { it.dobaDana == dob }.forEach { it.uzmiLijek() }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DodajLijekEkran(
    onDodaj: (String, DobaDana, Int, Int) -> Unit,
    onOdustani: () -> Unit
) {
    var naziv by remember { mutableStateOf("") }
    var pakiranje by remember { mutableStateOf("30") }
    var trenutnoStanje by remember { mutableStateOf("0") }
    var dobaDana by remember { mutableStateOf(DobaDana.JUTRO) }

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
            onValueChange = { naziv = it },
            label = { Text("Naziv lijeka") },
            modifier = Modifier.fillMaxWidth()
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

        DobaDanaDropdown(dobaDana) { dobaDana = it }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    if (naziv.isNotBlank()) {
                        onDodaj(
                            naziv,
                            dobaDana,
                            pakiranje.toIntOrNull() ?: 30,
                            trenutnoStanje.toIntOrNull() ?: 0
                        )
                    }
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
    val grupirani = DobaDana.entries.associateWith { dob -> lijekovi.filter { it.dobaDana == dob } }

    Column(modifier = Modifier.fillMaxWidth()) {
        DobaDana.entries.forEach { dob ->
            if (grupirani[dob]?.isNotEmpty() == true) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dob.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Button(onClick = { onUzmiSve(dob) }) {
                        Text("Uzmi sve")
                    }
                }

                grupirani[dob]?.forEach { lijek ->
                    Card(
                        onClick = { onLijekClick(lijek) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (lijek.trebaLiNaruciti())
                                Color(0xFFFFEBEE)
                            else
                                MaterialTheme.colorScheme.surface
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = lijek.naziv,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Stanje: ${lijek.trenutnoStanje} komada",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Pakiranje: ${lijek.pakiranje} komada",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }

                            if (lijek.trebaLiNaruciti()) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Naruči lijek",
                                    tint = Color.Red,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditLijekaEkran(
    lijek: Lijek,
    onSpremi: () -> Unit,
    onOdustani: () -> Unit
) {
    var naziv by remember { mutableStateOf(lijek.naziv) }
    var pakiranje by remember { mutableStateOf(lijek.pakiranje.toString()) }
    var trenutnoStanje by remember { mutableStateOf(lijek.trenutnoStanje.toString()) }
    var dobaDana by remember { mutableStateOf(lijek.dobaDana) }

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
            onValueChange = { naziv = it },
            label = { Text("Naziv lijeka") },
            modifier = Modifier.fillMaxWidth()
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

        Button(
            onClick = {
                lijek.trenutnoStanje += (pakiranje.toIntOrNull() ?: lijek.pakiranje)
                trenutnoStanje = lijek.trenutnoStanje.toString()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Dodaj pakiranje")
        }

        DobaDanaDropdown(dobaDana) { dobaDana = it }

        if (lijek.trebaLiNaruciti()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Upozorenje: Preostalo samo ${lijek.trenutnoStanje} komada. Potrebno naručiti!",
                        color = Color.Red
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
                    lijek.naziv = naziv
                    lijek.pakiranje = pakiranje.toIntOrNull() ?: lijek.pakiranje
                    lijek.trenutnoStanje = trenutnoStanje.toIntOrNull() ?: lijek.trenutnoStanje
                    lijek.dobaDana = dobaDana
                    onSpremi()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Spremi")
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