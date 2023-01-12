import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'app-ask-generic',
  templateUrl: './ask-generic.component.html',
  styleUrls: ['./ask-generic.component.scss']
})
export class AskGenericComponent {

  busy$ = new BehaviorSubject<boolean>(false)

  form = new FormGroup({
    websiteAbout: new FormControl('')
  });

  constructor(private httpClient: HttpClient) { }

  onSubmit() {

    const values = this.form.value

    this.busy$.next(true)

    this.httpClient.post('http://localhost:3000/api/test', values).subscribe({
      next: (result: any) => {
        console.log(result);

        this.busy$.next(false)
        const iframe = document.createElement('iframe') as HTMLIFrameElement;
        iframe.width = '100%';
        iframe.height = '600px';
        (document.getElementById('result') as any).appendChild(iframe)

        iframe.contentWindow?.document.open();
        iframe.contentWindow?.document.write(result.response);
        iframe.contentWindow?.document.close();


      }
    })
  }

  addIframe() {

  }
}
