import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Salarydate } from './salarydate';

describe('Salarydate', () => {
  let component: Salarydate;
  let fixture: ComponentFixture<Salarydate>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Salarydate]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Salarydate);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
