import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AutoSchedule } from './auto-schedule';

describe('AutoSchedule', () => {
  let component: AutoSchedule;
  let fixture: ComponentFixture<AutoSchedule>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AutoSchedule]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AutoSchedule);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
