import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Reschedule } from './reschedule';

describe('Reschedule', () => {
  let component: Reschedule;
  let fixture: ComponentFixture<Reschedule>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Reschedule]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Reschedule);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
