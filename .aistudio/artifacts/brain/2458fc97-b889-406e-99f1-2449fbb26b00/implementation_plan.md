# Pip-Boy Radio 3000 - Native Android Kotlin App

A native Android application in Kotlin and Jetpack Compose that ports the Pip-Boy 3000 radio player into a sleek Material 3 dark-themed experience with Fallout phosphor accents, Media3 ExoPlayer background streaming with media notifications, and real-time audio visualization.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The following decisions were confirmed during the requirements interview:

- **Confirmed Aesthetic**: Modern Material 3 dark theme with Fallout phosphor accents (deep slate `#0C100D`, phosphor green `#3CFF8F`, amber `#FFB000`, alert red `#FF4C4C`).
- **Confirmed Audio Engine**: Jetpack Media3 with `MediaSessionService` for continuous background streaming, Android 13+ system media controls, and lock screen notification support.
- **Confirmed Audio Visualizer**: Real-time animated phosphor frequency spectrum bars rendered on Compose `Canvas` with idle oscilloscope wave fallback.
- **Recommended Data Persistence**: Bundled pre-indexed station asset (`stations.json`) with in-memory caching and Android DataStore for favorites and volume preferences.

---

## 1. Overview & Core Concept

- **What It Does**: Streams 275+ live radio broadcasts across All India Radio (Akashvani), Vividh Bharati, FM Rainbow, and Wasteland ambient channels (SomaFM, Nightwave Plaza) with real-time spectrum visualization and RobCo terminal gimmicks.
- **Target Audience**: Fans of retro-futuristic Fallout aesthetics and radio enthusiasts looking for a responsive, battery-friendly native Android radio player.
- **Key Value**: Native Android performance, uninterrupted background streaming with system media notifications, robust HLS (`.m3u8`) and MP3 playback via ExoPlayer, and responsive Material 3 layout.

---

## 2. User Experience & Visual Design

### Key User Flows
1. **Launch & Station Discovery**:
   - The app opens with an edge-to-edge dark UI featuring the Pip-Boy header, live 24h clock, and status display.
   - Users browse 275+ stations organized by category chips (`ALL`, `WASTELAND`, `VIVIDH BHARATI`, `FM`, `AKASHVANI`).
   - Instant search bar filters frequencies and station names in real-time.
2. **Playback & Immersion**:
   - Tapping a station triggers a synthetic terminal click, acquires the live stream via Media3, and animates the phosphor green frequency bars.
   - Status transitions from `ACQUIRING...` to `BROADCASTING`, accompanied by RobCo personality remarks (e.g., Jazz detection, compliance checks).
   - Media controls allow Power toggle (`INITIALIZE` / `TERMINATE`), Previous/Next station stepping, and volume adjustments.
3. **Background Playback & Lock Screen**:
   - Minimizing the app or locking the screen keeps the stream playing seamlessly.
   - System media notification displays station metadata, live playback state, and Previous / Play / Pause / Next actions.
4. **RobCo Gimmicks & Overdrive**:
   - Tapping the mode toggle engages **CRT Overdrive Mode**, shifting accents to red phosphor flare, screen-shake animation, and emergency alert sirens.
   - Scrolling terminal footer rotates Vault-Tec tips and lore logs every 12 seconds.

### Visual Identity & Theme
- **Color Tokens**:
  - `Surface / Background`: `#0A0E0B` (terminal carbon) / `#121814` (surface container)
  - `Primary Phosphor`: `#3CFF8F` (Pip-Boy mint green)
  - `Secondary Phosphor`: `#1FA85B` (subdued green)
  - `Accent Amber`: `#FFB000` (RobCo amber status)
  - `Alert Red`: `#FF4C4C` (Overdrive / Emergency)
- **Typography**: Clean monospace display styling paired with Material 3 typography scale for accessible text scaling.
- **Components**: Framed cards with bracketed titles (`[ COMM-LINK ]`, `[ VISUALIZER ]`, `[ STATUS ]`, `[ CONTROLS ]`), M3 category chips, custom phosphor slider thumb, and active station indicators (`►`).

---

## 3. Key Product Decisions & Trade-Offs

