export interface VideoItem {
  id: string;
  title: string;
  url: string;
  duration?: number;
  thumbnail?: string;
  folder?: string;
  file?: File;
  lastPlayed?: number; // timestamp in seconds
  completed?: boolean;
  dateAdded: number;
}

export interface SubtitleTrack {
  id: string;
  label: string;
  src: string;
  language: string;
  file?: File;
}

export type AspectRatioMode = 'fit' | 'fill' | 'zoom' | '16-9' | '4-3' | 'stretch';

export interface EqualizerBand {
  frequency: number;
  gain: number;
  type: BiquadFilterType;
}

export interface SubtitleSettings {
  fontSize: number; // in rem or px
  color: string;
  bgColor: string; // rgba
  offsetY: number; // in % from bottom
}
