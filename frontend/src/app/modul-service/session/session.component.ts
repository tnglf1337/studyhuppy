import {Component, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {NgForOf} from '@angular/common';
import {BlockComponent} from './block/block.component';


@Component({
  selector: 'app-session',
  imports: [BlockComponent, FormsModule, NgForOf],
  templateUrl: './session.component.html',
  standalone: true,
  styleUrl: './session.component.scss'
})
export class SessionComponent implements OnInit{
  anzahlBloecke : number = 2;

  ngOnInit(): void {
    console.log(this.anzahlBloecke)
  }

  getBlocks(): number[] {
    return Array.from({ length: this.anzahlBloecke }, (_, i) => i);
  }

  print() : void {
    console.log(this.anzahlBloecke)
  }
}
