import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContractsAdd } from './contracts-add';

describe('ContractsAdd', () => {
  let component: ContractsAdd;
  let fixture: ComponentFixture<ContractsAdd>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContractsAdd]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ContractsAdd);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
