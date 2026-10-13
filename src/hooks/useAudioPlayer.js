import { useState, useEffect, useRef, useCallback } from 'react';
import Hls from 'hls.js';
import { STATIONS } from '../data/stationsData';
import { playPowerSound, playTuningStatic } from '../utils/soundEffects';

export function useAudioPlayer() {
  const [isPlaying, setIsPlaying] = useState(false);
  const [currentStationIndex, setCurrentStationIndex] = useState(0);
  const [volume, setVolumeState] = useState(0.7);
  const [statusSignal, setStatusSignal] = useState('STANDBY');
  const [temporaryStatus, setTemporaryStatus] = useState(null);

  const audioRef = useRef(null);
  const hlsRef = useRef(null);
  const audioContextRef = useRef(null);
  const analyserRef = useRef(null);
  const sourceNodeRef = useRef(null);
  const tempTimeoutRef = useRef(null);

  // Initialize Audio & Context
  const initAudio = useCallback(() => {
    if (!audioRef.current) {
      const audio = new Audio();
      audio.crossOrigin = 'anonymous';
      audio.volume = volume;
      audioRef.current = audio;
    }

    if (!audioContextRef.current) {
      const AudioCtx = window.AudioContext || window.webkitAudioContext;
      if (AudioCtx) {
        try {
          const ctx = new AudioCtx();
          const analyser = ctx.createAnalyser();
          analyser.fftSize = 256;
          analyser.smoothingTimeConstant = 0.8;

          try {
            const source = ctx.createMediaElementSource(audioRef.current);
            source.connect(analyser);
            analyser.connect(ctx.destination);
            sourceNodeRef.current = source;
          } catch (e) {
            console.warn('Audio element source connection fallback:', e);
          }

          audioContextRef.current = ctx;
          analyserRef.current = analyser;
        } catch (e) {
          console.warn('Web Audio Context initialization error:', e);
        }
      }
    } else if (audioContextRef.current.state === 'suspended') {
      audioContextRef.current.resume();
    }
  }, [volume]);

  const showTemporaryMessage = useCallback((text, durationMs = 3000) => {
    if (tempTimeoutRef.current) clearTimeout(tempTimeoutRef.current);
    setTemporaryStatus(text);
    tempTimeoutRef.current = setTimeout(() => {
      setTemporaryStatus(null);
    }, durationMs);
  }, []);

  const stopRadio = useCallback(() => {
    setIsPlaying(false);
    setStatusSignal('STANDBY');
    playPowerSound(false);

    if (audioRef.current) {
      audioRef.current.pause();
      audioRef.current.src = '';
    }

    if (hlsRef.current) {
      hlsRef.current.destroy();
      hlsRef.current.null = null;
    }
  }, []);

  const playStation = useCallback((index) => {
    const station = STATIONS[index];
    if (!station) return;

    initAudio();
    playTuningStatic();

    setCurrentStationIndex(index);
    setIsPlaying(true);
    setStatusSignal('ACQUIRING...');

    const audio = audioRef.current;
    if (!audio) return;

    if (hlsRef.current) {
      hlsRef.current.destroy();
      hlsRef.current = null;
    }

    const url = station.url;
    const isM3U8 = url.includes('.m3u8');

    if (isM3U8 && Hls.isSupported()) {
      const hls = new Hls({
        enableWorker: true,
        lowLatencyMode: true,
      });
      hlsRef.current = hls;
      hls.loadSource(url);
      hls.attachMedia(audio);

      hls.on(Hls.Events.MANIFEST_PARSED, () => {
        audio.play().then(() => {
          setStatusSignal('BROADCASTING');
        }).catch((err) => {
          console.warn('Autoplay prevented or stream error:', err);
          setStatusSignal('BROADCASTING');
        });
      });

      hls.on(Hls.Events.ERROR, (event, data) => {
        if (data.fatal) {
          console.warn('HLS stream error:', data.type);
          setStatusSignal('SIGNAL ERR');
        }
      });
    } else if (audio.canPlayType('application/vnd.apple.mpegurl')) {
      audio.src = url;
      audio.load();
      audio.play().then(() => {
        setStatusSignal('BROADCASTING');
      }).catch((err) => {
        console.warn('Audio play error:', err);
        setStatusSignal('SIGNAL ERR');
      });
    } else {
      audio.src = url;
      audio.load();
      audio.play().then(() => {
        setStatusSignal('BROADCASTING');
      }).catch((err) => {
        console.warn('Audio play error:', err);
        setStatusSignal('SIGNAL ERR');
      });
    }
  }, [initAudio]);

  const togglePower = useCallback(() => {
    if (isPlaying) {
      stopRadio();
    } else {
      playPowerSound(true);
      playStation(currentStationIndex);
    }
  }, [isPlaying, currentStationIndex, playStation, stopRadio]);

  const setVolume = useCallback((newVol) => {
    setVolumeState(newVol);
    if (audioRef.current) {
      audioRef.current.volume = newVol;
    }
  }, []);

  // Cleanup on unmount
  useEffect(() => {
    return () => {
      if (audioRef.current) {
        audioRef.current.pause();
      }
      if (hlsRef.current) {
        hlsRef.current.destroy();
      }
      if (audioContextRef.current) {
        audioContextRef.current.close().catch(() => {});
      }
    };
  }, []);

  const currentStation = STATIONS[currentStationIndex] || STATIONS[0];

  return {
    isPlaying,
    currentStationIndex,
    currentStation,
    volume,
    statusSignal: temporaryStatus || statusSignal,
    isTemporaryStatus: Boolean(temporaryStatus),
    analyser: analyserRef.current,
    togglePower,
    playStation,
    setVolume,
    showTemporaryMessage,
  };
}
