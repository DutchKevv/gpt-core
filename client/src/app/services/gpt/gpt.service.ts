import { HttpClient } from '@angular/common/http';
import { ChangeDetectorRef, Injectable } from '@angular/core';
import { BehaviorSubject, catchError, Observable, tap } from 'rxjs';
import { Capacitor } from '@capacitor/core';
import { environment } from '../../../environments/environment'

let host = 'https://real-estate.templatejump.com'

if (!environment.production) {
  host = Capacitor.isNativePlatform() ? 'http://10.0.2.2:3000' : 'http://localhost:3000'
}

@Injectable({
  providedIn: 'root'
})
export class GptService {

  busy$ = new BehaviorSubject<boolean>(null as any)
  speakDone$ = new BehaviorSubject<boolean>(false)
  content$ = new BehaviorSubject<string>(null as any)

  constructor(private httpClient: HttpClient) { }

  sendSpeach(text: string, tempChangeDetector: ChangeDetectorRef): Observable<{response: string}> {
    this.busy$.next(true)

    tempChangeDetector.detectChanges()
    return this.httpClient.post<{response: string}>(host + '/api/speak', { prompt: text })
      .pipe(
        tap(result => {
          this.content$.next(result.response)
          this.busy$.next(false)
        },
        catchError(error => {
          this.busy$.next(false)
          this.content$.next(error)
          throw error
        }))
      )
  }

  sendWebsite(values: any) {
    this.busy$.next(true)

    this.httpClient.post(host + '/api/website', values).subscribe({
      next: (result: any) => {
        console.log(result);

        this.busy$.next(false)
        this.content$.next(result.response)
        const iframe = document.createElement('iframe') as HTMLIFrameElement;
        iframe.width = '100%';
        iframe.height = '600px';

        (document.getElementById('result') as any).innerHTML = '';
        (document.getElementById('result') as any).appendChild(iframe)

        iframe.contentWindow?.document.open();
        iframe.contentWindow?.document.write(result.response);
        iframe.contentWindow?.document.close();
      }
    })
  }
}
