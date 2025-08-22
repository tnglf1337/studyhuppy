import {environment} from '../../../environments/environment';
import {inject, Injectable} from '@angular/core';
import {HeaderService} from '../../header.service';
import {HttpClient} from '@angular/common/http';
import {LoggingService} from '../../logging.service';
import {Session} from './session-domain';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SessionApiService {
  private SESSION_BASE_API = environment.sessionApiUrl
  private headerService = inject(HeaderService)
  private http = inject(HttpClient)
  private log = new LoggingService("SessionApiService", "session-service")

  saveSession(session: Session): Observable<any> {
    const headers = this.headerService.createAuthHeader()
    return this.http.post<any>(this.SESSION_BASE_API + '/create', session, {headers})
  }

  getSessions(): Observable<any> {
    const headers = this.headerService.createAuthHeader()
    return this.http.get<any>(this.SESSION_BASE_API + '/get-sessions', {headers})
  }
}
