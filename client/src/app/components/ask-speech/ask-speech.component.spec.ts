import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AskSpeechComponent } from './ask-speech.component';

describe('AskSpeechComponent', () => {
  let component: AskSpeechComponent;
  let fixture: ComponentFixture<AskSpeechComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AskSpeechComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AskSpeechComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
