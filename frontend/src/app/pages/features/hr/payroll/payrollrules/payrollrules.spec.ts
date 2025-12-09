import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Payrollrules } from './payrollrules';

describe('Payrollrules', () => {
  let component: Payrollrules;
  let fixture: ComponentFixture<Payrollrules>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Payrollrules]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Payrollrules);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
