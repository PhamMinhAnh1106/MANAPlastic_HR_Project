import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Payrollmain } from './payrollmain';

describe('Payrollmain', () => {
  let component: Payrollmain;
  let fixture: ComponentFixture<Payrollmain>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Payrollmain]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Payrollmain);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
