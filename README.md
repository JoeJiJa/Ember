<div align="center">

# 🔥 Char
### Modernized AMOLED Video Player for Android & Web

[![License](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE)
[![Build APK](https://github.com/actions/workflows/build-apk.yml/badge.svg)](#)
[![Theme](https://img.shields.io/badge/Theme-AMOLED%20Pitch%20Black-F59E0B.svg)](#)

<p align="center">
  <b>A next-generation, ultra-modern video player.</b><br/>
  Featuring real-time ambient backglow, 200% Web Audio gain boost, 5-band graphic equalizer, gesture-driven scrubbing, and dynamic swipe speed escalation (2.0x → 5.0x).
</p>

[✨ Features](#-features) • [📱 Download APK](#-download-apk) • [💻 Web Player](#-web-player) • [🛠️ Tech Stack](#️-tech-stack)

---

</div>

## 📱 Download APK
Head over to the **[Releases](../../releases)** tab on GitHub or check the **Actions Artifacts** to download `Char-Player-v1.0.0-debug.apk` directly onto your Android device!

---

## ✨ Features

### 🎨 Visual Aesthetic & Design System
- **AMOLED Pitch-Black Canvas (`#050505`)**: True black optimization for OLED/AMOLED smartphone displays.
- **Brushed Metallic Panels (`#121212`)**: Dark tactile surfaces with subtle glowing borders.
- **Warm Amber / Gold Accents (`#F59E0B`)**: High contrast indicators and sliders.
- **Glassmorphism**: Translucent floating playback controllers (`backdrop-blur-xl bg-black/60 border border-white/10`).

### ⚡ Full Gesture Engine
- **Vertical Left-Swipe**: Screen Brightness control with smooth visual slider pill.
- **Vertical Right-Swipe**: Volume control and **Audio Gain Boost (up to 200%)** via Web Audio API `GainNode`.
- **Horizontal Swipe**: Smooth video scrubbing with real-time timestamp preview.
- **Double-Tap (Left / Right)**: Instant ±10s seek accompanied by animated tactile ripple waves.
- **Long-Press & Swipe-to-Right Speed Escalation**:
  - Hold anywhere on the screen for instant **2.0X speed**.
  - While holding, drag/swipe to the right to dynamically transition through speeds:
    $$\mathbf{2.0x \rightarrow 2.5x \rightarrow 2.75x \rightarrow 3.0x \rightarrow 3.25x \rightarrow 3.5x \rightarrow 3.75x \rightarrow 4.0x \rightarrow 4.25x \rightarrow 4.5x \rightarrow 4.75x \rightarrow 5.0x}$$
  - Release anytime to seamlessly resume normal speed.

### 🎛️ Audio Studio & Equalizer
- **5-Band Graphic Equalizer**: Precise dB tuning across 60 Hz, 230 Hz, 910 Hz, 3.6 kHz, and 14 kHz.
- **Audio Presets**: One-tap profiles for *Flat*, *Bass Boost (+8dB)*, *Vocal Clarity*, *Cinema / Movie*, and *Night Dialogue*.
- **Night Mode Compressor**: Dynamic compression that softens sudden explosions and elevates quiet whispers for night viewing.

### 🌟 Premium Playback Upgrades
- **Dynamic Ambient Glow**: Real-time 2D Canvas edge-color projection that projects colors onto the dark background.
- **A-B Segment Looping**: Set Point A and Point B on the timeline to endlessly repeat loops.
- **Aspect Ratio Switcher**: One-tap toggles between *Fit*, *Fill / Crop*, *Zoom 125%*, *16:9*, *4:3*, and *Stretch*.
- **Custom Subtitle Loader**: Upload `.srt` and `.vtt` with full font size, color, and vertical offset positioning.
- **Frame-by-Frame Step & Screenshot**: 1/30s precision frame nudging and one-tap PNG snapshots.

---

## 💻 Web Player Quickstart

Run Char locally in seconds:

```bash
cd web-player
bun install
bun dev
```

Open [http://localhost:5173/](http://localhost:5173/) in any browser.

---

## 🛠️ Tech Stack
- **Frontend**: React 19, TypeScript, Vite
- **Styling**: Tailwind CSS v4, Lucide React Icons
- **Animation**: Framer Motion spring physics
- **Audio & Video Processing**: Web Audio API (`GainNode`, `DynamicsCompressorNode`, `BiquadFilterNode`), Canvas 2D
- **Packaging**: Capacitor ready for Android APK compilation
