// Web Audio API Engine supporting up to 200% Gain Boost, 5-Band Equalizer, and Dynamics Compressor
export class AudioProcessingEngine {
  private ctx: AudioContext | null = null;
  private source: MediaElementAudioSourceNode | null = null;
  private gainNode: GainNode | null = null;
  private compressorNode: DynamicsCompressorNode | null = null;
  private eqFilters: BiquadFilterNode[] = [];
  private attachedElement: HTMLMediaElement | null = null;
  public isNightMode: boolean = false;

  private frequencies = [60, 230, 910, 3600, 14000];

  public init(mediaElement: HTMLMediaElement) {
    if (this.attachedElement === mediaElement && this.ctx) {
      if (this.ctx.state === 'suspended') {
        this.ctx.resume().catch(() => {});
      }
      return;
    }

    try {
      const AudioCtxClass = window.AudioContext || (window as unknown as { webkitAudioContext: typeof AudioContext }).webkitAudioContext;
      this.ctx = new AudioCtxClass();
      this.attachedElement = mediaElement;

      // Allow cross origin media to be routed through Web Audio API
      if (!mediaElement.crossOrigin) {
        mediaElement.crossOrigin = 'anonymous';
      }

      this.source = this.ctx.createMediaElementSource(mediaElement);

      // 1. Gain Node (Supports up to 2.0 = 200% audio boost)
      this.gainNode = this.ctx.createGain();
      this.gainNode.gain.value = 1.0;

      // 2. Dynamics Compressor for Night Mode
      this.compressorNode = this.ctx.createDynamicsCompressor();
      this.compressorNode.threshold.value = -30;
      this.compressorNode.knee.value = 20;
      this.compressorNode.ratio.value = 12;
      this.compressorNode.attack.value = 0.003;
      this.compressorNode.release.value = 0.25;

      // 3. 5-Band Equalizer filters
      this.eqFilters = this.frequencies.map((freq, index) => {
        const filter = this.ctx!.createBiquadFilter();
        if (index === 0) {
          filter.type = 'lowshelf';
        } else if (index === this.frequencies.length - 1) {
          filter.type = 'highshelf';
        } else {
          filter.type = 'peaking';
          filter.Q.value = 1.4;
        }
        filter.frequency.value = freq;
        filter.gain.value = 0;
        return filter;
      });

      // Chain: Source -> EQ[0] -> EQ[1] -> EQ[2] -> EQ[3] -> EQ[4] -> Gain -> Destination
      let lastNode: AudioNode = this.source;
      this.eqFilters.forEach((filter) => {
        lastNode.connect(filter);
        lastNode = filter;
      });

      lastNode.connect(this.gainNode);
      this.gainNode.connect(this.ctx.destination);
    } catch (e) {
      console.warn('AudioContext routing error (falling back to standard audio):', e);
    }
  }

  public setVolumeBoost(multiplier: number) {
    if (!this.gainNode || !this.ctx) return;
    // Multiplier from 0.0 to 2.0
    this.gainNode.gain.setTargetAtTime(multiplier, this.ctx.currentTime, 0.02);
  }

  public setEqGain(bandIndex: number, gainDb: number) {
    if (!this.eqFilters[bandIndex] || !this.ctx) return;
    this.eqFilters[bandIndex].gain.setTargetAtTime(gainDb, this.ctx.currentTime, 0.02);
  }

  public setNightMode(enabled: boolean) {
    if (!this.ctx || !this.gainNode || !this.compressorNode) return;
    this.isNightMode = enabled;

    try {
      if (enabled) {
        this.gainNode.disconnect();
        this.gainNode.connect(this.compressorNode);
        this.compressorNode.connect(this.ctx.destination);
      } else {
        try {
          this.compressorNode.disconnect();
        } catch {
          // ignore
        }
        this.gainNode.disconnect();
        this.gainNode.connect(this.ctx.destination);
      }
    } catch {
      // ignore
    }
  }

  public resume() {
    if (this.ctx && this.ctx.state === 'suspended') {
      this.ctx.resume();
    }
  }
}

export const audioEngine = new AudioProcessingEngine();
