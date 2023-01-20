import { Injectable } from '@angular/core';
import { SpeechRecognition } from '@capacitor-community/speech-recognition';
import { Capacitor } from '@capacitor/core';
import { BehaviorSubject } from 'rxjs';
import { TextToSpeech } from '@capacitor-community/text-to-speech';

// const getSupportedLanguages = async () => {
//   const languages = await TextToSpeech.getSupportedLanguages();
// };

// const isLanguageSupported = async (lang: string) => {
//   const isSupported = await TextToSpeech.isLanguageSupported({ lang });
// };

declare let window: any

const synth = window.speechSynthesis

@Injectable({
  providedIn: 'root'
})
export class SpeechService {

  speaking$ = new BehaviorSubject<boolean>(false)
  listening$ = new BehaviorSubject<boolean>(false)
  content$ = new BehaviorSubject<string>(null)

  private browserRecognition: any

  initListenerBrowser() {
    var SpeechRecognition: any = SpeechRecognition || window.webkitSpeechRecognition
    var SpeechGrammarList: any = SpeechGrammarList || window.webkitSpeechGrammarList
    var SpeechRecognitionEvent: any = SpeechRecognitionEvent || window.webkitSpeechRecognitionEvent

    this.browserRecognition = new SpeechRecognition();
    if (SpeechGrammarList) {
      // SpeechGrammarList is not currently available in Safari, and does not have any effect in any other browser.
      // This code is provided as a demonstration of possible capability. You may choose not to use it.
      var speechRecognitionList = new SpeechGrammarList();;
      this.browserRecognition.grammars = speechRecognitionList;
    }
    this.browserRecognition.continuous = false;
    this.browserRecognition.lang = 'en-US';
    this.browserRecognition.interimResults = false;
    this.browserRecognition.maxAlternatives = 1;

    this.browserRecognition.onresult = (event: any) => {
      this.browserRecognition.stop();

      // The SpeechRecognitionEvent results property returns a SpeechRecognitionResultList object
      // The SpeechRecognitionResultList object contains SpeechRecognitionResult objects.
      // It has a getter so it can be accessed like an array
      // The first [0] returns the SpeechRecognitionResult at the last position.
      // Each SpeechRecognitionResult object contains SpeechRecognitionAlternative objects that contain individual results.
      // These also have getters so they can be accessed like arrays.
      // The second [0] returns the SpeechRecognitionAlternative at position 0.
      // We then return the transcript property of the SpeechRecognitionAlternative object
      var transcript = event.results[0][0].transcript;

      // this.speak({
      //   prompt: color
      // })

      console.log(transcript)
      console.log('Confidence: ' + event.results[0][0].confidence);

      if (transcript) {
        this.content$.next(transcript)
      }
    }

    this.browserRecognition.onspeechend = () => {
      console.info('speech end')
      this.browserRecognition.stop();
      this.listening$.next(false)
    }

    this.browserRecognition.onnomatch = () => {
      this.listening$.next(false)
      this.browserRecognition.stop();
      // diagnostic.textContent = "I didn't recognise that color.";
    }

    this.browserRecognition.onerror = (event: any) => {
      console.error(event)
      // diagnostic.textContent = 'Error occurred in recognition: ' + event.error;
      this.browserRecognition.stop()
      this.listening$.next(false)
      this.content$.next(null)
      // throw event
      // this.listen()
    }
  }

  async initListinerApp() {
    /**
    * This method will check for audio permissions.
    * @param none
    * @returns permission - boolean true/false if permissions are granted
    */
    const hasPermission = await SpeechRecognition.hasPermission();

    if (!hasPermission.permission) {
      /**
       * This method will prompt the user for audio permission.
       * @param none
       * @returns void
       */
      const getPermission = await SpeechRecognition.requestPermission();
      console.log('getPermission', getPermission)
      this.listening$.next(false)

      return
    }
  }
  async shutup() {
    Capacitor.isNativePlatform() ? await TextToSpeech.stop() : synth?.cancel()
    this.speaking$.next(false)
  }

  async speak(text: string) {
    Capacitor.isNativePlatform() ? await this.speakApp(text) : await this.speakBrowser(text)
  }

  async speakApp(text: string) {
    this.speaking$.next(true)

    await TextToSpeech.speak({
      text,
      lang: 'en-US',
      rate: 1.0,
      pitch: 1.0,
      volume: 1.0,
      category: 'ambient',
    });

    this.speaking$.next(false)
  }

  async speakBrowser(text: string) {
    await this.shutup();

    const speech = new SpeechSynthesisUtterance(text)
    speech.onend = () => this.speaking$.next(false)

    this.speaking$.next(true)
    synth.speak(speech);
  }

  async listen() {
    await this.shutup()

    try {
      if (Capacitor.isNativePlatform()) {
        await this.listenApp()
      } else {
        this.listenBrowser()
      }
    } catch (error) {
      console.error(error)
      this.listening$.next(false)
    }

  }

  async listenApp() {
    const hasPermission = await SpeechRecognition.hasPermission();

    if (!hasPermission.permission) {
      await this.initListinerApp();
    }

    this.listening$.next(true)

    const isAvailable = await SpeechRecognition.available();

    console.log('hasPermission', hasPermission);


    // listin to partial results
    const listenerResult = await SpeechRecognition.addListener("partialResults", (data: any) => {
      console.log("partialResults was fired", data);
    });

    const result = await SpeechRecognition.start({
      language: "en-US",
      maxResults: 2,
      prompt: "Say something",
      partialResults: false,
      popup: false,
    });

    this.listening$.next(false)

    if (result.matches[0]) {
      this.content$.next(result.matches[0])
    } else {
      throw 'matches'
    }

    console.log(4444, result);
  }

  listenBrowser() {
    this.listening$.next(true)

    if (!this.browserRecognition) {
      this.initListenerBrowser()
    }

    this.browserRecognition.start();
  }

  async getVoices() {
    return Capacitor.isNativePlatform() ? this.getVoicesApp() : this.getVoicesBrowser()
  }

  async getVoicesBrowser() {
    let voices = speechSynthesis.getVoices();

    if (!voices.length) {
      let utterance = new SpeechSynthesisUtterance("");
      speechSynthesis.speak(utterance);
      voices = speechSynthesis.getVoices();
    }

    return voices;
  }

  async getVoicesApp() {
    const voices = await TextToSpeech.getSupportedVoices()
    return voices
  }

}
