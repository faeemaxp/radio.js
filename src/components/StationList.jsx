import React, { useState, useMemo, useRef, useEffect } from 'react';
import { playTerminalClick } from '../utils/soundEffects';

const CATEGORIES = ['ALL', 'WASTELAND', 'VIVIDH BHARATI', 'FM', 'AKASHVANI'];

export function StationList({ stations, currentStationIndex, onSelectStation }) {
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('ALL');
  const activeItemRef = useRef(null);

  const filteredStations = useMemo(() => {
    const query = searchQuery.trim().toLowerCase();
    return stations.filter((station) => {
      const matchesSearch = !query || station.name.toLowerCase().includes(query);
      if (!matchesSearch) return false;

      if (selectedCategory === 'ALL') return true;
      if (selectedCategory === 'WASTELAND') {
        return station.category?.includes('Wasteland') || station.category?.includes('Vault') || station.category?.includes('Pre-War');
      }
      if (selectedCategory === 'VIVIDH BHARATI') {
        return station.name.includes('Vividh') || station.name.includes('VBS') || station.category === 'Vividh Bharati';
      }
      if (selectedCategory === 'FM') {
        return station.name.includes('FM');
      }
      if (selectedCategory === 'AKASHVANI') {
        return station.name.includes('Akashvani');
      }
      return true;
    });
  }, [stations, searchQuery, selectedCategory]);

  // Scroll active item into view when currentStationIndex changes
  useEffect(() => {
    if (activeItemRef.current) {
      activeItemRef.current.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    }
  }, [currentStationIndex]);

  const handleStationClick = (originalIndex) => {
    playTerminalClick();
    onSelectStation(originalIndex);
  };

  const handleCategoryClick = (cat) => {
    playTerminalClick();
    setSelectedCategory(cat);
  };

  return (
    <aside className="sidebar-panel frame-box">
      <div className="frame-title">[ COMM-LINK ]</div>

      <div className="search-container">
        <span className="search-prompt">SEARCH:</span>
        <input
          type="text"
          id="search-input"
          placeholder="FREQUENCY_"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          aria-label="Search frequency or station name"
        />
        {searchQuery && (
          <button
            className="clear-search-btn"
            onClick={() => {
              playTerminalClick();
              setSearchQuery('');
            }}
            title="Clear search"
          >
            ✕
          </button>
        )}
      </div>

      <div className="category-chips">
        {CATEGORIES.map((cat) => (
          <button
            key={cat}
            className={`category-chip ${selectedCategory === cat ? 'active' : ''}`}
            onClick={() => handleCategoryClick(cat)}
          >
            {cat}
          </button>
        ))}
      </div>

      <div className="signals-count-bar">
        <span>&gt; {filteredStations.length} SIGNALS AVAILABLE</span>
      </div>

      <ul id="station-list" className="station-list">
        {filteredStations.length === 0 ? (
          <li className="station-item no-signals">
            NO SIGNALS DETECTED
          </li>
        ) : (
          filteredStations.map((station) => {
            const originalIndex = stations.indexOf(station);
            const isActive = originalIndex === currentStationIndex;

            return (
              <li
                key={station.id || `${station.name}-${originalIndex}`}
                ref={isActive ? activeItemRef : null}
                className={`station-item ${isActive ? 'active' : ''}`}
                onClick={() => handleStationClick(originalIndex)}
              >
                <span className="station-name-text">{station.name}</span>
                {station.category && (
                  <span className="station-tag">{station.category}</span>
                )}
              </li>
            );
          })
        )}
      </ul>
    </aside>
  );
}
