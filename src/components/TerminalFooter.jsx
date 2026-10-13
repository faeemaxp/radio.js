import React, { useState, useEffect } from 'react';

const TIPS = [
  '> VAULT-TEC CONNECTED. STAND BY FOR EMERGENCY BROADCASTS. <',
  'VAULT-TEC TIP: A HAPPY DWELLER LISTENS DAILY',
  'VAULT-TEC TIP: DO NOT ADJUST YOUR SET',
  'REMINDER: RADIATED WATER MAY CAUSE GLOWING',
  'KEEP YOUR PIP-BOY 3000 CALIBRATED',
  'WAR NEVER CHANGES, BUT THE MUSIC DOES',
  'BROUGHT TO YOU BY ROBCO INDUSTRIES',
  'HAPPINESS IS MANDATORY IN THE VAULT',
  'OVERSEER EYES ARE WATCHING',
  '[LOG] LOCAL VAULT STATUS: NOMINAL',
  '[LOG] BROADCAST RANGE: CONTINENTAL RUINS',
  '[LOG] WATER PURIFICATION CHIP: FUNCTIONAL',
  '[LOG] ENCLAVE COMMS: INTERCEPTED'
];

export function TerminalFooter() {
  const [currentTipIndex, setCurrentTipIndex] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentTipIndex((prev) => (prev + 1) % TIPS.length);
    }, 12000);
    return () => clearInterval(interval);
  }, []);

  return (
    <footer className="terminal-footer">
      <div className="scrolling-text">
        {TIPS[currentTipIndex]}
      </div>
    </footer>
  );
}
