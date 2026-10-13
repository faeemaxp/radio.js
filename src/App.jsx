import React, { useState, useEffect, useCallback } from 'react';
import { STATIONS } from './data/stationsData';
import { useAudioPlayer } from './hooks/useAudioPlayer';
import { TerminalHeader } from './components/TerminalHeader';
import { Visualizer } from './components/Visualizer';
import { StatusPanel } from './components/StatusPanel';
import { Controls } from './components/Controls';
import { StationList } from './components/StationList';
import { TerminalFooter } from './components/TerminalFooter';
import { playOverdriveAlert } from './utils/soundEffects';

const PERSONALITY_MESSAGES = [
  'SIGNAL STABLE — ENJOY THE MUSIC, CITIZEN',
  'VAULT-TEC RADIO NETWORK CONNECTED',
  'WARNING: EXCESSIVE VIBING DETECTED',
  'CALIBRATING VACUUM TUBES...',
  'FILTERING BACKGROUND RADIATION...',
  'SYNCHRONIZING WITH VAULT NETWORK...'
];

export function App() {
  const {
    isPlaying,
    currentStationIndex,
    currentStation,
    volume,
    statusSignal,
    isTemporaryStatus,
    analyser,
    togglePower,
    playStation,
    setVolume,
    showTemporaryMessage,
  } = useAudioPlayer();

  const [isOverdrive, setIsOverdrive] = useState(false);
  const [isInterference, setIsInterference] = useState(false);
  const [isDrift, setIsDrift] = useState(false);

  // Trigger RobCo Overdrive mode
  const triggerOverdrive = useCallback(() => {
    setIsOverdrive(true);
    playOverdriveAlert();
    showTemporaryMessage('!!! OVERDRIVE !!!', 5000);

    setTimeout(() => {
      setIsOverdrive(false);
    }, 6000);
  }, [showTemporaryMessage]);

  // Key combo: Shift + O for Overdrive
  useEffect(() => {
    const handleKeyDown = (e) => {
      if (e.shiftKey && (e.key === 'O' || e.key === 'o')) {
        triggerOverdrive();
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [triggerOverdrive]);

  // Random glitches and drifts
  useEffect(() => {
    const glitchInterval = setInterval(() => {
      if (Math.random() < 0.15) {
        setIsInterference(true);
        setTimeout(() => setIsInterference(false), 400 + Math.random() * 500);
      }
    }, 8000);

    const driftInterval = setInterval(() => {
      if (Math.random() < 0.1) {
        setIsDrift(true);
        setTimeout(() => setIsDrift(false), 350 + Math.random() * 400);
      }
    }, 12000);

    // Periodic Vault-Tec compliance check
    const complianceInterval = setInterval(() => {
      showTemporaryMessage('VAULT-TEC COMPLIANCE CHECK: PASSED', 4000);
    }, 45000);

    return () => {
      clearInterval(glitchInterval);
      clearInterval(driftInterval);
      clearInterval(complianceInterval);
    };
  }, [showTemporaryMessage]);

  // Station Reaction on change
  const handleSelectStation = useCallback((index) => {
    playStation(index);

    const targetStation = STATIONS[index];
    if (targetStation) {
      const name = targetStation.name.toLowerCase();
      setTimeout(() => {
        if (name.includes('jazz') || name.includes('swing') || name.includes('secret')) {
          showTemporaryMessage('JAZZ SIGNAL — PRE-WAR NOSTALGIA CONFIRMED', 3500);
        } else if (name.includes('classical') || name.includes('symphony') || name.includes('raagam')) {
          showTemporaryMessage('CLASSICAL FREQUENCY — CULTURAL VALUE RESTORED', 3500);
        } else if (name.includes('news') || name.includes('live')) {
          showTemporaryMessage('PROPAGANDA LEVELS: ANALYZING...', 3500);
        } else if (name.includes('drone') || name.includes('space') || name.includes('fallout')) {
          showTemporaryMessage('WASTELAND RELAY LINK ESTABLISHED', 3500);
        } else {
          const randomMsg = PERSONALITY_MESSAGES[Math.floor(Math.random() * PERSONALITY_MESSAGES.length)];
          showTemporaryMessage(`VAULT-MSG: ${randomMsg}`, 3000);
        }
      }, 1500);
    }
  }, [playStation, showTemporaryMessage]);

  const handlePrevStation = useCallback(() => {
    const prevIndex = (currentStationIndex - 1 + STATIONS.length) % STATIONS.length;
    handleSelectStation(prevIndex);
  }, [currentStationIndex, handleSelectStation]);

  const handleNextStation = useCallback(() => {
    const nextIndex = (currentStationIndex + 1) % STATIONS.length;
    handleSelectStation(nextIndex);
  }, [currentStationIndex, handleSelectStation]);

  return (
    <div
      className={`pipboy-app-root ${isOverdrive ? 'crt-overdrive' : ''} ${
        isInterference ? 'signal-interference' : ''
      } ${isDrift ? 'signal-drift' : ''}`}
    >
      <div className="crt-overlay" />
      <div className="screen-curvature" />
      <div className="crt-vignette" />
      <div className="terminal-wear" />

      <div className="terminal-container">
        <StationList
          stations={STATIONS}
          currentStationIndex={currentStationIndex}
          onSelectStation={handleSelectStation}
        />

        <main className="main-panel">
          <TerminalHeader
            isOverdrive={isOverdrive}
            onToggleOverdrive={() => (isOverdrive ? setIsOverdrive(false) : triggerOverdrive())}
          />

          <div className="radio-interface">
            <Visualizer
              isPlaying={isPlaying}
              analyser={analyser}
              isInterference={isInterference}
              isOverdrive={isOverdrive}
            />

            <StatusPanel
              statusSignal={statusSignal}
              currentStation={currentStation}
              isPlaying={isPlaying}
              isTemporaryStatus={isTemporaryStatus}
            />

            <Controls
              isPlaying={isPlaying}
              volume={volume}
              onTogglePower={togglePower}
              onChangeVolume={setVolume}
              onPrevStation={handlePrevStation}
              onNextStation={handleNextStation}
            />
          </div>

          <TerminalFooter />
        </main>
      </div>
    </div>
  );
}

export default App;
