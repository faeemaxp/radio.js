package com.robco.pipboyradio.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.robco.pipboyradio.ui.components.ControlsSection
import com.robco.pipboyradio.ui.components.StationListSection
import com.robco.pipboyradio.ui.components.StatusPanel
import com.robco.pipboyradio.ui.components.TerminalFooter
import com.robco.pipboyradio.ui.components.TerminalHeader
import com.robco.pipboyradio.ui.components.VisualizerCanvas
import com.robco.pipboyradio.ui.theme.ScreenBackground
import com.robco.pipboyradio.ui.viewmodel.RadioViewModel
import kotlin.math.roundToInt

@Composable
fun PipBoyRadioScreen(
    viewModel: RadioViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val filteredStations = viewModel.getFilteredStations()

    // Shake animation on overdrive
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(uiState.isOverdrive) {
        if (uiState.isOverdrive) {
            repeat(10) {
                shakeOffset.animateTo(4f, tween(50))
                shakeOffset.animateTo(-4f, tween(50))
            }
            shakeOffset.animateTo(0f, tween(50))
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .testTag("pipboy_scaffold"),
        containerColor = ScreenBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Header (Clock, Title, Overdrive Mode)
                TerminalHeader(
                    isOverdrive = uiState.isOverdrive,
                    onToggleOverdrive = { viewModel.toggleOverdrive() }
                )

                // Visualizer Section
                VisualizerCanvas(
                    isPlaying = uiState.isPlaying,
                    isOverdrive = uiState.isOverdrive
                )

                // Status Panel
                StatusPanel(
                    statusSignal = uiState.displayStatus,
                    currentStation = uiState.currentStation,
                    isPlaying = uiState.isPlaying,
                    isOverdrive = uiState.isOverdrive
                )

                // Controls Section
                ControlsSection(
                    isPlaying = uiState.isPlaying,
                    volume = uiState.volume,
                    isOverdrive = uiState.isOverdrive,
                    onTogglePower = { viewModel.togglePower() },
                    onPrevStation = { viewModel.prevStation() },
                    onNextStation = { viewModel.nextStation() },
                    onVolumeChange = { viewModel.setVolume(it) }
                )

                // Station List Section (Weight fills remaining screen height)
                StationListSection(
                    stations = filteredStations,
                    currentStationIndex = uiState.currentStationIndex,
                    searchQuery = uiState.searchQuery,
                    selectedCategory = uiState.selectedCategory,
                    isOverdrive = uiState.isOverdrive,
                    onSearchChange = { viewModel.setSearchQuery(it) },
                    onCategoryChange = { viewModel.setCategory(it) },
                    onStationSelect = { viewModel.playStation(it) },
                    modifier = Modifier.weight(1f)
                )

                // Terminal Footer
                TerminalFooter(
                    tipText = uiState.activeTip
                )
            }
        }
    }
}