- **Audio Stack: Media3 ExoPlayer + MediaSessionService**:
  - *Chosen Approach*: Official Android Media3 library with a foreground service.
  - *Why*: Supports both HLS (`HlsMediaSource`) and direct MP3/AAC streams with automatic buffering, audio focus management, and lock screen media controls.
  - *Alternatives Considered*: Standard `MediaPlayer` (lacks modern HLS reliability and MediaSession integrations).
- **Visualizer: Custom Compose Canvas with Procedural Harmonic Fallback**:
  - *Chosen Approach*: `Canvas` composable rendering dynamic vertical frequency bars with top glow caps.
  - *Why*: Eliminates complex Android `Visualizer` native audio session permission issues across diverse OEM devices while delivering smooth 60fps animations.
- **Stations Catalog Storage**:
  - *Chosen Approach*: Clean bundled JSON asset parsed via `kotlinx.serialization` on initial launch into in-memory state.
  - *Why*: Instant launch without cloud network latency; preserves all 275 stations offline.

---

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────┐
│                   PipBoyRadioActivity                  │
│  (Edge-to-Edge, Safe Insets, M3 PipBoyTheme)           │
└───────────────────────────┬────────────────────────────┘
                            │
┌───────────────────────────▼────────────────────────────┐
│                    PipBoyRadioScreen                   │
│ ┌───────────────────────┐   ┌────────────────────────┐ │
│ │  TerminalHeader       │   │ VisualizerCanvas       │ │
│ │  (Clock, Overdrive)   │   │ (Phosphor Bars & Glow) │ │
│ ├───────────────────────┤   ├────────────────────────┤ │
│ │  StatusPanel          │   │ ControlsRow            │ │
│ │  (Status, Tuned, Tag) │   │ (Power, Step, Volume)  │ │
│ ├───────────────────────┤   ├────────────────────────┤ │
│ │  StationListSection   │   │ TerminalFooter         │ │
│ │  (Search, Chips, AIR) │   │ (Vault-Tec Tips Ticker)│ │
│ └───────────────────────┘   └────────────────────────┘ │
└───────────────────────────┬────────────────────────────┘
                            │ StateFlow / Events
┌───────────────────────────▼────────────────────────────┐
│                   RadioViewModel                       │
│  (Search filter, Category selection, Gimmicks timer)   │
└─────────────┬───────────────────────────┬──────────────┘
              │                           │
┌─────────────▼──────────────┐ ┌──────────▼──────────────┐
│     StationRepository      │ │   RadioPlaybackService  │
│ (275+ Stations, Favorites) │ │   (Media3 ExoPlayer,    │
│                            │ │   MediaSession, Notif)  │
└────────────────────────────┘ └─────────────────────────┘
```

### Data Model
```kotlin
data class Station(
    val id: String,
    val name: String,
    val url: String,
    val category: String, // WASTELAND, VIVIDH BHARATI, FM, AKASHVANI
    val isFavorite: Boolean = false
)

data class RadioUiState(
    val isPlaying: Boolean = false,
    val currentStationIndex: Int = 0,
    val currentStation: Station? = null,
    val statusSignal: String = "STANDBY",
    val searchQuery: String = "",
    val selectedCategory: String = "ALL",
    val volume: Float = 0.7f,
    val isOverdrive: Boolean = false,
    val activeTip: String = "> VAULT-TEC CONNECTED. STAND BY FOR EMERGENCY BROADCASTS. <"
)
```

### Interactive State Transitions
- **Station Selection**: User taps station card -> `RadioViewModel.selectStation(index)` -> sends `MediaItem` to `RadioPlaybackService` -> status updates to `ACQUIRING...` -> on playback ready updates to `BROADCASTING` -> triggers personality toast.
- **Overdrive Engagement**: Mode button pressed -> toggles `isOverdrive = true` -> UI flashes alert red with shake offset -> temporary status `!!! OVERDRIVE !!!` -> auto-resets after 6 seconds.
- **Category Filter**: Category chip clicked -> updates `selectedCategory` state flow -> filtered list re-computes without recomposing entire screen.
