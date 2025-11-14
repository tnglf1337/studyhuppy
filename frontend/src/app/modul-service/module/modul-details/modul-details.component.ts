import {Component, inject, OnInit} from '@angular/core';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {TimeFormatPipe} from '../time-format.pipe';
import {CapitalizePipe} from '../capitalize.pipe';
import {LoggingService} from '../../../logging.service';
import {MatSlideToggle} from "@angular/material/slide-toggle";
import {NgIf} from "@angular/common";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ModuleApiService} from '../module-api.service';
import {SnackbarService} from '../../../snackbar.service';

@Component({
  selector: 'app-modul-details',
  imports: [TimeFormatPipe, CapitalizePipe, MatSlideToggle, NgIf, ReactiveFormsModule, RouterLink],
  templateUrl: './modul-details.component.html',
  standalone: true,
  styleUrls: ['./modul-details.component.scss', '../../../general.scss', '../../../button.scss', '../../../color.scss']
})
export class ModulDetailsComponent implements  OnInit{
  log = new LoggingService("ModulDetailsComponent", "modul-service")

  route = inject(ActivatedRoute)
  service = inject(ModuleApiService)
  snackbarService = inject(SnackbarService)

  modulId! : string
  name!: string
  secondsLearned!: number
  kreditpunkte!: number
  kontaktzeitStunden!: number
  selbststudiumStunden!: number
  semesterstufe!: number
  semesterTyp!: string
  semesterJahr: string = '2000'
  lerntage!: string[]

  addTimeForm = new FormGroup({
    modulId: new FormControl("", Validators.required),
    time: new FormControl("", Validators.required)
  })

  ngOnInit(): void {
    this.initComponentWithQueryParamData()
  }

  initComponentWithQueryParamData() {
    this.route.queryParams.subscribe(params => {
      this.modulId = params['modulId']
      this.name = params['name']
      this.secondsLearned = params['secondsLearned']
      this.kreditpunkte = params['kreditpunkte']
      this.kontaktzeitStunden = params['kontaktzeitStunden']
      this.selbststudiumStunden = params['selbststudiumStunden']
      this.semesterstufe = params['semesterstufe']
      this.semesterTyp = params['semesterTyp']
      this.semesterJahr = this.getSemesterJahr(this.semesterTyp)
      this.lerntage = params['lerntage']

      this.queryParamLog()
    });
  }

  queryParamLog() {
    this.log.debug("name: " + this.name)
    this.log.debug("seconds: " + this.secondsLearned)
    this.log.debug("kp: " + this.kreditpunkte)
    this.log.debug("semesterstufe: " + this.semesterstufe)
    this.log.debug("semesterTyp: " + this.semesterTyp)
    this.log.debug("semesterJahr: " + this.semesterJahr)
    this.log.debug("lernage:" + this.lerntage)
  }

  getSemesterJahr(semesterTyp: string | undefined): string {
    let date = new Date().getFullYear()
    switch (semesterTyp){
      case 'SOMMERSEMESTER': return  date.toString()

      case 'WINTERSEMESTER': return  date + ' / ' + (date+1)

      default: return ''
    }
  }

  resetTimer() {
    this.service.resetTimer(this.modulId).subscribe({
      next: () => this.snackbarService.openSuccess("Timer wurde erfolgreich zurückgesetzt"),
      error: () => this.snackbarService.openError("Timer von konnte nicht zurückgesetzt werden"),
    })
  }

  deleteModul(): void {
    this.service.deleteModul(this.modulId).subscribe({
      next: () => {
        this.snackbarService.openSuccess("Modul erfolgreich gelöscht")
      },
      error: (err) => {
        this.snackbarService.openError(`Modul konnte nicht gelöscht werden. Grund: ${err}`)
      }
    });
  }

  putAktivStatus(fachId: string) {
    this.service.putAktivStatus(fachId).subscribe(() => {
      this.service.getAllModulesByUsername().subscribe({
        next: (data) => {
          // this.module = data; ????
        },
        error: (err) => {
          this.snackbarService.openError("Fehler beim Laden der Daten für Methode 'putAktivStatus'")
        }
      });
      this.snackbarService.openSuccess("Modulaktivität geändert")
    })
  }

  postSecondsToAdd() {
    this.addTimeForm.patchValue({modulId: this.modulId})
    if(this.addTimeForm.valid) {
      let data = this.addTimeForm.value
      console.log(data)
      this.service.postSecondsToAdd(data).subscribe({
        next: () => {
          this.log.debug("time data successfully sent")
          this.snackbarService.openSuccess("Zeit erfolgreich hinzugefügt")
          this.addTimeForm.patchValue({time: "--:--"})
        },
        error: (err) => {
          this.log.debug(`error sending time data. reason: ${err}`)
          this.snackbarService.openError("Zeit konnte nicht hinzugefügt werden")
        }
      })
    }
  }

  getActiveStatus(fachId: string) {
    //TODO implement
    return true
  }
}
