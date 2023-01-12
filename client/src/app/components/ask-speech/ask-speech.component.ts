import { Component, OnInit } from '@angular/core';

declare let window: any;

@Component({
  selector: 'app-ask-speech',
  templateUrl: './ask-speech.component.html',
  styleUrls: ['./ask-speech.component.scss']
})
export class AskSpeechComponent {

  start() {
    var SpeechRecognition: any = SpeechRecognition || window.webkitSpeechRecognition
    var SpeechGrammarList: any = SpeechGrammarList || window.webkitSpeechGrammarList
    var SpeechRecognitionEvent: any = SpeechRecognitionEvent || window.webkitSpeechRecognitionEvent

    var recognition = new SpeechRecognition();
    if (SpeechGrammarList) {
      // SpeechGrammarList is not currently available in Safari, and does not have any effect in any other browser.
      // This code is provided as a demonstration of possible capability. You may choose not to use it.
      var speechRecognitionList = new SpeechGrammarList();;
      recognition.grammars = speechRecognitionList;
    }
    recognition.continuous = false;
    recognition.lang = 'en-US';
    recognition.interimResults = false;
    recognition.maxAlternatives = 1;

    var diagnostic = document.querySelector('#output')  as HTMLElement;
    var bg = document.querySelector('html')  as HTMLElement;

    recognition.onresult = function (event: any) {
      // The SpeechRecognitionEvent results property returns a SpeechRecognitionResultList object
      // The SpeechRecognitionResultList object contains SpeechRecognitionResult objects.
      // It has a getter so it can be accessed like an array
      // The first [0] returns the SpeechRecognitionResult at the last position.
      // Each SpeechRecognitionResult object contains SpeechRecognitionAlternative objects that contain individual results.
      // These also have getters so they can be accessed like arrays.
      // The second [0] returns the SpeechRecognitionAlternative at position 0.
      // We then return the transcript property of the SpeechRecognitionAlternative object
      var color = event.results[0][0].transcript;
      diagnostic.textContent = 'Result received: ' + color + '.';
      bg.style.backgroundColor = color;
      console.log('Confidence: ' + event.results[0][0].confidence);
    }

    recognition.onspeechend = function () {
      recognition.stop();
    }

    recognition.onnomatch = function () {
      diagnostic.textContent = "I didn't recognise that color.";
    }

    recognition.onerror = function (event: any) {
      diagnostic.textContent = 'Error occurred in recognition: ' + event.error;
    }

  }
}
