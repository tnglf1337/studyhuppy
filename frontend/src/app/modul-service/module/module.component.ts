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
  allModule : { [key: number]: Modul[] } = {}
  displayedModule :  { [key: number]: Modul[] } = {}
  deactivatedAreVisible!: boolean

  delta = 0 // Differenz zwischen startDate und die aktuelle Uhrzeit
  currentModulSecondsLearned = 0
  timer: number = 0
  running: boolean = true
  disabledBtn : boolean[][] = []
  isLoading: boolean = true
  openPanels: boolean[] = []

  ngOnInit(): void {
    this.service.getAllModuleByFachsemester().subscribe({
      next: (data) => {
        this.allModule = data
        this.isLoading = false
        this.initDisabledBtn()
        this.initOpenPanels()
        this.setDisplayedModule()
      },
      error: (err) => {
        this.log.error(err)
      }
    });
    if (!localStorage.getItem("deactivatedAreVisible")) {
      localStorage.setItem("deactivatedAreVisible", "false")
    } else {
      this.deactivatedAreVisible = localStorage.getItem("deactivatedAreVisible") === "true"
    }
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
    return this.displayedModule[semester] || [];
  }

  showAccordionElement(i : number) {
    this.openPanels[i] = !this.openPanels[i];
  }

  initOpenPanels() {
    this.openPanels.push(true)
    for (let i = 1; i < Object.keys(this.allModule).length; i++) {
      this.openPanels.push(false)
    }
    //this.log.debug("Initialized openPanels: " + this.openPanels)
  }

  initDisabledBtn() {
    //Sortieren, damit hohe Fachsemester oben angezeigt werden
    const sortedKeys = Object.keys(this.allModule)
      .map(key => parseInt(key, 10))
      .sort((a, b) => b - a);

    for (const key of sortedKeys) {
      if (this.allModule.hasOwnProperty(key)) {
        const moduleListe = this.allModule[key];
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

  changeVisibility() {
    let currentVisibility = localStorage.getItem("deactivatedAreVisible") === "true";
    currentVisibility = !currentVisibility;
    localStorage.setItem("deactivatedAreVisible", String(currentVisibility));
    this.ngOnInit()
  }

  setDisplayedModule() {
    let res :  { [key: number]: Modul[] } = {}

    if (this.deactivatedAreVisible) {
      for (const [semester, moduleArray] of Object.entries(this.allModule)) {
        const sem = Number(semester);
        res[sem] = []
        for (const modul of moduleArray) {
          res[sem].push(modul)
        }
      }
    } else {
      for (const [semester, moduleArray] of Object.entries(this.allModule)) {
        const sem = Number(semester);
        res[sem] = []
        for (const modul of moduleArray) {
          if(modul.active) {
            res[sem].push(modul)
          }
        }
      }
    }

    this.displayedModule = res
  }
}
