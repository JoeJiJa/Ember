import React from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X, Type, Upload } from 'lucide-react';
import type { SubtitleSettings } from '../types';

interface SubtitleDrawerProps {
  isOpen: boolean;
  onClose: () => void;
  settings: SubtitleSettings;
  onSettingsChange: (settings: SubtitleSettings) => void;
  onFileUpload: (e: React.ChangeEvent<HTMLInputElement>) => void;
  activeSubtitleName: string | null;
  onClearSubtitle: () => void;
}

export const SubtitleDrawer: React.FC<SubtitleDrawerProps> = ({
  isOpen,
  onClose,
  settings,
  onSettingsChange,
  onFileUpload,
  activeSubtitleName,
  onClearSubtitle,
}) => {
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
            className="fixed bottom-0 left-0 right-0 max-w-xl mx-auto bg-[#121212] border-t border-white/10 rounded-t-3xl p-6 z-50 shadow-2xl text-white"
          >
            {/* Header */}
            <div className="flex items-center justify-between pb-4 border-b border-white/10">
              <div className="flex items-center gap-2">
                <Type className="w-5 h-5 text-amber-400" />
                <h3 className="text-lg font-semibold tracking-wide">Subtitle Customization</h3>
              </div>
              <button
                onClick={onClose}
                className="p-2 hover:bg-white/10 rounded-full transition-colors text-zinc-400 hover:text-white"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {/* File Upload Section */}
            <div className="py-4">
              <label className="flex items-center justify-center gap-2.5 w-full p-3.5 border border-dashed border-amber-500/40 rounded-2xl bg-amber-500/5 hover:bg-amber-500/10 cursor-pointer transition-all">
                <Upload className="w-5 h-5 text-amber-400" />
                <span className="text-xs font-medium text-amber-300">
                  {activeSubtitleName ? `Active: ${activeSubtitleName}` : 'Load Subtitle File (.srt, .vtt)'}
                </span>
                <input
                  type="file"
                  accept=".srt,.vtt"
                  onChange={onFileUpload}
                  className="hidden"
                />
              </label>

              {activeSubtitleName && (
                <button
                  onClick={onClearSubtitle}
                  className="text-[11px] text-rose-400 hover:text-rose-300 underline mt-2 block mx-auto"
                >
                  Remove Subtitles
                </button>
              )}
            </div>

            {/* Subtitle Appearance Settings */}
            <div className="space-y-4 pt-2">
              {/* Font Size */}
              <div className="flex flex-col gap-1.5">
                <div className="flex justify-between text-xs text-zinc-300">
                  <span>Font Size</span>
                  <span className="font-mono text-amber-400">{settings.fontSize}px</span>
                </div>
                <input
                  type="range"
                  min={12}
                  max={36}
                  value={settings.fontSize}
                  onChange={(e) =>
                    onSettingsChange({ ...settings, fontSize: parseInt(e.target.value, 10) })
                  }
                  className="w-full accent-amber-500 cursor-pointer"
                />
              </div>

              {/* Vertical Position */}
              <div className="flex flex-col gap-1.5">
                <div className="flex justify-between text-xs text-zinc-300">
                  <span>Vertical Offset (From Bottom)</span>
                  <span className="font-mono text-amber-400">{settings.offsetY}%</span>
                </div>
                <input
                  type="range"
                  min={2}
                  max={40}
                  value={settings.offsetY}
                  onChange={(e) =>
                    onSettingsChange({ ...settings, offsetY: parseInt(e.target.value, 10) })
                  }
                  className="w-full accent-amber-500 cursor-pointer"
                />
              </div>

              {/* Subtitle Color Options */}
              <div className="flex items-center justify-between pt-2">
                <span className="text-xs text-zinc-300">Text Color</span>
                <div className="flex gap-2">
                  {['#ffffff', '#fef08a', '#6ee7b7', '#93c5fd'].map((color) => (
                    <button
                      key={color}
                      onClick={() => onSettingsChange({ ...settings, color })}
                      style={{ backgroundColor: color }}
                      className={`w-6 h-6 rounded-full border-2 transition-transform active:scale-90 ${
                        settings.color === color ? 'border-amber-400 scale-110 shadow-lg' : 'border-transparent'
                      }`}
                    />
                  ))}
                </div>
              </div>

              {/* Live Preview Box */}
              <div className="mt-4 p-4 rounded-xl bg-black/60 border border-white/5 flex items-center justify-center min-h-[60px] text-center">
                <span
                  style={{
                    fontSize: `${settings.fontSize}px`,
                    color: settings.color,
                    textShadow: '0 2px 4px rgba(0,0,0,0.9)',
                  }}
                  className="font-medium px-2 py-0.5 rounded bg-black/60"
                >
                  Sample Subtitle: "Welcome to NextPlayer Modernized"
                </span>
              </div>
            </div>
          </motion.div>
        </>
      )}
    </AnimatePresence>
  );
};
