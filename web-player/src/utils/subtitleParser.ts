export interface SubtitleCue {
  start: number;
  end: number;
  text: string;
}

export function parseSrt(srtContent: string): SubtitleCue[] {
  const cues: SubtitleCue[] = [];
  const normalized = srtContent.replace(/\r\n/g, '\n').replace(/\r/g, '\n');
  const blocks = normalized.split(/\n\n+/);

  for (const block of blocks) {
    const lines = block.trim().split('\n');
    if (lines.length < 2) continue;

    // Line 0 is number, or Line 0 is timestamp
    let timeLine = lines[0];
    let textLines = lines.slice(1);

    if (/^\d+$/.test(lines[0]) && lines[1] && lines[1].includes('-->')) {
      timeLine = lines[1];
      textLines = lines.slice(2);
    }

    if (!timeLine.includes('-->')) continue;

    const [startStr, endStr] = timeLine.split('-->').map(s => s.trim());
    const start = parseTimestamp(startStr);
    const end = parseTimestamp(endStr);

    if (start !== null && end !== null) {
      cues.push({
        start,
        end,
        text: textLines.join('\n').replace(/<[^>]*>/g, '') // strip simple html tags
      });
    }
  }

  return cues;
}

function parseTimestamp(timeStr: string): number | null {
  // Format: 00:01:20,000 or 00:01:20.000
  const match = timeStr.match(/(?:(\d{1,2}):)?(\d{2}):(\d{2})[,.](\d{3})/);
  if (!match) return null;

  const hours = match[1] ? parseInt(match[1], 10) : 0;
  const minutes = parseInt(match[2], 10);
  const seconds = parseInt(match[3], 10);
  const milliseconds = parseInt(match[4], 10);

  return hours * 3600 + minutes * 60 + seconds + milliseconds / 1000;
}

export function formatTime(seconds: number): string {
  if (isNaN(seconds) || seconds < 0) return '00:00';
  const hrs = Math.floor(seconds / 3600);
  const mins = Math.floor((seconds % 3600) / 60);
  const secs = Math.floor(seconds % 60);

  if (hrs > 0) {
    return `${hrs}:${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  }
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
}
