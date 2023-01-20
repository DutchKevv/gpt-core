import { ChangeDetectionStrategy, ChangeDetectorRef, Component } from '@angular/core';
import { distinctUntilChanged, Subscription } from 'rxjs';
import { GptService } from 'src/app/services/gpt/gpt.service';
import { SpeechService } from 'src/app/services/speech/speech.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
  changeDetection: ChangeDetectionStrategy.Default
})
export class HomeComponent {

  active = ''

  private subscriptions: Subscription[] = []

  constructor(
    public gptService: GptService,
    public speechService: SpeechService,
    private changeDetectorRef: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.subscriptions.push(
      this.speechService.speaking$.subscribe(speaking => {
        setTimeout(() => {
          this.changeDetectorRef.detectChanges()
        })
      })
    )

    this.subscriptions.push(
      this.speechService.listening$.subscribe(listening => {
        setTimeout(() => {
          this.changeDetectorRef.detectChanges()
        })
      })
    )

    this.subscriptions.push(

      this.speechService.content$.subscribe(content => {
        console.log(2222)
        this.changeDetectorRef.detectChanges()

        if (content) {
          setTimeout(() => {
            this.changeDetectorRef.detectChanges()
          })


          this.gptService.sendSpeach(content, this.changeDetectorRef).subscribe(async result => {

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
          })
        }
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
