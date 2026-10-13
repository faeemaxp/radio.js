package com.robco.pipboyradio.ui.viewmodel

import android.app.Application
import android.content.ComponentName
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.robco.pipboyradio.data.model.RadioUiState
import com.robco.pipboyradio.data.model.Station
import com.robco.pipboyradio.data.repository.StationRepository
import com.robco.pipboyradio.service.RadioPlaybackService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class RadioViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StationRepository(application)
    private val _uiState = MutableStateFlow(RadioUiState())
    val uiState: StateFlow<RadioUiState> = _uiState.asStateFlow()

    private var allStations: List<Station> = emptyList()
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null

    private var tempStatusJob: Job? = null
    private var tipsJob: Job? = null
    private var complianceJob: Job? = null

    private val tips = listOf(
        "> VAULT-TEC CONNECTED. STAND BY FOR EMERGENCY BROADCASTS. <",
        "VAULT-TEC TIP: A HAPPY DWELLER LISTENS DAILY",
        "VAULT-TEC TIP: DO NOT ADJUST YOUR SET",
        "REMINDER: RADIATED WATER MAY CAUSE GLOWING",
        "KEEP YOUR PIP-BOY 3000 CALIBRATED",
        "WAR NEVER CHANGES, BUT THE MUSIC DOES",
        "BROUGHT TO YOU BY ROBCO INDUSTRIES",
        "HAPPINESS IS MANDATORY IN THE VAULT",
        "OVERSEER EYES ARE WATCHING",
        "[LOG] LOCAL VAULT STATUS: NOMINAL",
        "[LOG] BROADCAST RANGE: CONTINENTAL RUINS",
        "[LOG] WATER PURIFICATION CHIP: FUNCTIONAL",
        "[LOG] ENCLAVE COMMS: INTERCEPTED"
    )

    init {
        loadStations()
        initMediaController()
        startTipsLoop()
        startComplianceLoop()
    }

    private fun loadStations() {
        viewModelScope.launch {
            allStations = repository.getStations()
            if (allStations.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        currentStation = allStations[0],
                        currentStationIndex = 0
                    )
                }
            }
        }
    }

    fun getFilteredStations(): List<Station> {
        val query = _uiState.value.searchQuery.trim().lowercase()
        val cat = _uiState.value.selectedCategory

        return allStations.filter { station ->
            val matchesQuery = query.isEmpty() || station.name.lowercase().contains(query)
            if (!matchesQuery) return@filter false

            when (cat) {
                "ALL" -> true
                "WASTELAND" -> station.category.contains("Wasteland", ignoreCase = true) ||
                        station.category.contains("Vault", ignoreCase = true) ||
                        station.category.contains("Pre-War", ignoreCase = true)
                "VIVIDH BHARATI" -> station.name.contains("Vividh", ignoreCase = true) ||
                        station.name.contains("VBS", ignoreCase = true) ||
                        station.category.equals("Vividh Bharati", ignoreCase = true)
                "FM" -> station.name.contains("FM", ignoreCase = true)
                "AKASHVANI" -> station.name.contains("Akashvani", ignoreCase = true)
                else -> true
            }
        }
    }

    @OptIn(UnstableApi::class)
    private fun initMediaController() {
        val context = getApplication<Application>()
        val sessionToken = SessionToken(context, ComponentName(context, RadioPlaybackService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture?.addListener({
            try {
                mediaController = controllerFuture?.get()
                mediaController?.addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _uiState.update { it.copy(isPlaying = isPlaying) }
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_BUFFERING -> {
                                _uiState.update { it.copy(statusSignal = "ACQUIRING...") }
                            }
                            Player.STATE_READY -> {
                                if (mediaController?.isPlaying == true) {
                                    _uiState.update { it.copy(statusSignal = "BROADCASTING") }
                                }
                            }
                            Player.STATE_IDLE -> {
                                _uiState.update { it.copy(statusSignal = "STANDBY") }
                            }
                            Player.STATE_ENDED -> {
                                _uiState.update { it.copy(statusSignal = "STANDBY") }
                            }
                        }
                    }

                    override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                        _uiState.update { it.copy(statusSignal = "SIGNAL ERR") }
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, MoreExecutors.directExecutor())
    }

    fun playStation(index: Int) {
        if (index < 0 || index >= allStations.size) return
        val station = allStations[index]

        _uiState.update {
            it.copy(
                currentStationIndex = index,
                currentStation = station,
                isPlaying = true,
                statusSignal = "ACQUIRING..."
            )
        }

        mediaController?.let { player ->
            player.stop()
            player.clearMediaItems()
            val mediaItem = MediaItem.fromUri(station.url)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        }

        // Contextual station genre reaction
        val name = station.name.lowercase()
        viewModelScope.launch {
            delay(1500)
            when {
                name.contains("jazz") || name.contains("swing") || name.contains("secret") ->
                    showTemporaryStatus("JAZZ SIGNAL — PRE-WAR NOSTALGIA CONFIRMED", 3500)
                name.contains("classical") || name.contains("raagam") ->
                    showTemporaryStatus("CLASSICAL FREQUENCY — CULTURAL VALUE RESTORED", 3500)
                name.contains("news") || name.contains("live") ->
                    showTemporaryStatus("PROPAGANDA LEVELS: ANALYZING...", 3500)
                name.contains("drone") || name.contains("space") || name.contains("fallout") ->
                    showTemporaryStatus("WASTELAND RELAY LINK ESTABLISHED", 3500)
                else ->
                    showTemporaryStatus("VAULT-TEC RADIO NETWORK SYNCHRONIZED", 3000)
            }
        }
    }

    fun togglePower() {
        if (_uiState.value.isPlaying) {
            mediaController?.pause()
            _uiState.update {
                it.copy(
                    isPlaying = false,
                    statusSignal = "STANDBY"
                )
            }
        } else {
            playStation(_uiState.value.currentStationIndex)
        }
    }

    fun prevStation() {
        val total = allStations.size
        if (total == 0) return
        val newIdx = (_uiState.value.currentStationIndex - 1 + total) % total
        playStation(newIdx)
    }

    fun nextStation() {
        val total = allStations.size
        if (total == 0) return
        val newIdx = (_uiState.value.currentStationIndex + 1) % total
        playStation(newIdx)
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun setVolume(vol: Float) {
        val clamped = vol.coerceIn(0f, 1f)
        _uiState.update { it.copy(volume = clamped) }
        mediaController?.volume = clamped
    }

    fun toggleOverdrive() {
        val isNowOverdrive = !_uiState.value.isOverdrive
        _uiState.update { it.copy(isOverdrive = isNowOverdrive) }
        if (isNowOverdrive) {
            showTemporaryStatus("!!! OVERDRIVE !!!", 5000)
            viewModelScope.launch {
                delay(6000)
                _uiState.update { it.copy(isOverdrive = false) }
            }
        }
    }

    fun showTemporaryStatus(text: String, durationMs: Long = 3000) {
        tempStatusJob?.cancel()
        tempStatusJob = viewModelScope.launch {
            _uiState.update { it.copy(temporaryStatus = text) }
            delay(durationMs)
            _uiState.update { it.copy(temporaryStatus = null) }
        }
    }

    private fun startTipsLoop() {
        tipsJob = viewModelScope.launch {
            var index = 0
            while (isActive) {
                delay(12000)
                index = (index + 1) % tips.size
                _uiState.update { it.copy(activeTip = tips[index]) }
            }
        }
    }

    private fun startComplianceLoop() {
        complianceJob = viewModelScope.launch {
            while (isActive) {
                delay(45000)
                showTemporaryStatus("VAULT-TEC COMPLIANCE CHECK: PASSED", 4000)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        tempStatusJob?.cancel()
        tipsJob?.cancel()
        complianceJob?.cancel()
        controllerFuture?.let { MediaController.releaseFuture(it) }
    }
}
