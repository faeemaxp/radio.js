package com.robco.pipboyradio.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Station(
    val id: String = "",
    val name: String,
    val url: String,
    val category: String = "ALL",
    val isFavorite: Boolean = false
)

data class RadioUiState(
    val isPlaying: Boolean = false,
    val currentStationIndex: Int = 0,
    val currentStation: Station? = null,
    val statusSignal: String = "STANDBY",
    val temporaryStatus: String? = null,
    val searchQuery: String = "",
    val selectedCategory: String = "ALL",
    val volume: Float = 0.7f,
    val isOverdrive: Boolean = false,
    val isInterference: Boolean = false,
    val activeTip: String = "> VAULT-TEC CONNECTED. STAND BY FOR EMERGENCY BROADCASTS. <"
) {
    val displayStatus: String
        get() = temporaryStatus ?: statusSignal
}
