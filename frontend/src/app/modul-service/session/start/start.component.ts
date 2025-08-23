import {Component, inject, OnInit} from '@angular/core';
import {NgClass, NgForOf, NgIf} from '@angular/common';
import {SessionStateManager} from '../session-state-manager.service';
import {Session} from '../session-domain';
import {RouterLink} from '@angular/router';
import {SessionApiService} from '../session-api.service';
import {TimeFormatPipe} from '../../module/time-format.pipe';
import {ModuleApiService} from '../../module/module-api.service';

@Component({
  selector: 'app-session-start',
  imports: [NgIf, RouterLink, NgForOf, TimeFormatPipe, NgClass],
  templateUrl: './start.component.html',
  standalone: true,
  styleUrls: ['./start.component.scss', '../../../general.scss', '../../../button.scss']
})
export class SessionStartComponent implements OnInit{
  sessions: any
  selectedSession!: Session | null;
  sessionApiService = inject(SessionApiService)
  modulService = inject(ModuleApiService)
  sessionStateManager!: SessionStateManager;
  sessionStarted = true;
  sessionPaused = false;

  ngOnInit(): void {
    this.sessionApiService.getSessions().subscribe({
      next: (data) => {
        if (!data) {
          console.log("Keine Sessions vorhanden (204 No Content)");
          this.sessions = null;
          this.selectedSession = null;
          return;
        }

        console.log("received sessions from backend", data);
        this.sessions = data
        this.selectedSession = this.sessions[0];
        this.sessionStateManager = new SessionStateManager(this.modulService, this.selectedSession)
      }
    })
  }

  startSession(): void {
    this.sessionStateManager.start()
    this.sessionStarted = false;
  }

  pauseSession(): void {
    this.sessionStateManager.pause()
    this.sessionPaused = true;
    this.sessionStateManager.printTotaleLernzeit()
  }

  resumeSession(): void {
    this.startSession()
    this.sessionPaused = false;
    this.sessionStateManager.printTotaleLernzeit()
  }

  abortThisSession() : void {
    // Die Session wird abgebrochen, aber die bisher gelernte Zeit wird dem Modul gutgeschrieben
    const modulId = this.sessionStateManager.getCurrentBlockModulId()
    const secondsLearned = this.sessionStateManager.getCurrentTotal()
    this.modulService.postRawSeconds(modulId, secondsLearned).subscribe()

    this.sessionStateManager.printTotaleLernzeit()
    this.sessionStateManager.pause()
    this.sessionStarted = true;
    this.sessionStateManager.clearState();
    this.ngOnInit()
  }

  getCurrentLernzeit(): number {
    return this.sessionStateManager.getCurrentLernzeit()
  }

  getCurrentPause(): number {
    return this.sessionStateManager.getCurrentPause()
  }

  getCurrentBlockId(): string {
    return this.sessionStateManager.getCurrentBlockId()
  }
}
