import {inject, Injectable} from '@angular/core';
import {MatSnackBar} from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class SnackbarService {

  DURATION : number = 3500

  snackbar = inject(MatSnackBar)

  openInfo(message : string) {
    this.snackbar.open(message, "schließen", {
      duration: this.DURATION,
      panelClass: ['snackbar-info']
    })
  }

  openSuccess(message : string) {
    this.snackbar.open(message, "schließen", {
      duration: this.DURATION,
      panelClass: ['snackbar-success']
    })
  }

  openError(message : string) {
    this.snackbar.open(message, "schließen", {
      duration: this.DURATION,
      panelClass: ['snackbar-error']
    })
  }

  setDuration(dur : number) {
    this.DURATION = dur
  }
}
