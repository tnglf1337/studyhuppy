import {Component, inject, OnInit} from '@angular/core';
import {FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {NgForOf} from '@angular/common';
import {BlockComponent} from './block/block.component';
import {Block, Session} from './session-domain';
import {SessionApiService} from './session-api.service';


@Component({
  selector: 'app-session',
  imports: [BlockComponent, FormsModule, NgForOf, ReactiveFormsModule],
  templateUrl: './session.component.html',
  standalone: true,
  styleUrl: './session.component.scss'
})
export class SessionComponent implements OnInit{
  sessionApiService = inject(SessionApiService)
  anzahlBloecke : number = 2;
  session: any



  ngOnInit(): void {
    this.setSessionConfigData()
  }

  getBlocks(): number[] {
    return Array.from({ length: this.anzahlBloecke }, (_, i) => i);
  }

  setSessionConfigData() : void {
    let blocks = []

    for(let i = 0; i < this.anzahlBloecke; i++) {
      const block = new Block(300, 300);
      blocks.push(block)
    }
    this.session = new Session("dummy titel", "eine session", blocks);
    console.log(this.session)
  }

  getBlock(index : number) : Block {
    return this.session.blocks[index];
  }

  saveSession(): void {
    this.sessionApiService.saveSession(this.session).subscribe()
  }
}
