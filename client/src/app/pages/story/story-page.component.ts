import { ChangeDetectionStrategy, ChangeDetectorRef, Component } from '@angular/core';
import { distinctUntilChanged, Subscription } from 'rxjs';
import { GptService } from 'src/app/services/gpt/gpt.service';
import { SpeechService } from 'src/app/services/speech/speech.service';

@Component({
  selector: 'app-story-page',
  templateUrl: './story-page.component.html',
  styleUrls: ['./story-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.Default
})
export class StoryPageComponent {

  active = ''

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

  private subscriptions: Subscription[] = []
  private text = "Create a very long story about ###ANIMALS###. That is very ###FLOW### and full of jokes. Describe the food this animal likes. Add wikipedia data. Tell all about the history and future. Tell about the history of ###ANIMALS### also include a history from wikipedia and old songs, from the ###TIME###. After that, tell a dad joke"

  constructor(
    public gptService: GptService,
    public speechService: SpeechService,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  addDetail(type: string, text: string) {
    this.labels[type.toLowerCase()].push(text)
    this.text = this.text.replace('###' + type + '###', text)
  }

  finish() {
    this.text = this.text.replace(/'###ANIMALS###'/g, this.labels.animals.join(' and '))
    this.text = this.text.replace('###TIME###', this.labels.time.join(','))
    this.text = this.text.replace('###FLOW###', this.labels.flow.join(','))

    this.gptService.sendSpeach(this.text, this.changeDetectorRef).subscribe(async result => {

      this.changeDetectorRef.detectChanges()

      setTimeout(() => {
        const element = document.getElementById('result')

        element.scrollIntoView({
          behavior: 'smooth',
          block: 'start',
          inline: 'start',
        });
      }, 100)

      await this.speechService.speak(result.response)

      console.log(222222  )
    })
  }


  ngOnInit() {
    this.subscriptions.push(
      this.speechService.speaking$.subscribe(speaking => {
        setTimeout(() => {
          this.changeDetectorRef.detectChanges()
        })
      })
    )
  }

  ngOnDestroy() {
    this.subscriptions.forEach(sub => sub.unsubscribe())
  }

  shutup() {
    this.speechService.shutup()
    this.changeDetectorRef.detectChanges()
  }

  start() {
    this.speechService.listen();
  }
}
