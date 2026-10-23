import React, { useRef, useState, useEffect, useCallback } from 'react';
import {
  ArrowLeft,
  Play,
  Pause,
  RotateCcw,
  RotateCw,
  Lock,
  Unlock,
  Sliders,
  Settings,
  Type,
  Maximize,
  Minimize,
  Camera,
  StepBack,
  StepForward,
  PictureInPicture,
} from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import type { VideoItem, AspectRatioMode, SubtitleSettings } from '../types';
import { formatTime, parseSrt } from '../utils/subtitleParser';
import type { SubtitleCue } from '../utils/subtitleParser';
import { audioEngine } from '../utils/audioEngine';
import { AmbientGlowCanvas } from './AmbientGlowCanvas';
import { GestureOverlay } from './GestureOverlay';
import { AudioEqualizerDrawer } from './AudioEqualizerDrawer';
import { SubtitleDrawer } from './SubtitleDrawer';
import { PlaybackSettingsDrawer } from './PlaybackSettingsDrawer';
import confetti from 'canvas-confetti';

interface VideoPlayerScreenProps {
  video: VideoItem;
  onBack: () => void;
  onUpdateWatchProgress: (id: string, time: number, completed: boolean) => void;
}

export const VideoPlayerScreen: React.FC<VideoPlayerScreenProps> = ({
  video,
  onBack,
  onUpdateWatchProgress,
}) => {
  const containerRef = useRef<HTMLDivElement | null>(null);
  const videoRef = useRef<HTMLVideoElement | null>(null);

  // Core Playback State
  const [isPlaying, setIsPlaying] = useState<boolean>(true);
  const [currentTime, setCurrentTime] = useState<number>(video.lastPlayed || 0);
  const [duration, setDuration] = useState<number>(video.duration || 0);
  const [playbackSpeed, setPlaybackSpeed] = useState<number>(1.0);
  const [aspectRatio, setAspectRatio] = useState<AspectRatioMode>('fit');
  const [isLocked, setIsLocked] = useState<boolean>(false);
  const [controlsVisible, setControlsVisible] = useState<boolean>(true);
  const [isFullscreen, setIsFullscreen] = useState<boolean>(false);
  const [ambientGlow, setAmbientGlow] = useState<boolean>(true);
  const [audioOnly, setAudioOnly] = useState<boolean>(false);

  // Gesture Engine States
  const [brightness, setBrightness] = useState<number>(100);
  const [activeBrightnessOverlay, setActiveBrightnessOverlay] = useState<number | null>(null);
  const [audioBoostVolume, setAudioBoostVolume] = useState<number>(100);
  const [activeVolumeOverlay, setActiveVolumeOverlay] = useState<number | null>(null);
  const [seekFeedback, setSeekFeedback] = useState<{
    type: 'forward' | 'rewind';
    amount: number;
    targetTime: string;
  } | null>(null);
  const [isLongPressing2x, setIsLongPressing2x] = useState<boolean>(false);
  const [dynamicHoldSpeed, setDynamicHoldSpeed] = useState<number>(2.0);
  const [ripple, setRipple] = useState<{
    x: number;
    y: number;
    id: number;
    type: 'forward' | 'rewind';
  } | null>(null);

  // Drawers
  const [isEqDrawerOpen, setIsEqDrawerOpen] = useState<boolean>(false);
  const [isSubDrawerOpen, setIsSubDrawerOpen] = useState<boolean>(false);
  const [isSettingsDrawerOpen, setIsSettingsDrawerOpen] = useState<boolean>(false);

  // Audio Equalizer & Night mode
  const [eqGains, setEqGains] = useState<number[]>([0, 0, 0, 0, 0]);
  const [nightMode, setNightMode] = useState<boolean>(false);

  // Subtitles
  const [subtitleCues, setSubtitleCues] = useState<SubtitleCue[]>([]);
  const [currentSubtitleText, setCurrentSubtitleText] = useState<string>('');
  const [activeSubName, setActiveSubName] = useState<string | null>(null);
  const [subSettings, setSubSettings] = useState<SubtitleSettings>({
    fontSize: 20,
    color: '#ffffff',
    bgColor: 'rgba(0,0,0,0.6)',
    offsetY: 8,
  });

  // A-B Looping
  const [loopAB, setLoopAB] = useState<{ a: number | null; b: number | null; active: boolean }>({
    a: null,
    b: null,
    active: false,
  });

  // Snapshot flash animation
  const [snapshotFlash, setSnapshotFlash] = useState<boolean>(false);

  // Inactivity timeout for controls
  const controlsTimeoutRef = useRef<number | null>(null);

  const resetControlsTimeout = useCallback(() => {
    if (controlsTimeoutRef.current) clearTimeout(controlsTimeoutRef.current);
    if (!isLocked) {
      setControlsVisible(true);
      controlsTimeoutRef.current = window.setTimeout(() => {
        if (isPlaying) setControlsVisible(false);
      }, 4000);
    }
  }, [isLocked, isPlaying]);

  // Video initialization and audio routing
  useEffect(() => {
    const vid = videoRef.current;
    if (!vid) return;

    audioEngine.init(vid);

    const onPlay = () => {
      setIsPlaying(true);
      audioEngine.resume();
    };
    const onPause = () => setIsPlaying(false);
    const onLoadedMetadata = () => {
      setDuration(vid.duration || 0);
      if (video.lastPlayed && video.lastPlayed > 0 && video.lastPlayed < vid.duration) {
        vid.currentTime = video.lastPlayed;
      }
    };
    const onTimeUpdate = () => {
      setCurrentTime(vid.currentTime);
      // Check A-B loop boundary
      if (loopAB.active && loopAB.a !== null && loopAB.b !== null) {
        if (vid.currentTime >= loopAB.b) {
          vid.currentTime = loopAB.a;
        }
      }

      // Update subtitle cue matching
      if (subtitleCues.length > 0) {
        const cur = vid.currentTime;
        const matching = subtitleCues.find((c) => cur >= c.start && cur <= c.end);
        setCurrentSubtitleText(matching ? matching.text : '');
      }

      // Save watch history periodically
      onUpdateWatchProgress(
        video.id,
        vid.currentTime,
        vid.duration > 0 && vid.currentTime / vid.duration > 0.95
      );
    };

    vid.addEventListener('play', onPlay);
    vid.addEventListener('pause', onPause);
    vid.addEventListener('loadedmetadata', onLoadedMetadata);
    vid.addEventListener('timeupdate', onTimeUpdate);

    return () => {
      vid.removeEventListener('play', onPlay);
      vid.removeEventListener('pause', onPause);
      vid.removeEventListener('loadedmetadata', onLoadedMetadata);
      vid.removeEventListener('timeupdate', onTimeUpdate);
    };
  }, [video, loopAB, subtitleCues, onUpdateWatchProgress]);

  // Touch / Pointer Gesture Handling
  const touchStartRef = useRef<{ x: number; y: number; time: number; target: 'left' | 'right' | 'center' } | null>(null);
  const longPressTimeoutRef = useRef<number | null>(null);
  const isDraggingGesture = useRef<boolean>(false);
  const lastTapRef = useRef<{ time: number; x: number; y: number } | null>(null);

  const handlePointerDown = (e: React.PointerEvent<HTMLDivElement>) => {
    if (isLocked) return;

    resetControlsTimeout();
    const rect = e.currentTarget.getBoundingClientRect();
    const relX = e.clientX - rect.left;
    const side = relX < rect.width * 0.4 ? 'left' : relX > rect.width * 0.6 ? 'right' : 'center';

    touchStartRef.current = {
      x: e.clientX,
      y: e.clientY,
      time: Date.now(),
      target: side,
    };
    isDraggingGesture.current = false;

    // Trigger 2x Speed Long Press after 350ms
    longPressTimeoutRef.current = window.setTimeout(() => {
      if (!isDraggingGesture.current && videoRef.current) {
        setIsLongPressing2x(true);
        setDynamicHoldSpeed(2.0);
        videoRef.current.playbackRate = 2.0;
      }
    }, 350);
  };

  const handlePointerMove = (e: React.PointerEvent<HTMLDivElement>) => {
    if (isLocked || !touchStartRef.current) return;

    const deltaX = e.clientX - touchStartRef.current.x;
    const deltaY = e.clientY - touchStartRef.current.y;

    // If user is currently in Long-Press Hold mode and swiping to the right:
    if (isLongPressing2x) {
      if (deltaX > 0) {
        // Map swipe distance to speed steps: [2.0, 2.5, 2.75, 3.0, 3.25, 3.5, 3.75, 4.0, 4.25, 4.5, 4.75, 5.0]
        const speedSteps = [2.0, 2.5, 2.75, 3.0, 3.25, 3.5, 3.75, 4.0, 4.25, 4.5, 4.75, 5.0];
        const stepIndex = Math.min(
          speedSteps.length - 1,
          Math.max(0, Math.floor(deltaX / 28))
        );
        const targetSpeed = speedSteps[stepIndex];

        setDynamicHoldSpeed(targetSpeed);
        if (videoRef.current) {
          videoRef.current.playbackRate = targetSpeed;
        }
      } else {
        // Resting or moving back left returns to base 2.0X
        setDynamicHoldSpeed(2.0);
        if (videoRef.current) {
          videoRef.current.playbackRate = 2.0;
        }
      }
      return;
    }

    if (Math.abs(deltaX) > 10 || Math.abs(deltaY) > 10) {
      isDraggingGesture.current = true;
      if (longPressTimeoutRef.current) clearTimeout(longPressTimeoutRef.current);
    }

    // Vertical drag: Left side = Brightness, Right side = Volume
    if (Math.abs(deltaY) > Math.abs(deltaX)) {
      const step = (-deltaY / 150) * 100;
      if (touchStartRef.current.target === 'left') {
        const newBright = Math.min(100, Math.max(10, brightness + step * 0.2));
        setBrightness(newBright);
        setActiveBrightnessOverlay(newBright);
      } else if (touchStartRef.current.target === 'right') {
        const newVol = Math.min(200, Math.max(0, audioBoostVolume + step * 0.3));
        setAudioBoostVolume(newVol);
        setActiveVolumeOverlay(newVol);
        audioEngine.setVolumeBoost(newVol / 100);
      }
    } else if (Math.abs(deltaX) > 25 && duration > 0) {
      // Horizontal drag: Video scrubbing
      const scrubDiff = Math.round((deltaX / 300) * 60);
      const target = Math.min(duration, Math.max(0, currentTime + scrubDiff));
      setSeekFeedback({
        type: scrubDiff >= 0 ? 'forward' : 'rewind',
        amount: Math.abs(scrubDiff),
        targetTime: formatTime(target),
      });
    }
  };

  const handlePointerUp = (e: React.PointerEvent<HTMLDivElement>) => {
    if (longPressTimeoutRef.current) clearTimeout(longPressTimeoutRef.current);

    // Cancel dynamic hold speed and restore user's default playback speed
    if (isLongPressing2x && videoRef.current) {
      setIsLongPressing2x(false);
      setDynamicHoldSpeed(2.0);
      videoRef.current.playbackRate = playbackSpeed;
    }

    // Apply scrub seek if user was dragging horizontally
    if (seekFeedback && videoRef.current) {
      const deltaX = e.clientX - (touchStartRef.current?.x || e.clientX);
      const scrubDiff = Math.round((deltaX / 300) * 60);
      const target = Math.min(duration, Math.max(0, currentTime + scrubDiff));
      videoRef.current.currentTime = target;
      setSeekFeedback(null);
    }

    // Hide slider pills after short delay
    setTimeout(() => {
      setActiveBrightnessOverlay(null);
      setActiveVolumeOverlay(null);
    }, 800);

    // Check for Double-Tap (-10s / +10s)
    if (!isDraggingGesture.current && touchStartRef.current) {
      const now = Date.now();
      const rect = e.currentTarget.getBoundingClientRect();
      const clickX = e.clientX - rect.left;
      const clickY = e.clientY - rect.top;

      if (lastTapRef.current && now - lastTapRef.current.time < 300) {
        // Double tap confirmed
        const isRightHalf = clickX > rect.width * 0.5;
        const seekAmount = isRightHalf ? 10 : -10;
        if (videoRef.current) {
          videoRef.current.currentTime = Math.min(
            duration,
            Math.max(0, videoRef.current.currentTime + seekAmount)
          );
        }
        setRipple({
          x: clickX,
          y: clickY,
          id: now,
          type: isRightHalf ? 'forward' : 'rewind',
        });
        setTimeout(() => setRipple(null), 600);
        lastTapRef.current = null;
      } else {
        lastTapRef.current = { time: now, x: clickX, y: clickY };
      }
    }

    touchStartRef.current = null;
    isDraggingGesture.current = false;
  };

  // Play/Pause Toggle
  const togglePlay = () => {
    const vid = videoRef.current;
    if (!vid) return;
    if (vid.paused) {
      vid.play();
    } else {
      vid.pause();
    }
  };

  // Seek relative
  const seekRelative = (seconds: number) => {
    if (!videoRef.current) return;
    videoRef.current.currentTime = Math.min(
      duration,
      Math.max(0, videoRef.current.currentTime + seconds)
    );
  };

  // Step Frame
  const stepFrame = (frames: number) => {
    if (!videoRef.current) return;
    videoRef.current.pause();
    videoRef.current.currentTime = Math.min(
      duration,
      Math.max(0, videoRef.current.currentTime + frames * (1 / 30))
    );
  };

  // One-Tap Screenshot Capture
  const captureScreenshot = () => {
    const vid = videoRef.current;
    if (!vid) return;
    try {
      const canvas = document.createElement('canvas');
      canvas.width = vid.videoWidth || 1920;
      canvas.height = vid.videoHeight || 1080;
      const ctx = canvas.getContext('2d');
      if (ctx) {
        ctx.drawImage(vid, 0, 0, canvas.width, canvas.height);
        const link = document.createElement('a');
        link.download = `EMBER_${video.title.replace(/\s+/g, '_')}_${Math.round(currentTime)}s.png`;
        link.href = canvas.toDataURL('image/png');
        link.click();
        setSnapshotFlash(true);
        setTimeout(() => setSnapshotFlash(false), 300);
        confetti({ particleCount: 30, spread: 60, origin: { y: 0.8 } });
      }
    } catch (e) {
      console.warn('Screenshot capture failed (likely CORS on remote stream):', e);
      setSnapshotFlash(true);
      setTimeout(() => setSnapshotFlash(false), 300);
    }
  };

  // Picture in Picture
  const togglePiP = async () => {
    const vid = videoRef.current;
    if (!vid) return;
    try {
      if (document.pictureInPictureElement) {
        await document.exitPictureInPicture();
      } else if (vid.requestPictureInPicture) {
        await vid.requestPictureInPicture();
      }
    } catch (e) {
      console.warn('PiP error:', e);
    }
  };

  // Subtitle File Upload
  const handleSubtitleUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setActiveSubName(file.name);
    const reader = new FileReader();
    reader.onload = (evt) => {
      const content = evt.target?.result as string;
      if (content) {
        const parsed = parseSrt(content);
        setSubtitleCues(parsed);
      }
    };
    reader.readAsText(file);
  };

  // Aspect Ratio CSS Class Mapping
  const getAspectRatioClass = () => {
    switch (aspectRatio) {
      case 'fill':
        return 'object-cover w-full h-full';
      case 'zoom':
        return 'object-cover w-full h-full scale-125';
      case '16-9':
        return 'aspect-video w-full h-auto object-cover';
      case '4-3':
        return 'aspect-[4/3] max-w-full max-h-full object-cover';
      case 'stretch':
        return 'w-full h-full object-fill';
      case 'fit':
      default:
        return 'object-contain w-full h-full';
    }
  };

  return (
    <div
      ref={containerRef}
      onMouseMove={resetControlsTimeout}
      className="relative w-screen h-screen bg-[#050505] overflow-hidden flex items-center justify-center select-none"
      style={{ filter: `brightness(${brightness}%)` }}
    >
      {/* Dynamic Edge-Synced Backglow Canvas */}
      <AmbientGlowCanvas videoRef={videoRef} enabled={ambientGlow && !audioOnly} />

      {/* Snapshot Flash Overlay */}
      {snapshotFlash && (
        <div className="absolute inset-0 bg-white pointer-events-none z-50 animate-out fade-out duration-300" />
      )}

      {/* Touch & Gesture Capture Layer */}
      <div
        onPointerDown={handlePointerDown}
        onPointerMove={handlePointerMove}
        onPointerUp={handlePointerUp}
        onClick={() => {
          if (!isLocked) setControlsVisible((prev) => !prev);
        }}
        className="absolute inset-0 z-10 cursor-pointer"
      />

      {/* Gesture Overlays (Volume, Brightness, Scrub Feedback, 2X-5X Pill, Ripples) */}
      <GestureOverlay
        brightness={activeBrightnessOverlay}
        volume={activeVolumeOverlay}
        seekFeedback={seekFeedback}
        isLongPressing2x={isLongPressing2x}
        currentSpeed={dynamicHoldSpeed}
        ripple={ripple}
      />

      {/* HTML5 Video Element */}
      <video
        ref={videoRef}
        src={video.url}
        autoPlay
        playsInline
        className={`relative z-0 transition-transform duration-200 pointer-events-none ${
          audioOnly ? 'opacity-0' : 'opacity-100'
        } ${getAspectRatioClass()}`}
      />

      {/* Audio Only Mode Visualizer Placeholder */}
      {audioOnly && (
        <div className="absolute inset-0 flex flex-col items-center justify-center z-5 pointer-events-none text-zinc-400">
          <div className="w-24 h-24 rounded-full bg-amber-500/10 border border-amber-500/30 flex items-center justify-center animate-pulse">
            <Sliders className="w-10 h-10 text-amber-400" />
          </div>
          <span className="mt-4 text-sm font-semibold tracking-wide text-zinc-300">
            Audio-Only Mode Active
          </span>
          <span className="text-xs text-zinc-500">Screen power conserved</span>
        </div>
      )}

      {/* Custom Subtitles Display */}
      {currentSubtitleText && !audioOnly && (
        <div
          className="absolute left-0 right-0 z-20 flex justify-center px-6 pointer-events-none text-center"
          style={{ bottom: `${subSettings.offsetY}%` }}
        >
          <span
            style={{
              fontSize: `${subSettings.fontSize}px`,
              color: subSettings.color,
              backgroundColor: subSettings.bgColor,
              textShadow: '0 2px 5px rgba(0,0,0,0.95)',
            }}
            className="px-3 py-1 rounded-xl font-medium tracking-wide leading-relaxed max-w-2xl backdrop-blur-xs"
          >
            {currentSubtitleText}
          </span>
        </div>
      )}

      {/* Screen Lock Toggle Button (Always visible when locked) */}
      {isLocked && (
        <motion.button
          initial={{ scale: 0.8, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          onClick={() => setIsLocked(false)}
          className="absolute top-6 right-6 z-40 p-3.5 rounded-full bg-black/80 backdrop-blur-xl border border-amber-500/50 text-amber-400 shadow-2xl shadow-amber-500/20 active:scale-95"
        >
          <Unlock className="w-6 h-6" />
        </motion.button>
      )}

      {/* Floating Glassmorphic Player UI Controls */}
      <AnimatePresence>
        {controlsVisible && !isLocked && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            transition={{ duration: 0.2 }}
            className="absolute inset-0 z-30 flex flex-col justify-between p-4 sm:p-6 bg-gradient-to-t from-black/80 via-transparent to-black/80 pointer-events-auto"
          >
            {/* Top Bar */}
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <button
                  onClick={onBack}
                  className="p-2.5 rounded-full bg-black/50 hover:bg-white/15 backdrop-blur-xl border border-white/10 text-white transition-all active:scale-90"
                >
                  <ArrowLeft className="w-5 h-5" />
                </button>
                <div className="flex flex-col">
                  <h2 className="text-sm sm:text-base font-semibold text-white tracking-wide truncate max-w-[200px] sm:max-w-md">
                    {video.title}
                  </h2>
                  <span className="text-[11px] font-mono text-zinc-400">
                    {video.folder || 'NextPlayer Media'}
                  </span>
                </div>
              </div>

              {/* Action Buttons Top Right */}
              <div className="flex items-center gap-2">
                {/* Audio Equalizer */}
                <button
                  onClick={() => setIsEqDrawerOpen(true)}
                  title="Equalizer & Sound FX"
                  className="p-2.5 rounded-full bg-black/50 hover:bg-white/15 backdrop-blur-xl border border-white/10 text-white hover:text-amber-400 transition-all active:scale-90"
                >
                  <Sliders className="w-4 h-4" />
                </button>

                {/* Subtitles */}
                <button
                  onClick={() => setIsSubDrawerOpen(true)}
                  title="Subtitle Settings"
                  className="p-2.5 rounded-full bg-black/50 hover:bg-white/15 backdrop-blur-xl border border-white/10 text-white hover:text-amber-400 transition-all active:scale-90"
                >
                  <Type className="w-4 h-4" />
                </button>

                {/* Screenshot */}
                <button
                  onClick={captureScreenshot}
                  title="Instant Screenshot"
                  className="p-2.5 rounded-full bg-black/50 hover:bg-white/15 backdrop-blur-xl border border-white/10 text-white hover:text-amber-400 transition-all active:scale-90"
                >
                  <Camera className="w-4 h-4" />
                </button>

                {/* PiP */}
                <button
                  onClick={togglePiP}
                  title="Picture-in-Picture"
                  className="p-2.5 rounded-full bg-black/50 hover:bg-white/15 backdrop-blur-xl border border-white/10 text-white hover:text-amber-400 transition-all active:scale-90 hidden sm:flex"
                >
                  <PictureInPicture className="w-4 h-4" />
                </button>

                {/* Lock Screen */}
                <button
                  onClick={() => {
                    setIsLocked(true);
                    setControlsVisible(false);
                  }}
                  title="Lock Controls"
                  className="p-2.5 rounded-full bg-black/50 hover:bg-white/15 backdrop-blur-xl border border-white/10 text-white hover:text-amber-400 transition-all active:scale-90"
                >
                  <Lock className="w-4 h-4" />
                </button>

                {/* Playback Settings */}
                <button
                  onClick={() => setIsSettingsDrawerOpen(true)}
                  title="Playback Settings"
                  className="p-2.5 rounded-full bg-black/50 hover:bg-white/15 backdrop-blur-xl border border-white/10 text-white hover:text-amber-400 transition-all active:scale-90"
                >
                  <Settings className="w-4 h-4" />
                </button>
              </div>
            </div>

            {/* Center Controls (Skip -10s, Play/Pause, Skip +10s) */}
            <div className="flex items-center justify-center gap-6 sm:gap-10">
              <button
                onClick={() => seekRelative(-10)}
                className="p-3.5 rounded-full bg-black/40 hover:bg-white/15 backdrop-blur-xl border border-white/10 text-white hover:text-amber-400 transition-all active:scale-90"
              >
                <RotateCcw className="w-6 h-6" />
              </button>

              <button
                onClick={togglePlay}
                className="p-5 rounded-full bg-amber-500 hover:bg-amber-400 text-black shadow-2xl shadow-amber-500/40 transition-all active:scale-95"
              >
                {isPlaying ? <Pause className="w-8 h-8 fill-black" /> : <Play className="w-8 h-8 fill-black translate-x-0.5" />}
              </button>

              <button
                onClick={() => seekRelative(10)}
                className="p-3.5 rounded-full bg-black/40 hover:bg-white/15 backdrop-blur-xl border border-white/10 text-white hover:text-amber-400 transition-all active:scale-90"
              >
                <RotateCw className="w-6 h-6" />
              </button>
            </div>

            {/* Bottom Controls (Timeline, Timestamps, Precision Frame Step, Fullscreen) */}
            <div className="flex flex-col gap-2 bg-black/60 backdrop-blur-2xl p-4 rounded-3xl border border-white/10 shadow-2xl">
              {/* Timeline Progress Bar */}
              <div className="flex items-center gap-3">
                <span className="text-xs font-mono text-zinc-400 min-w-[45px]">
                  {formatTime(currentTime)}
                </span>

                <div className="relative flex-1 flex items-center group cursor-pointer">
                  {/* A-B Markers on Timeline */}
                  {loopAB.a !== null && duration > 0 && (
                    <div
                      className="absolute top-0 bottom-0 w-1 bg-amber-400 z-10 rounded-full"
                      style={{ left: `${(loopAB.a / duration) * 100}%` }}
                    />
                  )}
                  {loopAB.b !== null && duration > 0 && (
                    <div
                      className="absolute top-0 bottom-0 w-1 bg-amber-400 z-10 rounded-full"
                      style={{ left: `${(loopAB.b / duration) * 100}%` }}
                    />
                  )}

                  <input
                    type="range"
                    min={0}
                    max={duration || 100}
                    step={0.1}
                    value={currentTime}
                    onChange={(e) => {
                      const val = parseFloat(e.target.value);
                      setCurrentTime(val);
                      if (videoRef.current) videoRef.current.currentTime = val;
                    }}
                    className="w-full h-1.5 bg-white/20 rounded-lg appearance-none cursor-pointer accent-amber-500"
                  />
                </div>

                <span className="text-xs font-mono text-zinc-400 min-w-[45px]">
                  {formatTime(duration)}
                </span>
              </div>

              {/* Action Bar Beneath Timeline */}
              <div className="flex items-center justify-between pt-1">
                {/* Frame Step Controls */}
                <div className="flex items-center gap-1.5">
                  <button
                    onClick={() => stepFrame(-1)}
                    title="Previous Frame (1/30s)"
                    className="p-1.5 rounded-lg bg-white/5 hover:bg-white/15 text-zinc-400 hover:text-white transition-all flex items-center gap-1 text-[11px]"
                  >
                    <StepBack className="w-3.5 h-3.5" />
                    <span className="hidden sm:inline">Frame</span>
                  </button>
                  <button
                    onClick={() => stepFrame(1)}
                    title="Next Frame (1/30s)"
                    className="p-1.5 rounded-lg bg-white/5 hover:bg-white/15 text-zinc-400 hover:text-white transition-all flex items-center gap-1 text-[11px]"
                  >
                    <StepForward className="w-3.5 h-3.5" />
                    <span className="hidden sm:inline">Frame</span>
                  </button>
                </div>

                {/* Aspect Ratio Badge & Fullscreen */}
                <div className="flex items-center gap-2">
                  <span className="text-[10px] font-mono uppercase bg-white/10 px-2 py-0.5 rounded-full text-zinc-400">
                    {aspectRatio}
                  </span>

                  <button
                    onClick={() => {
                      if (!document.fullscreenElement) {
                        containerRef.current?.requestFullscreen();
                        setIsFullscreen(true);
                      } else {
                        document.exitFullscreen();
                        setIsFullscreen(false);
                      }
                    }}
                    className="p-2 rounded-xl hover:bg-white/10 text-zinc-400 hover:text-white transition-all"
                  >
                    {isFullscreen ? <Minimize className="w-4 h-4" /> : <Maximize className="w-4 h-4" />}
                  </button>
                </div>
              </div>
            </div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Equalizer Drawer */}
      <AudioEqualizerDrawer
        isOpen={isEqDrawerOpen}
        onClose={() => setIsEqDrawerOpen(false)}
        gains={eqGains}
        onGainChange={(idx, val) => {
          const next = [...eqGains];
          next[idx] = val;
          setEqGains(next);
        }}
        nightMode={nightMode}
        onNightModeToggle={() => {
          const next = !nightMode;
          setNightMode(next);
          audioEngine.setNightMode(next);
        }}
        audioBoost={audioBoostVolume}
        onAudioBoostChange={(val) => {
          setAudioBoostVolume(val);
          audioEngine.setVolumeBoost(val / 100);
        }}
      />

      {/* Subtitle Customizer Drawer */}
      <SubtitleDrawer
        isOpen={isSubDrawerOpen}
        onClose={() => setIsSubDrawerOpen(false)}
        settings={subSettings}
        onSettingsChange={setSubSettings}
        onFileUpload={handleSubtitleUpload}
        activeSubtitleName={activeSubName}
        onClearSubtitle={() => {
          setSubtitleCues([]);
          setActiveSubName(null);
          setCurrentSubtitleText('');
        }}
      />

      {/* Playback Settings Drawer */}
      <PlaybackSettingsDrawer
        isOpen={isSettingsDrawerOpen}
        onClose={() => setIsSettingsDrawerOpen(false)}
        speed={playbackSpeed}
        onSpeedChange={(val) => {
          setPlaybackSpeed(val);
          if (videoRef.current) videoRef.current.playbackRate = val;
        }}
        aspectRatio={aspectRatio}
        onAspectRatioChange={setAspectRatio}
        ambientGlow={ambientGlow}
        onAmbientGlowToggle={() => setAmbientGlow(!ambientGlow)}
        audioOnly={audioOnly}
        onAudioOnlyToggle={() => setAudioOnly(!audioOnly)}
        loopAB={loopAB}
        onSetPointA={() => setLoopAB((prev) => ({ ...prev, a: currentTime, active: prev.b !== null }))}
        onSetPointB={() => setLoopAB((prev) => ({ ...prev, b: currentTime, active: prev.a !== null }))}
        onClearLoopAB={() => setLoopAB({ a: null, b: null, active: false })}
        currentTime={currentTime}
      />
    </div>
  );
};
