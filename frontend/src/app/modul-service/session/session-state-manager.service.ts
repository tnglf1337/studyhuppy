import {Session} from './session-domain';
import {Injectable} from '@angular/core';

export class SessionStateManager {
  total : number = 0
  lernzeiten : number[] = []
  pausen : number[] = []

  currentLernzeitTimer : any = 0
  currentPauseTimer : any = 0


  constructor(session : Session) {
    this.lernzeiten = session.blocks.map(block => block.lernzeitSeconds);
    this.pausen = session.blocks.map(block => block.pausezeitSeconds);
  }

  start(): void {
    this.startBlock(0); // Start mit dem ersten Block
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
        clearInterval(this.currentLernzeitTimer);

        this.currentPauseTimer = setInterval(() => {
          this.pausen[i]--;
          console.log(`Block ${i} Pause: ${this.pausen[i]}`);

          if (this.pausen[i] <= 0) {
            clearInterval(this.currentPauseTimer);
            console.log(`Block ${i} abgeschlossen.`);
            this.startBlock(i + 1); // Nächsten Block starten
          }
        }, 1000);
      }
    }, 1000);
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
}
