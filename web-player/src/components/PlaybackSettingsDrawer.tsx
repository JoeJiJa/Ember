import React from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X, Sparkles, Gauge, Maximize2, Headphones, Repeat } from 'lucide-react';
import type { AspectRatioMode } from '../types';

interface PlaybackSettingsDrawerProps {
  isOpen: boolean;
  onClose: () => void;
  speed: number;
  onSpeedChange: (speed: number) => void;
  aspectRatio: AspectRatioMode;
  onAspectRatioChange: (mode: AspectRatioMode) => void;
  ambientGlow: boolean;
  onAmbientGlowToggle: () => void;
  audioOnly: boolean;
  onAudioOnlyToggle: () => void;
  loopAB: { a: number | null; b: number | null; active: boolean };
  onSetPointA: () => void;
  onSetPointB: () => void;
  onClearLoopAB: () => void;
  currentTime: number;
}

export const PlaybackSettingsDrawer: React.FC<PlaybackSettingsDrawerProps> = ({
  isOpen,
  onClose,
  speed,
  onSpeedChange,
  aspectRatio,
  onAspectRatioChange,
  ambientGlow,
  onAmbientGlowToggle,
  audioOnly,
  onAudioOnlyToggle,
  loopAB,
  onSetPointA,
  onSetPointB,
  onClearLoopAB,
  currentTime,
}) => {
  const speeds = [0.25, 0.5, 0.75, 1.0, 1.25, 1.5, 2.0, 3.0, 4.0];
  const aspectModes: { id: AspectRatioMode; label: string }[] = [
    { id: 'fit', label: 'Fit' },
    { id: 'fill', label: 'Fill / Crop' },
    { id: 'zoom', label: 'Zoom 125%' },
    { id: '16-9', label: '16:9' },
    { id: '4-3', label: '4:3' },
    { id: 'stretch', label: 'Stretch' },
  ];

  return (
    <AnimatePresence>
      {isOpen && (
        <>
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={onClose}
            className="fixed inset-0 bg-black/60 backdrop-blur-sm z-50"
          />

          <motion.div
            initial={{ y: '100%' }}
            animate={{ y: 0 }}
            exit={{ y: '100%' }}
            transition={{ type: 'spring', damping: 28, stiffness: 260 }}
            className="fixed bottom-0 left-0 right-0 max-w-xl mx-auto bg-[#121212] border-t border-white/10 rounded-t-3xl p-6 z-50 shadow-2xl text-white max-h-[85vh] overflow-y-auto"
          >
            {/* Header */}
            <div className="flex items-center justify-between pb-4 border-b border-white/10">
              <h3 className="text-lg font-semibold tracking-wide">Playback & Video Controls</h3>
              <button
                onClick={onClose}
                className="p-2 hover:bg-white/10 rounded-full transition-colors text-zinc-400 hover:text-white"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {/* Playback Speed */}
            <div className="py-4 border-b border-white/5">
              <div className="flex items-center justify-between mb-2">
                <div className="flex items-center gap-2 text-zinc-300">
                  <Gauge className="w-4 h-4 text-amber-400" />
                  <span className="text-xs font-semibold">Playback Speed</span>
                </div>
                <span className="text-xs font-mono font-bold text-amber-400">{speed.toFixed(2)}x</span>
              </div>

              {/* Speed Buttons Grid */}
              <div className="flex items-center gap-1.5 overflow-x-auto pb-2 scrollbar-none">
                {speeds.map((s) => (
                  <button
                    key={s}
                    onClick={() => onSpeedChange(s)}
                    className={`px-3 py-1.5 rounded-xl text-xs font-mono font-medium transition-all ${
                      speed === s
                        ? 'bg-amber-500 text-black font-bold shadow-lg shadow-amber-500/20'
                        : 'bg-white/5 border border-white/5 text-zinc-300 hover:bg-white/10'
                    }`}
                  >
                    {s}x
                  </button>
                ))}
              </div>

              {/* Custom Speed Slider */}
              <input
                type="range"
                min={0.25}
                max={4.0}
                step={0.05}
                value={speed}
                onChange={(e) => onSpeedChange(parseFloat(e.target.value))}
                className="w-full accent-amber-500 mt-2 cursor-pointer"
              />
            </div>

            {/* Aspect Ratio Switcher */}
            <div className="py-4 border-b border-white/5">
              <div className="flex items-center gap-2 text-zinc-300 mb-3">
                <Maximize2 className="w-4 h-4 text-amber-400" />
                <span className="text-xs font-semibold">Aspect Ratio Mode</span>
              </div>
              <div className="grid grid-cols-3 gap-2">
                {aspectModes.map((mode) => (
                  <button
                    key={mode.id}
                    onClick={() => onAspectRatioChange(mode.id)}
                    className={`py-2 px-3 rounded-xl text-xs font-medium border transition-all ${
                      aspectRatio === mode.id
                        ? 'bg-amber-500/15 border-amber-500 text-amber-300 font-semibold shadow-md'
                        : 'bg-white/5 border-white/10 text-zinc-400 hover:bg-white/10 hover:text-white'
                    }`}
                  >
                    {mode.label}
                  </button>
                ))}
              </div>
            </div>

            {/* A-B Loop Section */}
            <div className="py-4 border-b border-white/5">
              <div className="flex items-center justify-between mb-3">
                <div className="flex items-center gap-2 text-zinc-300">
                  <Repeat className="w-4 h-4 text-amber-400" />
                  <span className="text-xs font-semibold">A-B Segment Looping</span>
                </div>
                {loopAB.active && (
                  <button
                    onClick={onClearLoopAB}
                    className="text-[11px] text-rose-400 hover:text-rose-300 underline"
                  >
                    Clear Loop
                  </button>
                )}
              </div>

              <div className="grid grid-cols-2 gap-3">
                <button
                  onClick={onSetPointA}
                  className={`p-3 rounded-2xl border text-center transition-all ${
                    loopAB.a !== null
                      ? 'bg-amber-500/20 border-amber-500/60 text-amber-300'
                      : 'bg-white/5 border-white/10 text-zinc-300 hover:bg-white/10'
                  }`}
                >
                  <div className="text-xs font-bold">Set Point [A]</div>
                  <div className="text-[11px] font-mono mt-0.5 text-zinc-400">
                    {loopAB.a !== null ? `${loopAB.a.toFixed(1)}s` : `Current: ${currentTime.toFixed(1)}s`}
                  </div>
                </button>

                <button
                  onClick={onSetPointB}
                  className={`p-3 rounded-2xl border text-center transition-all ${
                    loopAB.b !== null
                      ? 'bg-amber-500/20 border-amber-500/60 text-amber-300'
                      : 'bg-white/5 border-white/10 text-zinc-300 hover:bg-white/10'
                  }`}
                >
                  <div className="text-xs font-bold">Set Point [B]</div>
                  <div className="text-[11px] font-mono mt-0.5 text-zinc-400">
                    {loopAB.b !== null ? `${loopAB.b.toFixed(1)}s` : `Current: ${currentTime.toFixed(1)}s`}
                  </div>
                </button>
              </div>
            </div>

            {/* Visual & Audio Toggles */}
            <div className="pt-4 grid grid-cols-1 sm:grid-cols-2 gap-3">
              {/* Dynamic Ambient Backglow */}
              <div
                onClick={onAmbientGlowToggle}
                className={`flex items-center justify-between p-3.5 rounded-2xl border cursor-pointer transition-all ${
                  ambientGlow
                    ? 'bg-amber-500/15 border-amber-500/50 text-amber-300'
                    : 'bg-white/5 border-white/10 text-zinc-300 hover:bg-white/10'
                }`}
              >
                <div className="flex items-center gap-2.5">
                  <Sparkles className={`w-5 h-5 ${ambientGlow ? 'text-amber-400' : 'text-zinc-400'}`} />
                  <div>
                    <div className="text-xs font-semibold">Dynamic Ambient Glow</div>
                    <div className="text-[10px] text-zinc-400">Edge colors projected on backdrop</div>
                  </div>
                </div>
                <div
                  className={`w-4 h-4 rounded-full border flex items-center justify-center ${
                    ambientGlow ? 'border-amber-400 bg-amber-400' : 'border-zinc-500'
                  }`}
                >
                  {ambientGlow && <div className="w-1.5 h-1.5 rounded-full bg-black" />}
                </div>
              </div>

              {/* Background Audio Only Mode */}
              <div
                onClick={onAudioOnlyToggle}
                className={`flex items-center justify-between p-3.5 rounded-2xl border cursor-pointer transition-all ${
                  audioOnly
                    ? 'bg-amber-500/15 border-amber-500/50 text-amber-300'
                    : 'bg-white/5 border-white/10 text-zinc-300 hover:bg-white/10'
                }`}
              >
                <div className="flex items-center gap-2.5">
                  <Headphones className={`w-5 h-5 ${audioOnly ? 'text-amber-400' : 'text-zinc-400'}`} />
                  <div>
                    <div className="text-xs font-semibold">Audio-Only Mode</div>
                    <div className="text-[10px] text-zinc-400">Hides video frames to save battery</div>
                  </div>
                </div>
                <div
                  className={`w-4 h-4 rounded-full border flex items-center justify-center ${
                    audioOnly ? 'border-amber-400 bg-amber-400' : 'border-zinc-500'
                  }`}
                >
                  {audioOnly && <div className="w-1.5 h-1.5 rounded-full bg-black" />}
                </div>
              </div>
            </div>
          </motion.div>
        </>
      )}
    </AnimatePresence>
  );
};
