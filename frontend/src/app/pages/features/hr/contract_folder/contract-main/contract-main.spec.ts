import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContractMain } from './contract-main';

describe('ContractMain', () => {
  let component: ContractMain;
  let fixture: ComponentFixture<ContractMain>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContractMain]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ContractMain);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
