import type { VideoItem } from './types';

export const DEMO_VIDEOS: VideoItem[] = [
  {
    id: 'demo-big-buck-bunny',
    title: 'Big Buck Bunny (4K Open Movie)',
    url: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4',
    duration: 596,
    folder: 'Blender Open Movies',
    thumbnail: 'https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600&auto=format&fit=crop&q=80',
    dateAdded: Date.now() - 1000 * 60 * 60 * 24 * 2,
    lastPlayed: 45,
  },
  {
    id: 'demo-sintel',
    title: 'Sintel - Animated Fantasy Epic',
    url: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4',
    duration: 888,
    folder: 'Blender Open Movies',
    thumbnail: 'https://images.unsplash.com/photo-1534447677768-be436bb09401?w=600&auto=format&fit=crop&q=80',
    dateAdded: Date.now() - 1000 * 60 * 60 * 24 * 3,
    lastPlayed: 120,
  },
  {
    id: 'demo-tears-of-steel',
    title: 'Tears of Steel (Sci-Fi VFX)',
    url: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4',
    duration: 734,
    folder: 'Sci-Fi Short Films',
    thumbnail: 'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600&auto=format&fit=crop&q=80',
    dateAdded: Date.now() - 1000 * 60 * 60 * 12,
    lastPlayed: 0,
  },
  {
    id: 'demo-we-are-going-on-bullrun',
    title: 'Bullrun Rally Stage - 1080p 60fps',
    url: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4',
    duration: 47,
    folder: 'Motorsport & Action',
    thumbnail: 'https://images.unsplash.com/photo-1568605117036-5fe5e7bab0b7?w=600&auto=format&fit=crop&q=80',
    dateAdded: Date.now() - 1000 * 60 * 60 * 5,
    lastPlayed: 0,
  },
  {
    id: 'demo-elephants-dream',
    title: 'Elephants Dream - Surreal Journey',
    url: 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4',
    duration: 653,
    folder: 'Blender Open Movies',
    thumbnail: 'https://images.unsplash.com/photo-1516339901601-2e1b62dc0c45?w=600&auto=format&fit=crop&q=80',
    dateAdded: Date.now() - 1000 * 60 * 60 * 48,
    lastPlayed: 320,
  }
];

export const EQ_PRESETS: { name: string; gains: number[] }[] = [
  { name: 'Flat', gains: [0, 0, 0, 0, 0] },
  { name: 'Bass Boost (+8dB)', gains: [8, 6, 2, 0, 0] },
  { name: 'Vocal Clarity', gains: [-2, 1, 6, 4, 1] },
  { name: 'Cinema / Movie', gains: [5, 2, 1, 4, 6] },
  { name: 'Night Dialogue', gains: [-4, 0, 8, 3, -2] },
];
