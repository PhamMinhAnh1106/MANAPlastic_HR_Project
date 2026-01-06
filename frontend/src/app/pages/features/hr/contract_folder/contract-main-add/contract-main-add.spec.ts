import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContractMainAdd } from './contract-main-add';

describe('ContractMainAdd', () => {
  let component: ContractMainAdd;
  let fixture: ComponentFixture<ContractMainAdd>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContractMainAdd]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ContractMainAdd);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
