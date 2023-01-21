import { ChangeDetectionStrategy, ChangeDetectorRef, Component } from '@angular/core';
import { Subscription } from 'rxjs';
import { GptService } from 'src/app/services/gpt/gpt.service';
import { SpeechService } from 'src/app/services/speech/speech.service';

const storyText = `
  Create a short child story about ###ANIMALS###.
  That is very ###FLOW### and full of jokes.
  Describe the food this animal likes.
  Tell all about the history and future.
  Tell about the history of ###ANIMALS### also include a history from wikipedia and old songs, from the ###TIME###.
`

const continueText = `
  Write the next chapter.
  This chapter should be around 500 words.
  Continue on the previous chapter.
  Use a logical structure and very colorful descriptions.
  Add a short chapter name that triggers the user to read it"
`

@Component({
  selector: 'app-story-page',
  templateUrl: './story-page.component.html',
  styleUrls: ['./story-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.Default
})
export class StoryPageComponent {

  labels: any = {
    animals: [],
    time: ['future'],
    flow: ['dark'],
  }

  options = {
    animals: ['dogs', 'cats', 'devils', 'monkeys', 'white humans', 'fish'],
    time: ["long ago", 'present', 'future'],
    flow: ['happy', 'dark', 'boring']
  }

  outputText = ''
  partCount = 1

  private subscriptions: Subscription[] = []

  constructor(
    public gptService: GptService,
    public speechService: SpeechService,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.createText()
  }

  ngOnDestroy() {
    this.subscriptions.forEach(sub => sub.unsubscribe())
  }

  addDetail(type: string, text: string) {
    this.labels[type.toLowerCase()].push(text)
    this.createText();
    this.changeDetectorRef.detectChanges()
  }

  removeDetail(name: string, type: string): void {
    this.labels[type].splice(this.labels[type].indexOf(name), 1);
  }

  createText() {
    this.outputText = storyText
    this.outputText = this.outputText.replaceAll('###ANIMALS###', this.labels.animals.join(' and '))
    this.outputText = this.outputText.replace('###TIME###', this.labels.time.join(','))
    this.outputText = this.outputText.replace('###FLOW###', this.labels.flow.join(','))
  }

  send(text?: string) {
    this.partCount = text ? this.partCount + 1 : 1;

    this.gptService.sendSpeach(text || this.outputText, this.changeDetectorRef).subscribe(async result => {

      this.changeDetectorRef.detectChanges()

      setTimeout(() => {
        const element = document.getElementById('result')

        element?.scrollIntoView({
          behavior: 'smooth',
          block: 'start',
          inline: 'start',
        });
      }, 500)

      await this.speechService.speak(result.response);

      this.speechService.speaking$.subscribe(speaking => {
        if (!speaking) {
          this.send(continueText);
        }
      })
    })
  }

  shutup() {
    this.speechService.shutup()
    this.changeDetectorRef.detectChanges()
  }
}
