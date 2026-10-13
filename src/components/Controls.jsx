import React, { useState } from 'react';
import { playTerminalClick } from '../utils/soundEffects';

const BOOT_PHRASES = ['INITIALIZE', 'BOOT SYSTEM', 'POWER ON', 'ENGAGE'];

export function Controls({
  isPlaying,
  volume,
  onTogglePower,
  onChangeVolume,
  onPrevStation,
  onNextStation
}) {
  const [initPhrase] = useState(() => BOOT_PHRASES[Math.floor(Math.random() * BOOT_PHRASES.length)]);

  const handlePowerClick = () => {
    playTerminalClick();
    onTogglePower();
  };

  const handlePrev = () => {
    playTerminalClick();
    onPrevStation();
  };

  const handleNext = () => {
    playTerminalClick();
    onNextStation();
  };

  return (
    <div className="controls-section frame-box">
      <div className="frame-title">[ CONTROLS ]</div>

      <div className="main-control-buttons">
        <button
          id="power-btn"
          className={`terminal-btn ${isPlaying ? 'active' : ''}`}
          onClick={handlePowerClick}
          aria-label={isPlaying ? 'Terminate Radio' : 'Initialize Radio'}
        >
          {isPlaying ? 'TERMINATE' : initPhrase}
        </button>

        <div className="tuning-step-buttons">
          <button
            className="terminal-btn-icon"
            onClick={handlePrev}
            title="Previous Station"
            aria-label="Previous Station"
          >
            &#9664; PREV
          </button>
          <button
            className="terminal-btn-icon"
            onClick={handleNext}
            title="Next Station"
            aria-label="Next Station"
          >
            NEXT &#9654;
          </button>
        </div>
      </div>

      <div className="control-group">
        <label htmlFor="volume">VOL</label>
        <input
          type="range"
          id="volume"
          min="0"
          max="1"
          step="0.05"
          value={volume}
          onChange={(e) => onChangeVolume(parseFloat(e.target.value))}
          aria-label="Volume Slider"
        />
        <span className="vol-percentage">{Math.round(volume * 100)}%</span>
      </div>
    </div>
  );
}
