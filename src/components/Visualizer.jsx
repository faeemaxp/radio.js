import React, { useRef, useEffect } from 'react';

export function Visualizer({ isPlaying, analyser, isInterference, isOverdrive }) {
  const canvasRef = useRef(null);
  const animFrameRef = useRef(null);
  const proceduralPhaseRef = useRef(0);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const handleResize = () => {
      const rect = canvas.getBoundingClientRect();
      canvas.width = rect.width * window.devicePixelRatio;
      canvas.height = rect.height * window.devicePixelRatio;
      ctx.scale(window.devicePixelRatio, window.devicePixelRatio);
    };

    handleResize();
    window.addEventListener('resize', handleResize);

    const render = () => {
      const rect = canvas.getBoundingClientRect();
      const width = rect.width;
      const height = rect.height;

      // Clear canvas with deep black
      ctx.fillStyle = '#000000';
      ctx.fillRect(0, 0, width, height);

      // Subtle CRT grid lines inside visualizer
      ctx.strokeStyle = 'rgba(31, 168, 91, 0.15)';
      ctx.lineWidth = 1;
      const gridSpacing = 24;
      for (let y = gridSpacing; y < height; y += gridSpacing) {
        ctx.beginPath();
        ctx.moveTo(0, y);
        ctx.lineTo(width, y);
        ctx.stroke();
      }

      if (!isPlaying) {
        // Idle state: Phosphor oscilloscope line with gentle wave
        proceduralPhaseRef.current += 0.03;
        ctx.strokeStyle = isOverdrive ? '#ff4c4c' : 'rgba(60, 255, 143, 0.5)';
        ctx.lineWidth = 2;
        ctx.beginPath();
        ctx.moveTo(0, height / 2);
        for (let x = 0; x < width; x += 4) {
          const y = height / 2 + Math.sin(x * 0.05 + proceduralPhaseRef.current) * 3;
          ctx.lineTo(x, y);
        }
        ctx.stroke();
        animFrameRef.current = requestAnimationFrame(render);
        return;
      }

      let dataArray = null;
      let hasRealAudio = false;

      if (analyser) {
        const bufferLength = analyser.frequencyBinCount;
        dataArray = new Uint8Array(bufferLength);
        analyser.getByteFrequencyData(dataArray);
        // Check if there are non-zero readings
        let sum = 0;
        for (let i = 0; i < Math.min(bufferLength, 32); i++) {
          sum += dataArray[i];
        }
        if (sum > 10) {
          hasRealAudio = true;
        }
      }

      const numBars = Math.floor(width / 6);
      const barWidth = Math.max(3, (width / numBars) - 2);

      proceduralPhaseRef.current += 0.08;
      const phase = proceduralPhaseRef.current;

      for (let i = 0; i < numBars; i++) {
        let barHeight = 0;

        if (hasRealAudio && dataArray) {
          const sampleIndex = Math.floor((i / numBars) * (dataArray.length * 0.7));
          barHeight = (dataArray[sampleIndex] / 255) * (height * 0.85);
        } else {
          // Dynamic Procedural Simulation when CORS or stream is starting
          const normI = i / numBars;
          const bass = Math.sin(phase * 1.5 + normI * 2) * 0.4 + 0.6;
          const mid = Math.cos(phase * 2.2 - normI * 4) * 0.3 + 0.5;
          const rhythm = (Math.sin(phase * 3.5) > 0.3 ? 0.35 : 0.1) * (1 - normI * 0.5);
          const raw = (bass * 0.4 + mid * 0.4 + rhythm) * (1 - normI * 0.4);
          barHeight = Math.max(4, raw * (height * 0.75));
        }

        // Glitch effect on interference
        if (isInterference) {
          barHeight *= Math.random() > 0.5 ? 1.4 : 0.4;
        }

        const x = i * (barWidth + 2);
        const y = height - barHeight;

        // Color selection
        if (isOverdrive) {
          const alpha = 0.5 + (barHeight / height) * 0.5;
          ctx.fillStyle = `rgba(255, 76, 76, ${alpha})`;
        } else {
          const alpha = 0.45 + (barHeight / height) * 0.55;
          ctx.fillStyle = `rgba(60, 255, 143, ${alpha})`;
        }

        ctx.fillRect(x, y, barWidth, barHeight);

        // Phosphor cap highlight on top of bar
        ctx.fillStyle = isOverdrive ? '#ff9999' : '#ccffcc';
        ctx.fillRect(x, Math.max(0, y - 2), barWidth, 2);
      }

      animFrameRef.current = requestAnimationFrame(render);
    };

    render();

    return () => {
      window.removeEventListener('resize', handleResize);
      if (animFrameRef.current) {
        cancelAnimationFrame(animFrameRef.current);
      }
    };
  }, [isPlaying, analyser, isInterference, isOverdrive]);

  return (
    <div className="visualizer-section frame-box">
      <div className="frame-title">[ VISUALIZER ]</div>
      <canvas ref={canvasRef} className="visualizer-canvas" />
    </div>
  );
}
