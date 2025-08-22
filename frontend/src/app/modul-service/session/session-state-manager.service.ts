import {Session} from './session-domain';
import {AudioService} from './audio.service';

export class SessionStateManager {
  audioService = new AudioService()
  total : number = 0
  lernzeiten : number[] = []
  pausen : number[] = []
  session : any

  currentLernzeitTimer : any = 0
  currentPauseTimer : any = 0
  currentLernzeitIndex : number = 0
  currentPauseIndex : number = 0
  currentBlockId : string


  constructor(session : Session | null) {
    console.log("session: ", session)
    this.lernzeiten = session!.blocks.map(block => block.lernzeitSeconds);
    this.pausen = session!.blocks.map(block => block.pausezeitSeconds);
    this.currentBlockId = session!.blocks[0].fachId || "";
    this.session = session
  }

  start(): void {
    this.startBlock(this.currentLernzeitIndex);
  }

  pause() : void {
    console.log("pausing timers")
    clearInterval(this.currentLernzeitTimer)
    clearInterval(this.currentPauseTimer)
  }

  startBlock(i: number) {
    if (i >= this.lernzeiten.length) {
      console.log("Alle Blöcke abgeschlossen!");
      return;
    }

    this.currentLernzeitTimer = setInterval(() => {
      this.total += 1;
      this.lernzeiten[i]--;

      console.log(`Block ${i} Lernzeit: ${this.lernzeiten[i]}`);

      if (this.lernzeiten[i] <= 0) {
        this.audioService.playAudio("block-clear-1.mp3");
        clearInterval(this.currentLernzeitTimer);
        this.currentLernzeitIndex++;
        this.currentPauseTimer = setInterval(() => {
          this.pausen[i]--;
          console.log(`Block ${i} Pause: ${this.pausen[i]}`);

          if (this.pausen[i] <= 0) {
            clearInterval(this.currentPauseTimer);
            this.currentPauseIndex++;
            console.log(`Block ${i} abgeschlossen.`);
            this.startBlock(i + 1);
          }
        }, 1000);
      }
    }, 1000);
    this.currentBlockId = this.session!.blocks[this.currentLernzeitIndex].fachId || "";
  }

  clearState() : void {
    this.total = 0;
    this.lernzeiten = [];
    this.pausen = [];
    clearInterval(this.currentLernzeitTimer);
    clearInterval(this.currentPauseTimer);
    this.currentLernzeitTimer = 0;
    this.currentPauseTimer = 0;
  }
  getCurrentLernzeit() : number {
    return this.lernzeiten[this.currentLernzeitIndex];
  }

  getCurrentPause() : number {
    return this.pausen[this.currentPauseIndex];
  }

  getCurrentBlockId() : string {
    return this.currentBlockId;
  }
}
