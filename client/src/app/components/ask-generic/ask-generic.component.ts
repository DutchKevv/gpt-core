import { HttpClient } from '@angular/common/http';
import { Component, Input } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import { BehaviorSubject } from 'rxjs';
import { GptService } from 'src/app/services/gpt/gpt.service';

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

  constructor(public gptService: GptService) { }

  onSubmit() {

    const values = this.form.value

    this.busy$.next(true)

    this.gptService.send(values)

  }

  addIframe() {

  }
}
