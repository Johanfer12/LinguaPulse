package com.antigravity.linguapulse

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import com.antigravity.linguapulse.notifications.NotificationHelper
import com.antigravity.linguapulse.ui.screens.*
import com.antigravity.linguapulse.ui.theme.LinguaPulseTheme
import com.antigravity.linguapulse.update.UpdateManager
import com.antigravity.linguapulse.update.UpdateState
import com.antigravity.linguapulse.util.TtsHelper

enum class Screen(val title: String, val icon: ImageVector) {
    STUDY("Repaso", Icons.Default.School),
    EXPLORE("Explorar", Icons.Default.Search),
    GUIDE("Guía", Icons.Default.MenuBook),
    SETTINGS("Ajustes", Icons.Default.Settings)
}

// Se evalua una sola vez: `Screen.values()` creaba un array nuevo en cada
// recomposicion de la barra de navegacion.
private val SCREENS = Screen.values()

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var ttsHelper: TtsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ttsHelper = TtsHelper(this)

        handleIncomingIntent(intent)

        setContent {
            LinguaPulseTheme {
                var currentScreen by rememberSaveable { mutableStateOf(Screen.STUDY) }
                var showAddDialog by rememberSaveable { mutableStateOf(false) }
                var updatePromptDismissed by rememberSaveable { mutableStateOf(false) }

                // State collected from ViewModel
                val allCards by viewModel.allCards.collectAsState()
                val dueCards by viewModel.dueCards.collectAsState()
                val totalCount by viewModel.totalCount.collectAsState()
                val masteredCount by viewModel.masteredCount.collectAsState()
                val dueCount by viewModel.dueCount.collectAsState()
                val selectedCategory by viewModel.selectedStudyCategory.collectAsState()
                val intervalMinutes by viewModel.notificationIntervalMinutes.collectAsState()
                val autoCheckUpdates by viewModel.autoCheckUpdates.collectAsState()
                val updateState by viewModel.updateState.collectAsState()
                val deepLinkCard by viewModel.deepLinkCard.collectAsState()

                // Android 13+ Notification Permission Launcher
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { /* La respuesta se maneja de forma silenciosa. */ }

                fun checkAndRequestNotificationPermission() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }

                fun startUpdateDownload() {
                    if (UpdateManager.canRequestInstall(this@MainActivity)) {
                        viewModel.downloadAndInstallUpdate()
                    } else {
                        // Sin este permiso el instalador del sistema nunca aparece.
                        UpdateManager.openInstallPermissionSettings(this@MainActivity)
                    }
                }

                LaunchedEffect(Unit) {
                    checkAndRequestNotificationPermission()
                    // Detecta automaticamente cualquier version publicada desde el ultimo uso.
                    viewModel.checkForUpdates(silent = true)
                }

                Scaffold(
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = NavigationBarDefaults.Elevation
                        ) {
                            SCREENS.forEach { screen ->
                                val selected = currentScreen == screen
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { currentScreen = screen },
                                    icon = {
                                        Icon(
                                            imageVector = screen.icon,
                                            contentDescription = screen.title
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = screen.title,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    when (currentScreen) {
                        Screen.STUDY -> {
                            StudyScreen(
                                dueCards = dueCards,
                                selectedCategory = selectedCategory,
                                onCategoryChange = viewModel::setSelectedCategory,
                                onRatingSelected = viewModel::submitReview,
                                onSpeak = ttsHelper::speak,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        Screen.EXPLORE -> {
                            ExploreScreen(
                                cards = allCards,
                                onAddCardClick = { showAddDialog = true },
                                onSpeak = ttsHelper::speak,
                                onDeleteCard = viewModel::deleteCard,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        Screen.GUIDE -> {
                            GuideScreen(
                                onSpeak = ttsHelper::speak,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        Screen.SETTINGS -> {
                            SettingsScreen(
                                totalCards = totalCount,
                                masteredCards = masteredCount,
                                dueCardsCount = dueCount,
                                notificationIntervalMinutes = intervalMinutes,
                                onIntervalChange = viewModel::setNotificationInterval,
                                onSendTestNotification = viewModel::sendTestNotification,
                                onRequestNotificationPermission = { checkAndRequestNotificationPermission() },
                                onOpenDueCards = { currentScreen = Screen.STUDY },
                                onOpenCatalog = { currentScreen = Screen.EXPLORE },
                                updateState = updateState,
                                autoCheckUpdates = autoCheckUpdates,
                                onAutoCheckUpdatesChange = viewModel::setAutoCheckUpdates,
                                onCheckUpdates = { viewModel.checkForUpdates(silent = false) },
                                onDownloadUpdate = { startUpdateDownload() },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }

                    // Aviso de version nueva detectada automaticamente.
                    val available = updateState as? UpdateState.Available
                    if (available != null && !updatePromptDismissed) {
                        AlertDialog(
                            onDismissRequest = { updatePromptDismissed = true },
                            icon = { Icon(Icons.Default.SystemUpdate, contentDescription = null) },
                            title = { Text("Nueva versión disponible") },
                            text = {
                                Text(
                                    "LinguaPulse ${available.release.versionName} ya está publicada." +
                                            if (available.release.notes.isNotBlank()) {
                                                "\n\n${available.release.notes}"
                                            } else {
                                                ""
                                            }
                                )
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    updatePromptDismissed = true
                                    startUpdateDownload()
                                }) {
                                    Text("Actualizar ahora")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { updatePromptDismissed = true }) {
                                    Text("Más tarde")
                                }
                            }
                        )
                    }

                    // Deep link review dialog from notification
                    deepLinkCard?.let { card ->
                        CardReviewDialog(
                            card = card,
                            onDismiss = viewModel::clearDeepLinkCard,
                            onRatingSelected = viewModel::submitReview,
                            onSpeak = ttsHelper::speak
                        )
                    }

                    // Custom card creation modal
                    if (showAddDialog) {
                        AddCardDialog(
                            onDismiss = { showAddDialog = false },
                            onCardAdded = viewModel::insertCustomCard
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent == null) return
        val cardId = intent.getLongExtra(NotificationHelper.EXTRA_CARD_ID, -1L)
        if (cardId != -1L) {
            viewModel.loadDeepLinkCard(cardId)
        }
    }

    override fun onDestroy() {
        ttsHelper.shutdown()
        super.onDestroy()
    }
}
