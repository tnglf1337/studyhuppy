export class AudioService {
  private audio: HTMLAudioElement;

  constructor() {
    this.audio = new Audio();
  }

  playAudio(fileName: string): void {
    const filePath = `assets/sfx/${fileName}`;
    this.audio.src = filePath;
    this.audio.load();
    this.audio.play().catch(error => {
      console.error("Error playing audio:", error);
    });
  }
}
