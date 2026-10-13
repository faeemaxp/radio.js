package com.robco.pipboyradio.data.repository

import android.content.Context
import com.robco.pipboyradio.data.model.Station
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class StationRepository(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private var cachedStations: List<Station> = emptyList()

    suspend fun getStations(): List<Station> = withContext(Dispatchers.IO) {
        if (cachedStations.isNotEmpty()) return@withContext cachedStations

        try {
            val jsonString = context.assets.open("stations.json").bufferedReader().use { it.readText() }
            val stations: List<Station> = json.decodeFromString(jsonString)
            cachedStations = stations
            stations
        } catch (e: Exception) {
            e.printStackTrace()
            getFallbackStations()
        }
    }

    private fun getFallbackStations(): List<Station> = listOf(
        Station(id = "somafm-groovesalad", name = "SomaFM: Groove Salad", url = "https://ice1.somafm.com/groovesalad-128-mp3", category = "Wasteland Ambient"),
        Station(id = "somafm-deepspaceone", name = "SomaFM: Deep Space One", url = "https://ice1.somafm.com/deepspaceone-128-mp3", category = "Wasteland Ambient"),
        Station(id = "somafm-secretagent", name = "SomaFM: Secret Agent", url = "https://ice1.somafm.com/secretagent-128-mp3", category = "Vault Jazz"),
        Station(id = "somafm-dronezone", name = "SomaFM: Drone Zone", url = "https://ice1.somafm.com/dronezone-128-mp3", category = "Wasteland Ambient"),
        Station(id = "nightwave-plaza", name = "Nightwave Plaza", url = "https://radio.plaza.one/mp3", category = "Pre-War Wave"),
        Station(id = "vividh-bharati", name = "Vividh Bharati", url = "https://air.pc.cdn.bitgravity.com/air/live/pbaudio001/playlist.m3u8", category = "Vividh Bharati")
    )
}
