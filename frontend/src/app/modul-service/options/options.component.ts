import {Component, inject, OnInit} from '@angular/core';
import {Modul} from '../module/domain';
import {CommonModule} from '@angular/common';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatSlideToggle} from '@angular/material/slide-toggle';
import {ModuleApiService} from '../module/module-api.service';
import {LoggingService} from '../../logging.service';
import {SnackbarService} from '../../snackbar.service';

@Component({
  selector: 'app-options',
  imports: [CommonModule, ReactiveFormsModule, FormsModule, MatSlideToggle],
  templateUrl: './options.component.html',
  standalone: true,
  styleUrls: ['./options.component.scss', '../../color.scss', '../../general.scss']
})
export class OptionsComponent implements OnInit{
  log = new LoggingService("OptionsComponent", "modul-service")
  sb = inject(SnackbarService)
  service = inject(ModuleApiService)

  module : Modul[] = []
  modulFachId : string = ''

  addTimeForm = new FormGroup({
    fachId: new FormControl("", Validators.required),
    time: new FormControl("", Validators.required)
  })

  addKlausurDate = new FormGroup({
    fachId: new FormControl("", Validators.required),
    date: new FormControl("", Validators.required),
    time: new FormControl("", Validators.required)
  })

  ngOnInit(): void {
    this.service.getAllModulesByUsername().subscribe({
      next: (data) => {
        this.module = data;
        this.log.debug(`Got data: ${data}`)
      },
      error: (err) => {
        this.log.error("Could not get data for service")
      }
    });
  }

  resetTimer() {
    this.service.resetTimer(this.modulFachId).subscribe({
      next: () => this.sb.openSuccess("Timer wurde erfolgreich zurückgesetzt"),
      error: () => this.sb.openError("Timer von konnte nicht zurückgesetzt werden"),
    })
  }

  deleteModul(): void {
    this.service.deleteModul(this.modulFachId).subscribe({
      next: () => {
        this.module = this.module.filter(modul => modul.fachId !== this.modulFachId);
        this.sb.openSuccess("Modul erfolgreich gelöscht")
      },
      error: (err) => {
        this.sb.openError(`Modul konnte nicht gelöscht werden. Grund: ${err}`)
      }
    });
  }

  putAktivStatus(fachId: string) {
    this.service.putAktivStatus(fachId).subscribe(() => {
      this.service.getAllModulesByUsername().subscribe({
        next: (data) => {
          this.module = data;
        },
        error: (err) => {
          this.sb.openError("Fehler beim Laden der Daten für Methode 'putAktivStatus'")
        }
      });
      this.sb.openSuccess("Modulaktivität geändert")
    })
  }

  sendAddTimeData() {
    this.addTimeForm.patchValue({fachId: this.modulFachId})
    if(this.addTimeForm.valid) {
      let data = this.addTimeForm.value
      this.service.sendAddTimeData(data).subscribe({
        next: () => {
          this.log.debug("time data successfully sent")
          this.sb.openSuccess("Zeit erfolgreich hinzugefügt")
        },
        error: (err) => {
          this.log.debug(`error sending time data. reason: ${err}`)
          this.sb.openError("Zeit konnte nicht hinzugefügt werden")
        }
      })
    }
  }

  sendKlausurDateData() {
    this.addKlausurDate.patchValue({fachId: this.modulFachId})
    if(this.addKlausurDate.valid) {
      let data = this.addKlausurDate.value
      this.service.sendKlausurDateData(data).subscribe({
        next: () => {
          this.log.debug("klausur date data successfully sent")
          this.sb.openSuccess("Klausurdatum-Daten erfolgreich versendet")
        },
        error: (err) => {
          this.log.debug(`error sending klausur date data. reason: ${err}`)
          this.sb.openError("Klausurdatum konnte nicht eingetragen werden")
        }
      })
    }
  }

  selectModule(fachId: Event) {
    const selectElement = fachId.target as HTMLSelectElement;
    this.modulFachId = selectElement.value
    this.log.debug(`selected modul: ${this.modulFachId}`)
  }

  getActiveStatus(fachId: string) {
    const modul = this.module.find(m => m.fachId === fachId);
    let isActive : boolean = modul ? modul.active : false
    return isActive
  }
}
