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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.font.FontWeight

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            E_lijekovi_2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PocetniEkran(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun PocetniEkran(modifier: Modifier = Modifier) {
    var nazivLijeka by rememberSaveable { mutableStateOf("") }
    var dobaDana by rememberSaveable { mutableStateOf(DobaDana.JUTRO) }
    var pakiranje by rememberSaveable { mutableStateOf("30") }
    var trenutnoStanje by rememberSaveable { mutableStateOf("0") }
    val lijekovi = remember { mutableStateListOf<Lijek>() }
    var idCounter by rememberSaveable { mutableStateOf(0) }
    var editLijek by remember { mutableStateOf<Lijek?>(null) }

    if (editLijek != null) {
        EditLijekaEkran(
            lijek = editLijek!!,
            onSpremi = {
                editLijek = null
            },
            onOdustani = { editLijek = null }
        )
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
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

            Spacer(modifier = Modifier.height(24.dp))
            Text("Dodaj novi lijek", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = nazivLijeka,
                onValueChange = { nazivLijeka = it },
                label = { Text("Naziv lijeka") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = pakiranje,
                    onValueChange = { pakiranje = it },
                    label = { Text("Pakiranje (komada)") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = trenutnoStanje,
                    onValueChange = { trenutnoStanje = it },
                    label = { Text("Trenutno stanje") },
                    modifier = Modifier.weight(1f)
                )
            }

            DobaDanaDropdown(dobaDana) { dobaDana = it }

            Button(
                onClick = {
                    if (nazivLijeka.isNotBlank()) {
                        lijekovi.add(
                            Lijek(
                                id = idCounter++,
                                naziv = nazivLijeka,
                                dobaDana = dobaDana,
                                pakiranje = pakiranje.toIntOrNull() ?: 30,
                                trenutnoStanje = trenutnoStanje.toIntOrNull() ?: 0
                            )
                        )
                        nazivLijeka = ""
                        pakiranje = "30"
                        trenutnoStanje = "0"
                        dobaDana = DobaDana.JUTRO
                    }
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Dodaj")
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
        PocetniEkran()
    }
}