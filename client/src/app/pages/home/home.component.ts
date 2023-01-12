import { Component } from '@angular/core';
import { GptService } from 'src/app/services/gpt/gpt.service';

const count = 0

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent {
  active = ''

  constructor(private gptService: GptService) {}

  ngOnInit() {
    const synth = window.speechSynthesis
    let ourText
    let count = 0
    if (count++ === 0) {
      ourText = "Give a fucking input, or suck gigantic blue Balls"
    } else {
      ourText = 'How may I help you?'
    }

    const utterThis = new SpeechSynthesisUtterance(ourText)

    synth.speak(utterThis)
  }

}
