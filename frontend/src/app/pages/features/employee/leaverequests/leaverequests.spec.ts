import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Leaverequests } from './leaverequests';

describe('Leaverequests', () => {
  let component: Leaverequests;
  let fixture: ComponentFixture<Leaverequests>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Leaverequests]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Leaverequests);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
