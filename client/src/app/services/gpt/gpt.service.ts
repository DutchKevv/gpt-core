import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

const synth = window.speechSynthesis

@Injectable({
  providedIn: 'root'
})
export class GptService {

  busy$ = new BehaviorSubject<boolean>(false)
  speakDone$ = new BehaviorSubject<boolean>(false)

  constructor(private httpClient: HttpClient) { }

  shutup() {
    synth.cancel()
  }

  speak(values: any) {

    this.busy$.next(true)
    this.speakDone$.next(false)

    this.httpClient.post('http://localhost:3000/api/speak', values).subscribe({
      next: (result: any) => {
        console.log(result);

        this.busy$.next(false)

        let ourText = result.response
        const speech = new SpeechSynthesisUtterance(ourText)
        speech.onend = () => {
          this.speakDone$.next(true)
        }
        synth.speak(speech);

        (document.getElementById('result') as any).innerText = result.response;
      }
    })
  }

  send(values: any) {

    this.busy$.next(true)

    this.httpClient.post('http://localhost:3000/api/website', values).subscribe({
      next: (result: any) => {
        console.log(result);

        this.busy$.next(false)

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
