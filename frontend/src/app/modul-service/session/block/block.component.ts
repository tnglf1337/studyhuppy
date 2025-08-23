import {Component, inject, Input, OnInit} from '@angular/core';
import {ModuleApiService} from '../../module/module-api.service';
import {Modul} from '../../module/domain';
import {NgForOf} from '@angular/common';
import {Block} from '../session-domain';

@Component({
  selector: 'Lernblock',
  imports: [
    NgForOf
  ],
  templateUrl: './block.component.html',
  standalone: true,
  styleUrl: './block.component.scss'
})
export class BlockComponent implements OnInit{
  @Input() index!: number;
  @Input() block!: Block;
  @Input() module : Modul[] = []

  ngOnInit(): void {

  }

  setModulOfBlock(event : Event): void {
    const select = event.target as HTMLSelectElement;
    const modulId = select.value;
    this.block.setModulId(modulId)

    let modulName = ""

    for (let i = 0; i < this.module.length; i++) {
      if(this.module[i].fachId == modulId) {
        modulName = this.module[i].name
        break
      }
    }

    this.block.setModulName(modulName)

    console.log(`[Block ${this.index}] ModulId gesetzt: ${this.block.modulId}`);
    console.log(`[Block ${this.index}] ModulName gesetzt: ${this.block.modulName}`);
  }

  setLernzeitOfBlock(event : Event): void {
    const select = event.target as HTMLSelectElement;
    const minutes = Number(select.value);
    this.block.setLernzeitSeconds(minutes * 60);
    console.log(`[Block ${this.index}] Lernzeit gesetzt: ${this.block.lernzeitSeconds}`);
  }

  setPauseOfBlock(event : Event): void {
    const select = event.target as HTMLSelectElement;
    const minutes = Number(select.value);
    this.block.setPausezeitSeconds(minutes * 60)
    console.log(`[Block ${this.index}] Pause gesetzt: ${this.block.pausezeitSeconds}`);
  }
}
