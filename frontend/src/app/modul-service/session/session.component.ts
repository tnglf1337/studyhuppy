import {Component, inject, OnInit} from '@angular/core';
import {FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';
import {BlockComponent} from './block/block.component';
import {Block, Session} from './session-domain';
import {SessionApiService} from './session-api.service';
import {Modul} from '../module/domain';
import {ModuleApiService} from '../module/module-api.service';
import {SessionStateManager} from './session-state-manager.service';


@Component({
  selector: 'app-session',
  imports: [BlockComponent, FormsModule, NgForOf, ReactiveFormsModule, NgIf],
  templateUrl: './session.component.html',
  standalone: true,
  styleUrl: './session.component.scss'
})
export class SessionComponent implements OnInit{
  sessionApiService = inject(SessionApiService)
  anzahlBloecke : number = 2;
  session: any
  module : Modul[] = []
  modulService = inject(ModuleApiService)
  sessionStateManager!: SessionStateManager;
  sessionStarted = true;
  sessionPaused = false;

  ngOnInit(): void {
    this.modulService.getAllModulesByUsername().subscribe(
      {
        next: data => {
          this.module = data
          console.log(this.module)
        }
      }
    )

    this.setSessionConfigData()

    this.sessionStateManager = new SessionStateManager(this.session)
  }

  getBlocks(): number[] {
    return Array.from({ length: this.anzahlBloecke }, (_, i) => i);
  }

  setSessionConfigData() : void {
    let blocks = []

    for(let i = 0; i < this.anzahlBloecke; i++) {
      const block = new Block(10, 10, this.module?.[0]?.fachId);
      blocks.push(block)
    }
    this.session = new Session("dummy titel", "eine session", blocks);
    console.log(this.session)
  }

  getBlock(index : number) : Block {
    return this.session.blocks[index];
  }

  saveSession(): void {
    if(this.session.validSession()) {
      this.sessionApiService.saveSession(this.session).subscribe()
    } else {
      console.error("Session ist ungültig. Bitte überprüfen Sie die Eingaben.");
    }
  }

  startSession(): void {
    this.sessionStateManager.start()
    this.sessionStarted = false;
  }

  pauseSession(): void {
    this.sessionStateManager.pause()
    this.sessionPaused = true;
  }

  resumeSession(): void {
    this.startSession()
    this.sessionPaused = false;
  }

  abortThisSession() : void {
    this.sessionStateManager.pause()
    this.sessionStarted = true;
    this.sessionStateManager.clearState();
    this.ngOnInit()
  }
}
