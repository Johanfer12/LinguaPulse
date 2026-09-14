package com.antigravity.linguapulse.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.antigravity.linguapulse.BuildConfig
import com.antigravity.linguapulse.data.UserPreferences
import com.antigravity.linguapulse.update.UpdateState

@Composable
fun SettingsScreen(
    totalCards: Int,
    masteredCards: Int,
    dueCardsCount: Int,
    notificationIntervalMinutes: Long,
    onIntervalChange: (Long) -> Unit,
    onSendTestNotification: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
    onOpenDueCards: () -> Unit,
    onOpenCatalog: () -> Unit,
    updateState: UpdateState,
    autoCheckUpdates: Boolean,
    onAutoCheckUpdatesChange: (Boolean) -> Unit,
    onCheckUpdates: () -> Unit,
    onDownloadUpdate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⚙️ Ajustes & Progreso",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            BetaBadge()
        }

        // 1. Estadisticas: ahora cada tarjeta lleva a alguna parte.
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📊 Tu Progreso de Retención",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Toca una cifra para ir directo a esa sección.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatBox(
                        label = "Total Fichas",
                        value = "$totalCards",
                        accent = MaterialTheme.colorScheme.primary,
                        onClick = onOpenCatalog
                    )
                    StatBox(
                        label = "Dominadas",
                        value = "$masteredCards",
                        accent = Color(0xFF10B981),
                        onClick = onOpenCatalog
                    )
                    StatBox(
                        label = "Pendientes Hoy",
                        value = "$dueCardsCount",
                        accent = Color(0xFFF59E0B),
                        onClick = onOpenDueCards
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onOpenDueCards,
                    enabled = dueCardsCount > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (dueCardsCount > 0) {
                            "Repasar $dueCardsCount pendientes ahora"
                        } else {
                            "No tienes pendientes por ahora 🎉"
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 2. Notificaciones
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SectionHeader(
                    icon = Icons.Default.NotificationsActive,
                    title = "Notificaciones de Aprendizaje",
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Recibe periódicamente una palabra o conector con su significado directo en la barra de estado para aprender sin abrir la app.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Frecuencia de envío:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                UserPreferences.INTERVAL_OPTIONS.forEach { (minutes, label) ->
                    val selected = notificationIntervalMinutes == minutes
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                onIntervalChange(minutes)
                                onRequestNotificationPermission()
                            }
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selected,
                            onClick = {
                                onIntervalChange(minutes)
                                onRequestNotificationPermission()
                            }
                        )
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Android agrupa el trabajo en segundo plano, así que el envío puede desviarse unos minutos. El mínimo permitido por el sistema es de 15 minutos.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onRequestNotificationPermission()
                        onSendTestNotification()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("🔔 Probar Notificación Ahora", fontWeight = FontWeight.Bold)
                }
            }
        }

        // 3. Actualizaciones
        UpdatesCard(
            updateState = updateState,
            autoCheckUpdates = autoCheckUpdates,
            onAutoCheckUpdatesChange = onAutoCheckUpdatesChange,
            onCheckUpdates = onCheckUpdates,
            onDownloadUpdate = onDownloadUpdate
        )

        // 4. Tema
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SectionHeader(
                    icon = if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                    title = "Tema & Ergonomía Visual",
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isDark) {
                        "Modo Oscuro Activo: Adaptado automáticamente según el tema de tu teléfono para proteger la vista en ambientes de poca luz."
                    } else {
                        "Modo Claro Activo: Adaptado automáticamente según el tema de tu teléfono para máxima legibilidad durante el día."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        Text(
            text = "LinguaPulse ${BuildConfig.VERSION_NAME} · build ${BuildConfig.VERSION_CODE}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun UpdatesCard(
    updateState: UpdateState,
    autoCheckUpdates: Boolean,
    onAutoCheckUpdatesChange: (Boolean) -> Unit,
    onCheckUpdates: () -> Unit,
    onDownloadUpdate: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionHeader(
                icon = Icons.Default.SystemUpdate,
                title = "Actualizaciones",
                tint = MaterialTheme.colorScheme.tertiary
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Cada cambio publicado en GitHub genera una versión nueva. LinguaPulse la detecta y la instala desde aquí.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Buscar al abrir la app",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Comprobación automática en segundo plano",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Switch(
                    checked = autoCheckUpdates,
                    onCheckedChange = onAutoCheckUpdatesChange
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Estado actual del flujo de actualizacion.
            when (updateState) {
                is UpdateState.Checking -> StatusRow(
                    text = "Buscando actualizaciones…",
                    showSpinner = true
                )

                is UpdateState.UpToDate -> StatusRow(
                    text = "Ya tienes la última versión disponible.",
                    icon = Icons.Default.CheckCircle,
                    tint = Color(0xFF10B981)
                )

                is UpdateState.Available -> Column {
                    StatusRow(
                        text = "Versión ${updateState.release.versionName} disponible",
                        icon = Icons.Default.NewReleases,
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    if (updateState.release.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = updateState.release.notes,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                is UpdateState.Downloading -> {
                    val progress = updateState.progress
                    Column {
                        StatusRow(
                            text = progress
                                ?.let { "Descargando… ${(it * 100).toInt()}%" }
                                ?: "Descargando…",
                            showSpinner = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (progress != null) {
                            LinearProgressIndicator(
                                progress = progress,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                            )
                        } else {
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                            )
                        }
                    }
                }

                is UpdateState.ReadyToInstall -> StatusRow(
                    text = "Descarga lista. Confirma la instalación en el diálogo del sistema.",
                    icon = Icons.Default.Download,
                    tint = Color(0xFF10B981)
                )

                is UpdateState.Failed -> StatusRow(
                    text = updateState.message,
                    icon = Icons.Default.ErrorOutline,
                    tint = MaterialTheme.colorScheme.error
                )

                UpdateState.Idle -> Unit
            }

            Spacer(modifier = Modifier.height(12.dp))

            val downloadable = updateState is UpdateState.Available
            val busy = updateState is UpdateState.Checking || updateState is UpdateState.Downloading

            Button(
                onClick = { if (downloadable) onDownloadUpdate() else onCheckUpdates() },
                enabled = !busy,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (downloadable) {
                        MaterialTheme.colorScheme.tertiary
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            ) {
                Icon(
                    imageVector = if (downloadable) Icons.Default.Download else Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (downloadable) "Descargar e instalar" else "Buscar actualizaciones",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    tint: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StatusRow(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    tint: Color = MaterialTheme.colorScheme.primary,
    showSpinner: Boolean = false
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (showSpinner) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(10.dp))
        } else if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
        )
    }
}

@Composable
private fun BetaBadge() {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.16f)
    ) {
        Text(
            text = "BETA",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Composable
private fun StatBox(
    label: String,
    value: String,
    accent: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = accent.copy(alpha = 0.12f),
        modifier = Modifier
            .width(100.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = accent
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}
