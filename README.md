# PIP-BOY RADIO 3000

RobCo Industries Termlink Protocol inspired retro-futuristic radio receiver and audio frequency visualizer.

## Features

- **RobCo Pip-Boy 3000 Terminal Aesthetic**:
  - CRT scanlines, screen curvature vignette, phosphor glow, and subtle flickering
  - RobCo phosphor green color palette with amber and emergency red accents
  - Dynamic 24-hour terminal clock and broadcast status indicators
- **Live Radio Streaming Engine**:
  - Full support for HLS (`.m3u8`) and direct MP3 streams via `hls.js` and HTML5 Audio
  - 275+ cataloged radio stations across Akashvani (AIR), Vividh Bharati, FM Rainbow, SomaFM, and Nightwave Plaza
  - Live frequency search and category filtering (All, Wasteland, Vividh Bharati, FM, Akashvani)
- **Real-Time Visualizer**:
  - Dynamic HTML5 Web Audio API frequency spectrum analyzer
  - Phosphor green waveform bars with dynamic gradient and cap lighting
  - Procedural baseline and frequency fluctuation fallback for resilient animation
- **RobCo Vault-Tec Gimmicks Module**:
  - Rotating Vault-Tec survival tips and lore ticks in the terminal footer
  - RobCo personality system responding to station genres (Jazz, Classical, News, Wasteland)
  - Interactive Web Audio synthesized mechanical clicks and tuning static
  - CRT Overdrive mode (trigger via Shift+O or header mode button) with red emergency warning and screen shake
  - Subtle random signal drift and interference simulations
  - Periodic Vault-Tec compliance check alerts

## Running the Application

```bash
npm run dev
```

Build for production:

```bash
npm run build
```
