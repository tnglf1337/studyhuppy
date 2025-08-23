import {Component, inject, OnInit} from '@angular/core';
import {Session} from '../session-domain';
import {SessionApiService} from '../session-api.service';
import {NgForOf} from '@angular/common';
import {SnackbarService} from '../../../snackbar.service';

@Component({
  selector: 'app-delete',
  imports: [
    NgForOf
  ],
  templateUrl: './delete.component.html',
  standalone: true,
  styleUrls: ['./delete.component.scss', '../../../general.scss']
})
export class SessionDeleteComponent implements OnInit{
  sessionApiService = inject(SessionApiService)
  snackbarService = inject(SnackbarService)
  sessions : Session[] = []

  ngOnInit(): void {
    this.sessionApiService.getSessions().subscribe({
      next: data => {
        this.sessions = data
        console.log(this.sessions)
      }
    })
  }

  deleteSession(titel : string | undefined, fachId: string | undefined) {
    console.log("delete this session: ", fachId)
    this.sessionApiService.deleteSession(fachId).subscribe({
      next: response => {
        this.snackbarService.openSuccess(`Lern-Session ${titel} wurde erfolgreich gelöscht`)
        this.ngOnInit()
      },
      error: status => {
        if (status == 404) {
          this.snackbarService.openError("error")
        }
      }
    })
  }
}
