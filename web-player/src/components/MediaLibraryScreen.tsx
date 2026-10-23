import React, { useState } from 'react';
import {
  Folder,
  Film,
  Play,
  Upload,
  Search,
  Grid,
  List as ListIcon,
  Sparkles,
  CheckCircle2,
} from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import type { VideoItem } from '../types';
import { formatTime } from '../utils/subtitleParser';

interface MediaLibraryScreenProps {
  videos: VideoItem[];
  onSelectVideo: (video: VideoItem) => void;
  onImportFiles: (files: FileList) => void;
  onClearHistory: () => void;
}

export const MediaLibraryScreen: React.FC<MediaLibraryScreenProps> = ({
  videos,
  onSelectVideo,
  onImportFiles,
  onClearHistory,
}) => {
  const [selectedFolder, setSelectedFolder] = useState<string>('All Videos');
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [viewMode, setViewMode] = useState<'grid' | 'list'>('grid');

  // Extract unique folders
  const folders = ['All Videos', ...Array.from(new Set(videos.map((v) => v.folder || 'Downloads')))];

  // Filter videos
  const filteredVideos = videos.filter((v) => {
    const matchesFolder = selectedFolder === 'All Videos' || (v.folder || 'Downloads') === selectedFolder;
    const matchesQuery = v.title.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesFolder && matchesQuery;
  });

  return (
    <div className="min-h-screen bg-[#050505] text-white flex flex-col pb-16">
      {/* Top Header */}
      <header className="sticky top-0 z-30 bg-[#050505]/80 backdrop-blur-2xl border-b border-white/5 px-4 sm:px-8 py-4 flex flex-col gap-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-2xl bg-gradient-to-tr from-amber-600 to-amber-400 flex items-center justify-center shadow-lg shadow-amber-500/20">
              <Film className="w-5 h-5 text-black" />
            </div>
            <div>
              <h1 className="text-xl font-bold tracking-tight bg-gradient-to-r from-amber-400 via-amber-200 to-white bg-clip-text text-transparent">
                EMBER
              </h1>
              <div className="flex items-center gap-2">
                <span className="text-[10px] uppercase font-mono tracking-wider text-amber-400 font-semibold">
                  AMOLED Video Player
                </span>
                <span className="w-1 h-1 rounded-full bg-zinc-600" />
                <span className="text-[10px] text-zinc-400">Next-Gen Media Engine</span>
              </div>
            </div>
          </div>

          {/* Action Header Buttons */}
          <div className="flex items-center gap-2">
            {/* Native Folder Picker Button */}
            <label className="flex items-center gap-2 px-3.5 py-2 rounded-xl bg-amber-500 hover:bg-amber-400 text-black font-semibold text-xs transition-all shadow-lg shadow-amber-500/20 cursor-pointer active:scale-95">
              <Upload className="w-4 h-4" />
              <span className="hidden sm:inline">Add Videos / Folder</span>
              <input
                type="file"
                multiple
                accept="video/*"
                onChange={(e) => {
                  if (e.target.files) onImportFiles(e.target.files);
                }}
                className="hidden"
              />
            </label>

            {/* View Mode Toggle */}
            <div className="flex items-center bg-white/5 border border-white/10 rounded-xl p-0.5">
              <button
                onClick={() => setViewMode('grid')}
                className={`p-1.5 rounded-lg transition-colors ${
                  viewMode === 'grid' ? 'bg-amber-500 text-black' : 'text-zinc-400 hover:text-white'
                }`}
              >
                <Grid className="w-4 h-4" />
              </button>
              <button
                onClick={() => setViewMode('list')}
                className={`p-1.5 rounded-lg transition-colors ${
                  viewMode === 'list' ? 'bg-amber-500 text-black' : 'text-zinc-400 hover:text-white'
                }`}
              >
                <ListIcon className="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>

        {/* Search Bar */}
        <div className="relative w-full">
          <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-zinc-500" />
          <input
            type="text"
            placeholder="Search videos, clips, or formats..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full bg-[#121212] border border-white/10 rounded-2xl pl-10 pr-4 py-2.5 text-xs text-white placeholder-zinc-500 focus:outline-none focus:border-amber-500/50 transition-colors"
          />
        </div>

        {/* Folder Pills Horizontal Scroll */}
        <div className="flex items-center gap-2 overflow-x-auto pb-1 scrollbar-none">
          {folders.map((folder) => (
            <button
              key={folder}
              onClick={() => setSelectedFolder(folder)}
              className={`flex items-center gap-1.5 px-3.5 py-1.5 rounded-full text-xs font-medium whitespace-nowrap transition-all ${
                selectedFolder === folder
                  ? 'bg-amber-500/20 border border-amber-500/60 text-amber-300 font-semibold shadow-sm'
                  : 'bg-white/5 border border-white/5 text-zinc-400 hover:bg-white/10 hover:text-white'
              }`}
            >
              <Folder className="w-3.5 h-3.5" />
              <span>{folder}</span>
            </button>
          ))}
        </div>
      </header>

      {/* Main Content Area */}
      <main className="flex-1 px-4 sm:px-8 pt-6 max-w-7xl mx-auto w-full">
        {/* Banner with Feature Highlights */}
        <div className="mb-6 p-4 rounded-3xl bg-gradient-to-r from-amber-950/30 via-[#121212] to-amber-950/20 border border-amber-500/20 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="p-3 rounded-2xl bg-amber-500/10 text-amber-400">
              <Sparkles className="w-6 h-6" />
            </div>
            <div>
              <h2 className="text-sm font-semibold text-white">Full Gesture-Driven Engine Enabled</h2>
              <p className="text-xs text-zinc-400">
                Left swipe: Brightness • Right swipe: Volume up to 200% • Double-tap: ±10s Seek • Long-press: 2X Fast Forward
              </p>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <button
              onClick={onClearHistory}
              className="text-[11px] text-zinc-400 hover:text-amber-400 transition-colors underline"
            >
              Reset Watch History
            </button>
          </div>
        </div>

        {/* Videos Container */}
        {filteredVideos.length === 0 ? (
          <div className="py-20 flex flex-col items-center justify-center text-center">
            <Film className="w-12 h-12 text-zinc-600 mb-3" />
            <h3 className="text-base font-semibold text-zinc-300">No videos found</h3>
            <p className="text-xs text-zinc-500 max-w-sm mt-1">
              Import local video files using the top button or select a different folder.
            </p>
          </div>
        ) : viewMode === 'grid' ? (
          /* Grid View */
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
            <AnimatePresence>
              {filteredVideos.map((video) => {
                const progressPct =
                  video.duration && video.lastPlayed
                    ? Math.min(100, (video.lastPlayed / video.duration) * 100)
                    : 0;

                return (
                  <motion.div
                    key={video.id}
                    layout
                    initial={{ opacity: 0, y: 15 }}
                    animate={{ opacity: 1, y: 0 }}
                    exit={{ opacity: 0 }}
                    onClick={() => onSelectVideo(video)}
                    className="group relative bg-[#121212] border border-white/5 hover:border-amber-500/40 rounded-2xl overflow-hidden cursor-pointer transition-all duration-300 hover:shadow-2xl hover:shadow-amber-500/5 hover:-translate-y-1 flex flex-col"
                  >
                    {/* Thumbnail Container */}
                    <div className="relative aspect-video w-full bg-zinc-900 overflow-hidden">
                      {video.thumbnail ? (
                        <img
                          src={video.thumbnail}
                          alt={video.title}
                          className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
                        />
                      ) : (
                        <div className="w-full h-full flex items-center justify-center bg-zinc-950">
                          <Film className="w-10 h-10 text-zinc-700" />
                        </div>
                      )}

                      {/* Play Hover Overlay */}
                      <div className="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center">
                        <div className="w-12 h-12 rounded-full bg-amber-500 text-black flex items-center justify-center shadow-lg shadow-amber-500/30 transform scale-90 group-hover:scale-100 transition-transform">
                          <Play className="w-5 h-5 fill-black translate-x-0.5" />
                        </div>
                      </div>

                      {/* Duration Tag */}
                      {video.duration && (
                        <div className="absolute bottom-2 right-2 px-2 py-0.5 rounded-md bg-black/80 backdrop-blur-md text-[10px] font-mono text-zinc-300 font-medium">
                          {formatTime(video.duration)}
                        </div>
                      )}

                      {/* Completed badge */}
                      {video.completed && (
                        <div className="absolute top-2 left-2 flex items-center gap-1 bg-amber-500/90 text-black px-2 py-0.5 rounded-full text-[10px] font-bold">
                          <CheckCircle2 className="w-3 h-3" />
                          <span>Finished</span>
                        </div>
                      )}

                      {/* Saved Watch Progress Bar */}
                      {progressPct > 0 && !video.completed && (
                        <div className="absolute bottom-0 left-0 right-0 h-1 bg-white/20">
                          <div
                            className="h-full bg-amber-500 shadow-sm shadow-amber-400"
                            style={{ width: `${progressPct}%` }}
                          />
                        </div>
                      )}
                    </div>

                    {/* Metadata Card Footer */}
                    <div className="p-3.5 flex flex-col justify-between flex-1">
                      <h3 className="text-xs font-semibold text-white group-hover:text-amber-400 transition-colors line-clamp-2 leading-relaxed">
                        {video.title}
                      </h3>
                      <div className="flex items-center justify-between text-[11px] text-zinc-500 mt-2 font-mono">
                        <span>{video.folder || 'Downloads'}</span>
                        {video.lastPlayed && video.lastPlayed > 0 && (
                          <span className="text-amber-400/90">Resume {formatTime(video.lastPlayed)}</span>
                        )}
                      </div>
                    </div>
                  </motion.div>
                );
              })}
            </AnimatePresence>
          </div>
        ) : (
          /* List View */
          <div className="flex flex-col gap-2">
            <AnimatePresence>
              {filteredVideos.map((video) => {
                const progressPct =
                  video.duration && video.lastPlayed
                    ? Math.min(100, (video.lastPlayed / video.duration) * 100)
                    : 0;

                return (
                  <motion.div
                    key={video.id}
                    layout
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    exit={{ opacity: 0 }}
                    onClick={() => onSelectVideo(video)}
                    className="group bg-[#121212] border border-white/5 hover:border-amber-500/30 rounded-2xl p-2.5 flex items-center gap-3.5 cursor-pointer transition-all hover:bg-white/5"
                  >
                    <div className="relative w-28 aspect-video rounded-xl overflow-hidden bg-zinc-900 shrink-0">
                      {video.thumbnail ? (
                        <img src={video.thumbnail} alt={video.title} className="w-full h-full object-cover" />
                      ) : (
                        <div className="w-full h-full flex items-center justify-center bg-zinc-950">
                          <Film className="w-6 h-6 text-zinc-700" />
                        </div>
                      )}
                      {progressPct > 0 && !video.completed && (
                        <div className="absolute bottom-0 left-0 right-0 h-1 bg-white/20">
                          <div className="h-full bg-amber-500" style={{ width: `${progressPct}%` }} />
                        </div>
                      )}
                    </div>

                    <div className="flex-1 min-w-0">
                      <h3 className="text-xs font-semibold text-white group-hover:text-amber-400 transition-colors truncate">
                        {video.title}
                      </h3>
                      <div className="flex items-center gap-3 text-[11px] text-zinc-500 mt-1 font-mono">
                        <span>{video.folder || 'Downloads'}</span>
                        {video.duration && <span>• {formatTime(video.duration)}</span>}
                        {video.lastPlayed && video.lastPlayed > 0 && (
                          <span className="text-amber-400">• Resume at {formatTime(video.lastPlayed)}</span>
                        )}
                      </div>
                    </div>

                    <div className="w-8 h-8 rounded-full bg-white/5 group-hover:bg-amber-500 group-hover:text-black flex items-center justify-center transition-all text-zinc-400">
                      <Play className="w-3.5 h-3.5 fill-current translate-x-0.5" />
                    </div>
                  </motion.div>
                );
              })}
            </AnimatePresence>
          </div>
        )}
      </main>
    </div>
  );
};
