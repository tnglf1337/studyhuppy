import {inject, Injectable} from '@angular/core';
import { Modul} from './domain';
import { HttpClient } from '@angular/common/http';
import {Observable } from 'rxjs';
import {HeaderService} from '../../header.service';
import { environment } from '../../../environments/environment'
import {LoggingService} from '../../logging.service';

@Injectable({
  providedIn: 'root'
})
export class ModuleApiService {
  private MODUL_BASE_API = environment.modulApiUrl
  private headerService = inject(HeaderService)
  private http = inject(HttpClient)
  private log = new LoggingService("ModuleApiService", "modul-service")

  resetTimer(fachId: string): Observable<any> {
    this.log.debug(`Reset timer for modul id='${fachId}'...`)
    const headers = this.headerService.createAuthHeader()
    return this.http.put(this.MODUL_BASE_API + '/reset', {
      headers,
      params:
        {
          fachId: fachId
        }
    });
  }

  getModuleByFachsemester(): Observable<{ [key: number]: Modul[] }> {
    const headers = this.headerService.createAuthHeader()
    return this.http.get<{ [key: number]: Modul[] }>(this.MODUL_BASE_API + '/get-active-module2', {headers});
  }

  getAllModulesByUsername(): Observable<Modul[]> {
    const headers = this.headerService.createAuthHeader()
    return this.http.get<Modul[]>(this.MODUL_BASE_API + '/get-all-by-username', { headers });
  }

  getSeconds(fachId : string) : Observable<number> {
    const headers = this.headerService.createAuthHeader()
    return this.http.get<number>(this.MODUL_BASE_API + '/get-seconds',  {
      headers,
      params:
      {
        fachId: fachId
      }
    })
  }

  postNewSeconds(fachId: string, sessionSecondsLearned: number): Observable<any> {
    const element = document.getElementById(fachId)
    if (!element || !element.dataset['value']) return new Observable<any>()

    const seconds = parseInt(element.dataset['value'], 10)
    if (isNaN(seconds)) return new Observable<any>()

    const headers = this.headerService.createAuthHeader()

    const payload = {
      fachId: fachId,
      secondsLearned: seconds,
      secondsLearnedThisSession: sessionSecondsLearned
    }
    this.log.debug(`Try posting secondsLearned=${seconds} and secondsLearnedThisSession=${sessionSecondsLearned} for modul id='${fachId}'...`)
    return this.http.post<any>(this.MODUL_BASE_API + '/update', payload, {headers})
  }

  postRawSeconds(modulId : string, secondsToAdd : number) : Observable<any> {
    const headers = this.headerService.createAuthHeader()

    const payload = {
      modulId: modulId,
      secondsToAdd: secondsToAdd,
    }
    return this.http.post<any>(this.MODUL_BASE_API + '/add-seconds', payload, {headers})
  }

  postFormData(formData : any) : Observable<any> {
    this.log.debug(`Try posting new modul data=${formData}...`)
    const headers = this.headerService.createAuthHeader()
    return this.http.post(this.MODUL_BASE_API + '/new-modul', formData, {headers})
  }

  deleteModul(fachId: string) : Observable<void> {
    this.log.debug(`Try deleting modul id='${fachId}'...`)
    const headers = this.headerService.createAuthHeader()
    return this.http.delete<void>(this.MODUL_BASE_API + '/delete?fachId=' + fachId, {headers})
  }

  putAktivStatus(fachId: string) : Observable<void> {
    this.log.debug(`Try changing active status for modul id='${fachId}'...`)
    const headers = this.headerService.createAuthHeader()
    return this.http.put<void>(this.MODUL_BASE_API + '/change-active', {
      headers,
      params: {
        fachId: fachId}
      })
  }

  sendAddTimeData(data: any) : Observable<void> {
    this.log.debug(`Try sending additional time...`)
    const headers = this.headerService.createAuthHeader()
    return this.http.post<void>(this.MODUL_BASE_API + '/add-time', data, {headers})
  }

  sendKlausurDateData(data: any) : Observable<void> {
    this.log.debug(`Try sending klausur date...`)
    const headers = this.headerService.createAuthHeader()
    return this.http.post<void>(this.MODUL_BASE_API + '/add-klausur-date', data ,{headers})
  }

  getModulSelectData() {
    const headers = this.headerService.createAuthHeader()
    return this.http.get<any>(this.MODUL_BASE_API + '/get-modul-select-data', {headers})
  }
}
