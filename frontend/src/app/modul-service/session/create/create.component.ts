import {Component, inject, OnInit} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {NgForOf} from '@angular/common';
import {BlockComponent} from '../block/block.component';
import {SessionApiService} from '../session-api.service';
import {Modul} from '../../module/domain';
import {ModuleApiService} from '../../module/module-api.service';
import {Block, Session} from '../session-domain';
import {SnackbarService} from '../../../snackbar.service';

@Component({
  selector: 'app-session-create',
  imports: [BlockComponent, FormsModule, NgForOf, ReactiveFormsModule],
  templateUrl: './create.component.html',
  styleUrls: ['./create.component.scss', '../../../general.scss', '../../../button.scss', '../../../forms.scss', '../../../color.scss']
})
export class SessionCreateComponent implements OnInit{
  snackbarService = inject(SnackbarService)
  sessionApiService = inject(SessionApiService)
  anzahlBloecke : number = 2;
  sessionTitel : string = "";
  sessionBeschreibung : string = "";
  session: any
  module : Modul[] = []
  modulService = inject(ModuleApiService)

  ngOnInit(): void {
    this.modulService.getAllModulesByUsername().subscribe(
      {
        next: data => {
          this.module = data
          // console.log(this.module)
        }
      }
    )
    this.setSessionConfigData()
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
    this.session = new Session(this.sessionTitel, this.sessionBeschreibung, blocks);
    console.log(this.session)
  }

  getBlock(index : number) : Block {
    return this.session.blocks[index];
  }

  saveSession(): void {
    if(this.session.validSession()) {
      this.sessionApiService.saveSession(this.session).subscribe({
        next: (response) => {
          this.snackbarService.openInfo("Session erfolgreich gespeichert.");
          this.setSessionConfigData();
        },
        error: (error) => {
          this.snackbarService.openError("Fehler beim Speichern der Session. Bitte versuchen Sie es erneut.");
          console.error("Error saving session:", error);
        }
      })
    } else {
      console.error("Session ist ungültig. Bitte überprüfen Sie die Eingaben.");
    }
  }
}
