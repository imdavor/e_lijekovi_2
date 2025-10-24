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
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
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
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.6f), Color.Transparent))
                    ),
                tonalElevation = 2.dp,
                shape = RoundedCornerShape(12.dp)
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
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.AddShoppingCart, contentDescription = null, tint = Color.White)
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

    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Kvadratić sa slikom (ikonom)
            Surface(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        brush = Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.6f), Color.Transparent))
                    ),
                tonalElevation = 2.dp,
                shape = RoundedCornerShape(10.dp)
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
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                        maxLines = 2
                    )
                }
                if (lijek.napomene.isNotBlank()) {
                    Text(
                        text = lijek.napomene,
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
                            containerColor = Color(0xFFE0E0E0),
                            contentColor = Color.Black
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
