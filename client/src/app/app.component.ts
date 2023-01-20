import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss'],
  changeDetection: ChangeDetectionStrategy.Default
})
export class AppComponent {

  public appPages = [
    { title: 'Question', url: '', icon: 'mail' },
    { title: 'Story', url: '/story', icon: 'mail' },
  ];
  public labels = ['Family', 'Friends', 'Notes', 'Work', 'Travel', 'Reminders'];
  constructor() { }

  ngOnInit() {


  }
}
