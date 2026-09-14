package com.antigravity.linguapulse.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antigravity.linguapulse.data.Categories
import com.antigravity.linguapulse.data.Flashcard
import com.antigravity.linguapulse.srs.SrsRating
import com.antigravity.linguapulse.ui.components.FilterChipItem
import com.antigravity.linguapulse.ui.components.FlipCardView
import com.antigravity.linguapulse.ui.components.SrsRatingButtons

@Composable
fun StudyScreen(
    dueCards: List<Flashcard>,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    onRatingSelected: (Flashcard, SrsRating) -> Unit,
    onSpeak: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentIndex by remember(dueCards) { mutableStateOf(0) }
    var isFlipped by remember(currentIndex, dueCards) { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Filter Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🎴 Repaso Diario (SRS)",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Memoriza con el método de repetición espaciada",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    FilterChipItem(
                        selected = selectedCategory == "ALL",
                        label = "Todas",
                        onClick = { onCategoryChange("ALL") }
                    )
                    FilterChipItem(
                        selected = selectedCategory == Categories.DAILY,
                        label = "Día a Día",
                        onClick = { onCategoryChange(Categories.DAILY) }
                    )
                    FilterChipItem(
                        selected = selectedCategory == Categories.B2B_SALES,
                        label = "Ventas B2B Tech",
                        onClick = { onCategoryChange(Categories.B2B_SALES) }
                    )
                }
            }
        }

        // Main Review Flow
        if (dueCards.isEmpty() || currentIndex >= dueCards.size) {
            // Empty State: All caught up!
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "¡Estás al día! 🎉",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Has repasado todas las tarjetas programadas para este momento. Vuelve más tarde o explora el catálogo completo.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { currentIndex = 0 },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reiniciar Sesión")
                        }
                    }
                }
            }
        } else {
            val currentCard = dueCards[currentIndex]

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Progress counter & bar
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tarjeta ${currentIndex + 1} de ${dueCards.size}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "${dueCards.size - currentIndex} pendientes hoy",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    LinearProgressIndicator(
                        progress = (currentIndex + 1).toFloat() / dueCards.size.toFloat(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }

                // 3D Flip Card View
                FlipCardView(
                    card = currentCard,
                    isFlipped = isFlipped,
                    onFlip = { isFlipped = !isFlipped },
                    onSpeak = onSpeak,
                    modifier = Modifier.weight(1f, fill = false)
                )

                // Rating buttons (visible after flip or user decision)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    if (!isFlipped) {
                        // Sin boton duplicado: la tarjeta se voltea tocandola.
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Toca la tarjeta para ver el significado",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    } else {
                        SrsRatingButtons(
                            onRatingSelected = { rating ->
                                onRatingSelected(currentCard, rating)
                                isFlipped = false
                                currentIndex += 1
                            }
                        )
                    }
                }
            }
        }
    }
}
