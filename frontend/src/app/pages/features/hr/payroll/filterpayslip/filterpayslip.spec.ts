import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Filterpayslip } from './filterpayslip';

describe('Filterpayslip', () => {
  let component: Filterpayslip;
  let fixture: ComponentFixture<Filterpayslip>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Filterpayslip]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Filterpayslip);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
