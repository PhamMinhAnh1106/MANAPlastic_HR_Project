import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Payrollpayslip } from './payrollpayslip';

describe('Payrollpayslip', () => {
  let component: Payrollpayslip;
  let fixture: ComponentFixture<Payrollpayslip>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Payrollpayslip]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Payrollpayslip);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
