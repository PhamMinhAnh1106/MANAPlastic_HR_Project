import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Leaverequestcheck } from './leaverequestcheck';

describe('Leaverequestcheck', () => {
  let component: Leaverequestcheck;
  let fixture: ComponentFixture<Leaverequestcheck>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Leaverequestcheck]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Leaverequestcheck);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
