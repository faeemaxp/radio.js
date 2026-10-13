import React from 'react';

export function StatusPanel({ statusSignal, currentStation, isPlaying, isTemporaryStatus }) {
  const isBlinking = statusSignal === 'ACQUIRING...' || isTemporaryStatus;

  return (
    <div className="info-section">
      <div className="now-playing frame-box">
        <div className="frame-title">[ STATUS ]</div>
        <div className="status-row">
          <span className="label">STATUS:</span>
          <span className={`value ${isBlinking ? 'blink' : ''} ${isTemporaryStatus ? 'emergency-alert' : ''}`}>
            {statusSignal}
          </span>
        </div>
        <div className="status-row">
          <span className="label">TUNED:</span>
          <span className={`value ${isPlaying ? 'blink' : ''} station-tuned-text`}>
            {isPlaying ? (currentStation?.name || '---') : '---'}
          </span>
        </div>
        {isPlaying && currentStation?.category && (
          <div className="status-row secondary-status-row">
            <span className="label">RELAY:</span>
            <span className="sub-value">
              [{currentStation.category.toUpperCase()}]
            </span>
          </div>
        )}
      </div>
    </div>
  );
}
