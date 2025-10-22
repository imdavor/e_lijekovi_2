package com.example.e_lijekovi_2.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.e_lijekovi_2.Lijek
import androidx.compose.material3.ExperimentalMaterial3Api

@Composable
fun FavoritiLijekoviScreen(
    lijekovi: List<Lijek>,
    onDodaj: (Lijek) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Favoriti",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        if (lijekovi.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Nema favorita.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(lijekovi) { lijek ->
                    LijekFavoritCard(
                        lijek = lijek,
                        onDodaj = { onDodaj(lijek) },
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LijekFavoritCard(
    lijek: Lijek,
    onDodaj: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(4.dp),
        onClick = onDodaj
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(lijek.naziv, style = MaterialTheme.typography.titleMedium)
            Text(lijek.doza, style = MaterialTheme.typography.bodyMedium)
            // Dodaj još prikaza po potrebi
        }
    }
}
