import React, { useState, useEffect } from 'react';
import { playTerminalClick } from '../utils/soundEffects';

export function TerminalHeader({ isOverdrive, onToggleOverdrive }) {
  const [timeStr, setTimeStr] = useState('00:00:00');

  useEffect(() => {
    const updateTime = () => {
      const now = new Date();
      setTimeStr(now.toLocaleTimeString('en-US', { hour12: false }));
    };
    updateTime();
    const interval = setInterval(updateTime, 1000);
    return () => clearInterval(interval);
  }, []);

  const handleOverdriveClick = () => {
    playTerminalClick();
    onToggleOverdrive();
  };

  return (
    <header className="terminal-header">
      <div className="header-line">----------------------------------------</div>
      <div className="header-title-row">
        <h1>PIP-BOY 3000</h1>
        <button
          className={`overdrive-badge-btn ${isOverdrive ? 'active-overdrive' : ''}`}
          onClick={handleOverdriveClick}
          title="Toggle RobCo Overdrive (Shift+O)"
        >
          {isOverdrive ? '[ OVERDRIVE: ENGAGED ]' : '[ MODE: STANDARD ]'}
        </button>
      </div>
      <div className="system-status">
        <span>&gt; SIGNAL: ACTIVE</span>
        <span id="clock" className="terminal-clock">{timeStr}</span>
      </div>
    </header>
  );
}
