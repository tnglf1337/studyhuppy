import {Component, inject, OnInit} from '@angular/core';
import {Modul} from './domain';
import {CommonModule} from '@angular/common';
import {TimeFormatPipe} from './time-format.pipe';
import {RouterLink} from '@angular/router';
import {LoggingService} from '../../logging.service';
import {ModuleApiService} from './module-api.service';
import {TimerService} from '../timer.service';
import {LoadingDataComponent} from '../../app-layout/loading-data/loading-data.component';

@Component({
  selector: 'app-module',
  templateUrl: './module.component.html',
  styleUrls: ['./module.component.scss', '../../loading.scss', '../../accordion.scss', '../../debug.scss'],
  standalone: true,
  imports: [CommonModule, TimeFormatPipe, RouterLink, LoadingDataComponent]
})
export class ModuleComponent implements OnInit{
  service = inject(ModuleApiService)
  log : LoggingService = new LoggingService("ModuleComponent", "modul-service")
  pipe : TimeFormatPipe = new TimeFormatPipe()
  module: { [key: number]: Modul[] } = {}

  delta = 0 // Differenz zwischen startDate und die aktuelle Uhrzeit
  currentModulSecondsLearned = 0
  timer: number = 0
  running: boolean = true
  disabledBtn : boolean[][] = []
  isLoading: boolean = true
  openPanels: boolean[] = []

  ngOnInit(): void {
    this.service.getModuleByFachsemester().subscribe({
      next: (data) => {
        this.module = data
        this.isLoading = false
        this.initDisabledBtn()
        this.initOpenPanels()
      },
      error: (err) => {
        this.log.error(err)
      }
    });
  }

  updateSecondsOnModulUI(fachId: string, seconds: number): void {
    const element = document.getElementById(fachId);
    console.log("element before update: ", element)
    if (element) {
      element.innerText = this.pipe.transform(seconds)
      element.dataset['value'] = seconds.toString();
    }
    console.log("element after update: ", element)
  }

  /**
   * Startet oder beendet den Timer für das Modul, das per Button-Klick ausgewählt wurde.
   *
   * @param modulId - Die ID des Moduls, für das der Timer gestartet wird.
   * @param i - Reihen-Index für die Button-Logik.
   * @param j - Spalten-Index für die Button-Logik.
   */
  toggleTimer(modulId: string, i : number, j: number) : void {
    const timerService = new TimerService(modulId)

    // Wurde noch kein Button betätigt, dann kann der Modul-Timer starten,
    // sonst wäre disabledBtn[index]=true und es kann
    // kein weiterer Button betätigt werden
    if(!this.disabledBtn[i][j]) {
      if (this.running) {
        this.service.getSeconds(modulId).subscribe({
          next: data =>  {
            this.currentModulSecondsLearned = data
          }
        })
         // Setze den Timer im local storage
        timerService.setLogicTimer()

        // Starte den eigentlichen Timer
        this.timer = window.setInterval(() => {
          this.delta = timerService.computeCurrentTimerDelta() // every 1000ms -> += 1
          this.updateSecondsOnModulUI(modulId, this.currentModulSecondsLearned + this.delta);
        }, 1000);

        this.switchButtonStyle(modulId, 0, i);
        this.running = false;
        this.setRunningBtn(i, j)
      } else {
        clearInterval(this.timer)
        this.clearRunningBtn()
        this.service.postTimerRequest(timerService.getTimerRequest()).subscribe()
        this.running = true;
        this.switchButtonStyle(modulId, 1, i);
      }
    }
  }

  switchButtonStyle(fachId: string, flag: 0 | 1, index : number): void {
    const button = document.getElementById("btn-" + fachId)
    if (!button) return;

    const icon = button.querySelector<HTMLElement>(".button-icon-" + index);
    if (!icon) return;

    if (flag === 1) {
      button.classList.remove("stop");
      button.classList.remove("stop-button");
      button.classList.add("play");
      button.classList.add("play-button");
    } else if (flag === 0) {
      button.classList.add("stop");
      button.classList.add("stop-button");
      button.classList.remove("play");
      button.classList.remove("play-button");
    }
  }

  getModuleForSemester(semester: number): Modul[] {
    return this.module[semester] || [];
  }

  showAccordionElement(i : number) {
    this.openPanels[i] = !this.openPanels[i];
  }

  initOpenPanels() {
    this.openPanels.push(true)
    for (let i = 1; i < Object.keys(this.module).length; i++) {
      this.openPanels.push(false)
    }
    //this.log.debug("Initialized openPanels: " + this.openPanels)
  }

  initDisabledBtn() {
    //Sortieren, damit hohe Fachsemester oben angezeigt werden
    const sortedKeys = Object.keys(this.module)
      .map(key => parseInt(key, 10))
      .sort((a, b) => b - a);

    for (const key of sortedKeys) {
      if (this.module.hasOwnProperty(key)) {
        const moduleListe = this.module[key];
        let fachSemesterBtn = Array(moduleListe.length).fill(false)
        this.disabledBtn.push(fachSemesterBtn)
      }
    }

    this.log.debug("Initialized disabledBtn: " + this.disabledBtn)
  }

  setRunningBtn(i : number, j: number){
    for (let k = 0; k < this.disabledBtn.length; k++) {
      for (let l = 0; l < this.disabledBtn[k].length; l++) {
        this.disabledBtn[k][l] = !(k === i && l === j);
      }
    }
    //this.log.debug(`Set disabledBtn to 'true', except at index [${i}][${j}]`)
  }

  clearRunningBtn() {
    for (let i = 0; i < this.disabledBtn.length; i++) {
      for (let j = 0; j < this.disabledBtn[i].length; j++) {
        this.disabledBtn[i][j] = false
      }
    }
    //this.log.debug("Cleared disabledBtn array")
  }

  protected readonly Object = Object;
}
