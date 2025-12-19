import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LeavePolicies } from './leave-policies';

describe('LeavePolicies', () => {
  let component: LeavePolicies;
  let fixture: ComponentFixture<LeavePolicies>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LeavePolicies]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LeavePolicies);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
