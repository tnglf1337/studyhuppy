import {Component, OnInit} from '@angular/core';
import {NgIf} from '@angular/common';
import {SessionStateManager} from '../session-state-manager.service';
import {Block, Session} from '../session-domain';

@Component({
  selector: 'app-session-start',
  imports: [NgIf],
  templateUrl: './start.component.html',
  styleUrl: './start.component.scss'
})
export class SessionStartComponent implements OnInit{
  session: any // TODO: need to get all configured sessions from backend
  sessionStateManager!: SessionStateManager;
  sessionStarted = true;
  sessionPaused = false;

  ngOnInit(): void {
    // dummy data!
    let blocks = []
    for(let i = 0; i < 3; i++) {
      const block = new Block(10, 10, i.toString());
      blocks.push(block)
    }
    this.session = new Session("dummy titel", "eine session", blocks);
    // end dummy data

    this.sessionStateManager = new SessionStateManager(this.session)
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
