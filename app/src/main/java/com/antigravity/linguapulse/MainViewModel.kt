package com.antigravity.linguapulse

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.antigravity.linguapulse.data.CardRepository
import com.antigravity.linguapulse.data.Flashcard
import com.antigravity.linguapulse.data.UserPreferences
import com.antigravity.linguapulse.notifications.CardNotificationWorker
import com.antigravity.linguapulse.srs.SrsRating
import com.antigravity.linguapulse.update.UpdateManager
import com.antigravity.linguapulse.update.UpdateState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as LinguaPulseApp
    private val repository: CardRepository = app.repository
    private val preferences: UserPreferences = app.preferences

    val allCards: StateFlow<List<Flashcard>> = repository.allCards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalCount: StateFlow<Int> = repository.totalCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val masteredCount: StateFlow<Int> = repository.masteredCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val dueCount: StateFlow<Int> = repository.getDueCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val selectedStudyCategory = MutableStateFlow("ALL")

    @OptIn(ExperimentalCoroutinesApi::class)
    val dueCards: StateFlow<List<Flashcard>> = selectedStudyCategory
        .flatMapLatest { category ->
            repository.getDueCards(category)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _notificationIntervalMinutes =
        MutableStateFlow(preferences.notificationIntervalMinutes)
    val notificationIntervalMinutes: StateFlow<Long> = _notificationIntervalMinutes.asStateFlow()

    private val _autoCheckUpdates = MutableStateFlow(preferences.autoCheckUpdates)
    val autoCheckUpdates: StateFlow<Boolean> = _autoCheckUpdates.asStateFlow()

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    val deepLinkCard = MutableStateFlow<Flashcard?>(null)

    fun setSelectedCategory(category: String) {
        selectedStudyCategory.value = category
    }

    fun submitReview(card: Flashcard, rating: SrsRating) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.submitReview(card, rating)
        }
    }

    fun insertCustomCard(card: Flashcard) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertCard(card)
        }
    }

    fun deleteCard(card: Flashcard) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCard(card)
        }
    }

    fun loadDeepLinkCard(cardId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            deepLinkCard.value = repository.getCardById(cardId)
        }
    }

    fun clearDeepLinkCard() {
        deepLinkCard.value = null
    }

    // ---------------------------------------------------------------- Notificaciones

    fun setNotificationInterval(minutes: Long) {
        _notificationIntervalMinutes.value = minutes
        preferences.notificationIntervalMinutes = minutes
        CardNotificationWorker.schedulePeriodic(
            context = getApplication(),
            intervalMinutes = minutes,
            replaceExisting = true
        )
    }

    fun sendTestNotification() {
        CardNotificationWorker.triggerImmediate(getApplication())
    }

    // ---------------------------------------------------------------- Actualizaciones

    fun setAutoCheckUpdates(enabled: Boolean) {
        _autoCheckUpdates.value = enabled
        preferences.autoCheckUpdates = enabled
    }

    /**
     * Busca una version mas reciente publicada en GitHub.
     *
     * @param silent true en el chequeo automatico de arranque: no muestra el
     *        estado "buscando" ni el resultado "ya estas al dia".
     */
    fun checkForUpdates(silent: Boolean = false) {
        if (_updateState.value is UpdateState.Checking) return
        if (silent && !preferences.autoCheckUpdates) return

        viewModelScope.launch {
            if (!silent) _updateState.value = UpdateState.Checking

            val result = UpdateManager.fetchLatestRelease()
            preferences.lastUpdateCheck = System.currentTimeMillis()

            result.fold(
                onSuccess = { release ->
                    _updateState.value = when {
                        UpdateManager.isNewer(release) -> UpdateState.Available(release)
                        silent -> UpdateState.Idle
                        else -> UpdateState.UpToDate(System.currentTimeMillis())
                    }
                },
                onFailure = { error ->
                    _updateState.value = if (silent) {
                        UpdateState.Idle
                    } else {
                        UpdateState.Failed(
                            error.message ?: "No se pudo consultar GitHub. Revisa tu conexion."
                        )
                    }
                }
            )
        }
    }

    fun downloadAndInstallUpdate() {
        val current = _updateState.value
        val release = when (current) {
            is UpdateState.Available -> current.release
            is UpdateState.Failed -> return
            else -> return
        }

        viewModelScope.launch {
            _updateState.value = UpdateState.Downloading(release, 0f)

            val result = UpdateManager.downloadApk(getApplication(), release) { progress ->
                _updateState.value = UpdateState.Downloading(release, progress)
            }

            result.fold(
                onSuccess = { apk ->
                    _updateState.value = UpdateState.ReadyToInstall(release)
                    runCatching { UpdateManager.installApk(getApplication(), apk) }
                        .onFailure {
                            _updateState.value = UpdateState.Failed(
                                "No se pudo abrir el instalador: ${it.message}"
                            )
                        }
                },
                onFailure = { error ->
                    _updateState.value = UpdateState.Failed(
                        error.message ?: "Fallo la descarga del APK."
                    )
                }
            )
        }
    }

    fun dismissUpdateState() {
        _updateState.value = UpdateState.Idle
    }
}
