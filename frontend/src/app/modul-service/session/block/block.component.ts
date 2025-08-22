import {Component, inject, Input, OnInit} from '@angular/core';
import {ModuleApiService} from '../../module/module-api.service';
import {Modul} from '../../module/domain';
import {NgForOf} from '@angular/common';

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

  module : Modul[] = []
  modulService = inject(ModuleApiService)

  ngOnInit(): void {
    this.modulService.getAllModulesByUsername().subscribe(
      {
        next: data => {
          this.module = data
          console.log(this.module)
        }
      }
    )
  }
}
