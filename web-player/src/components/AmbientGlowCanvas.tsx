import React, { useEffect, useRef } from 'react';

interface AmbientGlowCanvasProps {
  videoRef: React.RefObject<HTMLVideoElement | null>;
  enabled: boolean;
}

export const AmbientGlowCanvas: React.FC<AmbientGlowCanvasProps> = ({ videoRef, enabled }) => {
  const canvasRef = useRef<HTMLCanvasElement | null>(null);

  useEffect(() => {
    if (!enabled) return;

    let animFrame: number;
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d', { willReadFrequently: true });
    if (!ctx) return;

    const render = () => {
      const video = videoRef.current;
      if (video && !video.paused && !video.ended && video.readyState >= 2) {
        try {
          // Draw video downscaled for super smooth, low-overhead glow
          ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
        } catch {
          // CORS or ready state exceptions are handled safely
        }
      }
      animFrame = requestAnimationFrame(render);
    };

    animFrame = requestAnimationFrame(render);
    return () => cancelAnimationFrame(animFrame);
  }, [enabled, videoRef]);

  if (!enabled) return null;

  return (
    <div className="absolute inset-0 pointer-events-none flex items-center justify-center overflow-hidden z-0">
      <canvas
        ref={canvasRef}
        width={64}
        height={36}
        className="w-[115%] h-[115%] object-cover blur-3xl opacity-50 scale-110 transition-opacity duration-700"
      />
    </div>
  );
};
