import { useState, useEffect } from 'react';
import type { VideoItem } from './types';
import { DEMO_VIDEOS } from './constants';
import { MediaLibraryScreen } from './components/MediaLibraryScreen';
import { VideoPlayerScreen } from './components/VideoPlayerScreen';

const STORAGE_KEY = 'nextplayer_saved_library_v1';

export function App() {
  const [videos, setVideos] = useState<VideoItem[]>(() => {
    try {
      const saved = localStorage.getItem(STORAGE_KEY);
      if (saved) {
        const parsed = JSON.parse(saved);
        if (Array.isArray(parsed) && parsed.length > 0) {
          return parsed;
        }
      }
    } catch {
      // ignore
    }
    return DEMO_VIDEOS;
  });

  const [activeVideo, setActiveVideo] = useState<VideoItem | null>(null);

  // Sync videos to localStorage
  useEffect(() => {
    try {
      // Don't serialize File object blobs to localStorage
      const serializable = videos.map(({ file, ...rest }) => rest);
      localStorage.setItem(STORAGE_KEY, JSON.stringify(serializable));
    } catch {
      // ignore
    }
  }, [videos]);

  // Handle local video files import
  const handleImportFiles = (fileList: FileList) => {
    const newItems: VideoItem[] = [];
    for (let i = 0; i < fileList.length; i++) {
      const file = fileList[i];
      if (!file.type.startsWith('video/')) continue;

      const blobUrl = URL.createObjectURL(file);
      const folderName = (file as unknown as { webkitRelativePath?: string }).webkitRelativePath
        ? (file as unknown as { webkitRelativePath: string }).webkitRelativePath.split('/')[0]
        : 'Local Device';

      newItems.push({
        id: `local-${Date.now()}-${i}`,
        title: file.name.replace(/\.[^/.]+$/, ''),
        url: blobUrl,
        folder: folderName,
        file,
        dateAdded: Date.now(),
        lastPlayed: 0,
      });
    }

    if (newItems.length > 0) {
      setVideos((prev) => [...newItems, ...prev]);
    }
  };

  // Update playback history
  const handleUpdateWatchProgress = (id: string, time: number, completed: boolean) => {
    setVideos((prev) =>
      prev.map((v) => (v.id === id ? { ...v, lastPlayed: time, completed } : v))
    );
  };

  // Clear Watch History
  const handleClearHistory = () => {
    setVideos((prev) =>
      prev.map((v) => ({ ...v, lastPlayed: 0, completed: false }))
    );
  };

  return (
    <div className="w-full min-h-screen bg-[#050505] text-white">
      {activeVideo ? (
        <VideoPlayerScreen
          video={activeVideo}
          onBack={() => setActiveVideo(null)}
          onUpdateWatchProgress={handleUpdateWatchProgress}
        />
      ) : (
        <MediaLibraryScreen
          videos={videos}
          onSelectVideo={(video) => setActiveVideo(video)}
          onImportFiles={handleImportFiles}
          onClearHistory={handleClearHistory}
        />
      )}
    </div>
  );
}

export default App;
