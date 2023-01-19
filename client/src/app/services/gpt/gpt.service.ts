import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { SpeechService } from '../speech/speech.service';

declare let window: any;
declare let Capacitor: any;

const host = ''
// const host = Capacitor.isNativePlatform() ? 'http://10.0.2.2:3000' : 'http://localhost:3000'

@Injectable({
  providedIn: 'root'
})
export class GptService {

  busy$ = new BehaviorSubject<boolean>(null as any)
  speakDone$ = new BehaviorSubject<boolean>(false)
  content$ = new BehaviorSubject<string>(null as any)

  constructor(private httpClient: HttpClient) { }

  sendSpeach(text: string): Observable<{response: string}> {
    this.busy$.next(true)

    return this.httpClient.post<{response: string}>(host + '/api/speak', { prompt: text }).pipe(tap(result => {
      this.content$.next(result.response)
      this.busy$.next(false)
    }))
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
