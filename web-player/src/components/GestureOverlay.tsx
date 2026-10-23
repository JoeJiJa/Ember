import React from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Sun, Volume2, VolumeX, FastForward, Rewind } from 'lucide-react';

interface GestureOverlayProps {
  brightness: number | null; // 0 - 100
  volume: number | null; // 0 - 200
  seekFeedback: { type: 'forward' | 'rewind'; amount: number; targetTime: string } | null;
  isLongPressing2x: boolean;
  currentSpeed?: number;
  ripple: { x: number; y: number; id: number; type: 'forward' | 'rewind' } | null;
}

export const GestureOverlay: React.FC<GestureOverlayProps> = ({
  brightness,
  volume,
  seekFeedback,
  isLongPressing2x,
  currentSpeed = 2.0,
  ripple,
}) => {
  return (
    <div className="absolute inset-0 pointer-events-none z-30 flex items-center justify-center">
      {/* Speed Indicator on Long Press + Right Swipe Transition */}
      <AnimatePresence>
        {isLongPressing2x && (
          <motion.div
            initial={{ opacity: 0, scale: 0.8, y: -20 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.8 }}
            className="absolute top-16 bg-black/85 backdrop-blur-xl border border-amber-500/50 text-amber-400 px-5 py-2 rounded-full flex flex-col items-center gap-0.5 shadow-2xl shadow-amber-500/20"
          >
            <div className="flex items-center gap-2 text-sm font-bold tracking-wider">
              <FastForward className="w-4 h-4 animate-pulse text-amber-400" />
              <span className="font-mono text-base">{currentSpeed.toFixed(currentSpeed % 1 === 0 ? 1 : 2)}X SPEED</span>
            </div>
            <span className="text-[10px] font-medium text-amber-200/80 tracking-wide">
              {currentSpeed > 2 ? 'Swipe ➔ to 5.0X' : 'Hold & swipe right to increase speed'}
            </span>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Brightness Vertical Slider Pill (Left Side) */}
      <AnimatePresence>
        {brightness !== null && (
          <motion.div
            initial={{ opacity: 0, x: -30 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: -30 }}
            className="absolute left-8 flex flex-col items-center bg-black/75 backdrop-blur-xl border border-white/10 rounded-2xl p-3 w-14 shadow-2xl"
          >
            <Sun className="w-5 h-5 text-amber-400 mb-2" />
            <div className="w-2.5 h-32 bg-white/15 rounded-full overflow-hidden flex flex-col justify-end p-0.5">
              <div
                className="w-full bg-gradient-to-t from-amber-500 to-amber-300 rounded-full transition-all duration-75"
                style={{ height: `${brightness}%` }}
              />
            </div>
            <span className="text-xs font-mono font-medium text-white/90 mt-2">
              {Math.round(brightness)}%
            </span>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Volume / Audio Boost Vertical Slider Pill (Right Side) */}
      <AnimatePresence>
        {volume !== null && (
          <motion.div
            initial={{ opacity: 0, x: 30 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: 30 }}
            className="absolute right-8 flex flex-col items-center bg-black/75 backdrop-blur-xl border border-white/10 rounded-2xl p-3 w-14 shadow-2xl"
          >
            {volume === 0 ? (
              <VolumeX className="w-5 h-5 text-zinc-400 mb-2" />
            ) : (
              <Volume2 className={`w-5 h-5 mb-2 ${volume > 100 ? 'text-rose-400 animate-pulse' : 'text-amber-400'}`} />
            )}
            <div className="w-2.5 h-32 bg-white/15 rounded-full overflow-hidden flex flex-col justify-end p-0.5 relative">
              {/* >100% Boost indicator mark */}
              <div className="absolute top-[50%] left-0 right-0 h-[1px] bg-rose-500/50" />
              <div
                className={`w-full rounded-full transition-all duration-75 ${
                  volume > 100
                    ? 'bg-gradient-to-t from-amber-500 via-amber-300 to-rose-500'
                    : 'bg-gradient-to-t from-amber-500 to-amber-300'
                }`}
                style={{ height: `${Math.min(100, (volume / 200) * 100)}%` }}
              />
            </div>
            <span
              className={`text-xs font-mono font-medium mt-2 ${
                volume > 100 ? 'text-rose-400 font-bold' : 'text-white/90'
              }`}
            >
              {Math.round(volume)}%
            </span>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Horizontal Scrub Seek Feedback Bubble */}
      <AnimatePresence>
        {seekFeedback && (
          <motion.div
            initial={{ opacity: 0, scale: 0.9 }}
            animate={{ opacity: 1, scale: 1 }}
            exit={{ opacity: 0, scale: 0.9 }}
            className="bg-black/85 backdrop-blur-2xl border border-amber-500/30 rounded-2xl px-6 py-4 flex flex-col items-center gap-1 shadow-2xl text-center"
          >
            <div className="flex items-center gap-2 text-amber-400 font-medium">
              {seekFeedback.type === 'forward' ? (
                <>
                  <FastForward className="w-5 h-5" />
                  <span>+{seekFeedback.amount}s</span>
                </>
              ) : (
                <>
                  <Rewind className="w-5 h-5" />
                  <span>-{seekFeedback.amount}s</span>
                </>
              )}
            </div>
            <div className="text-xl font-mono font-bold text-white tracking-wide">
              {seekFeedback.targetTime}
            </div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Double Tap Ripple Animation */}
      <AnimatePresence>
        {ripple && (
          <motion.div
            key={ripple.id}
            initial={{ opacity: 0.9, scale: 0.3 }}
            animate={{ opacity: 0, scale: 2.5 }}
            exit={{ opacity: 0 }}
            transition={{ duration: 0.55, ease: 'easeOut' }}
            className="absolute rounded-full pointer-events-none flex items-center justify-center border border-amber-500/40 bg-amber-500/15 backdrop-blur-xs w-28 h-28"
            style={{ left: ripple.x - 56, top: ripple.y - 56 }}
          >
            {ripple.type === 'forward' ? (
              <FastForward className="w-10 h-10 text-amber-400" />
            ) : (
              <Rewind className="w-10 h-10 text-amber-400" />
            )}
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
};
