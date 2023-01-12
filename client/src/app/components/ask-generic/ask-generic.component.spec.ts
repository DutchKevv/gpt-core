import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AskGenericComponent } from './ask-generic.component';

describe('AskGenericComponent', () => {
  let component: AskGenericComponent;
  let fixture: ComponentFixture<AskGenericComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AskGenericComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AskGenericComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
