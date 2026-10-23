import React from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X, Sliders, Moon, RotateCcw } from 'lucide-react';
import { EQ_PRESETS } from '../constants';
import { audioEngine } from '../utils/audioEngine';

interface AudioEqualizerDrawerProps {
  isOpen: boolean;
  onClose: () => void;
  gains: number[];
  onGainChange: (index: number, val: number) => void;
  nightMode: boolean;
  onNightModeToggle: () => void;
  audioBoost: number;
  onAudioBoostChange: (val: number) => void;
}

export const AudioEqualizerDrawer: React.FC<AudioEqualizerDrawerProps> = ({
  isOpen,
  onClose,
  gains,
  onGainChange,
  nightMode,
  onNightModeToggle,
  audioBoost,
  onAudioBoostChange,
}) => {
  const frequencies = ['60 Hz', '230 Hz', '910 Hz', '3.6 kHz', '14 kHz'];

  const handleApplyPreset = (presetGains: number[]) => {
    presetGains.forEach((gain, idx) => {
      onGainChange(idx, gain);
      audioEngine.setEqGain(idx, gain);
    });
  };

  const resetEq = () => {
    handleApplyPreset([0, 0, 0, 0, 0]);
  };

  return (
    <AnimatePresence>
      {isOpen && (
        <>
          {/* Backdrop */}
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={onClose}
            className="fixed inset-0 bg-black/60 backdrop-blur-sm z-50"
          />

          {/* Drawer Sheet */}
          <motion.div
            initial={{ y: '100%' }}
            animate={{ y: 0 }}
            exit={{ y: '100%' }}
            transition={{ type: 'spring', damping: 28, stiffness: 260 }}
            className="fixed bottom-0 left-0 right-0 max-w-xl mx-auto bg-[#121212] border-t border-white/10 rounded-t-3xl p-6 z-50 shadow-2xl text-white"
          >
            {/* Header */}
            <div className="flex items-center justify-between pb-4 border-b border-white/10">
              <div className="flex items-center gap-2">
                <Sliders className="w-5 h-5 text-amber-400" />
                <h3 className="text-lg font-semibold tracking-wide">Audio Equalizer & Studio</h3>
              </div>
              <div className="flex items-center gap-2">
                <button
                  onClick={resetEq}
                  title="Reset EQ"
                  className="p-2 hover:bg-white/10 rounded-full transition-colors text-zinc-400 hover:text-white"
                >
                  <RotateCcw className="w-4 h-4" />
                </button>
                <button
                  onClick={onClose}
                  className="p-2 hover:bg-white/10 rounded-full transition-colors text-zinc-400 hover:text-white"
                >
                  <X className="w-5 h-5" />
                </button>
              </div>
            </div>

            {/* Presets Pill Carousel */}
            <div className="py-4">
              <span className="text-xs uppercase font-mono tracking-wider text-zinc-400 block mb-2">
                EQ Sound Profiles
              </span>
              <div className="flex items-center gap-2 overflow-x-auto pb-1 scrollbar-none">
                {EQ_PRESETS.map((preset) => (
                  <button
                    key={preset.name}
                    onClick={() => handleApplyPreset(preset.gains)}
                    className="px-3.5 py-1.5 text-xs font-medium rounded-full bg-white/5 border border-white/10 hover:border-amber-500/50 hover:bg-amber-500/10 hover:text-amber-300 transition-all whitespace-nowrap active:scale-95"
                  >
                    {preset.name}
                  </button>
                ))}
              </div>
            </div>

            {/* 5-Band Sliders Grid */}
            <div className="grid grid-cols-5 gap-3 py-4 bg-black/40 rounded-2xl p-4 border border-white/5">
              {frequencies.map((freq, idx) => (
                <div key={freq} className="flex flex-col items-center gap-2">
                  <span className="text-[11px] font-mono text-amber-400 font-semibold">
                    {gains[idx] > 0 ? `+${gains[idx]}` : gains[idx]}dB
                  </span>
                  <div className="h-32 flex items-center justify-center">
                    <input
                      type="range"
                      min={-12}
                      max={12}
                      step={1}
                      value={gains[idx]}
                      onChange={(e) => {
                        const val = parseFloat(e.target.value);
                        onGainChange(idx, val);
                        audioEngine.setEqGain(idx, val);
                      }}
                      className="w-28 -rotate-90 origin-center cursor-pointer accent-amber-500"
                    />
                  </div>
                  <span className="text-[11px] text-zinc-400 font-mono mt-1">{freq}</span>
                </div>
              ))}
            </div>

            {/* Audio Boost & Night Mode Toggles */}
            <div className="mt-5 grid grid-cols-1 sm:grid-cols-2 gap-3">
              {/* Night Mode Compressor */}
              <div
                onClick={onNightModeToggle}
                className={`flex items-center justify-between p-3.5 rounded-2xl border cursor-pointer transition-all ${
                  nightMode
                    ? 'bg-amber-500/15 border-amber-500/50 text-amber-300'
                    : 'bg-white/5 border-white/10 text-zinc-300 hover:bg-white/10'
                }`}
              >
                <div className="flex items-center gap-2.5">
                  <Moon className={`w-5 h-5 ${nightMode ? 'text-amber-400' : 'text-zinc-400'}`} />
                  <div>
                    <div className="text-xs font-semibold">Night Mode Compressor</div>
                    <div className="text-[10px] text-zinc-400">Softens loud explosions, elevates quiet whispers</div>
                  </div>
                </div>
                <div
                  className={`w-4 h-4 rounded-full border flex items-center justify-center ${
                    nightMode ? 'border-amber-400 bg-amber-400' : 'border-zinc-500'
                  }`}
                >
                  {nightMode && <div className="w-1.5 h-1.5 rounded-full bg-black" />}
                </div>
              </div>

              {/* Audio Boost Slider (Up to 200%) */}
              <div className="p-3.5 rounded-2xl bg-white/5 border border-white/10 flex flex-col gap-1.5">
                <div className="flex items-center justify-between">
                  <span className="text-xs font-semibold text-zinc-300">Volume Gain Boost</span>
                  <span
                    className={`text-xs font-mono font-bold ${
                      audioBoost > 100 ? 'text-rose-400 animate-pulse' : 'text-amber-400'
                    }`}
                  >
                    {Math.round(audioBoost)}%
                  </span>
                </div>
                <input
                  type="range"
                  min={0}
                  max={200}
                  step={5}
                  value={audioBoost}
                  onChange={(e) => {
                    const val = parseFloat(e.target.value);
                    onAudioBoostChange(val);
                  }}
                  className="w-full accent-amber-500 cursor-pointer"
                />
              </div>
            </div>
          </motion.div>
        </>
      )}
    </AnimatePresence>
  );
};
