import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterSchedule } from './register-schedule';

describe('RegisterSchedule', () => {
  let component: RegisterSchedule;
  let fixture: ComponentFixture<RegisterSchedule>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegisterSchedule]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegisterSchedule);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
