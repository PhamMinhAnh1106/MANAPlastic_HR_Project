import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageOvertime } from './manage-overtime';

describe('ManageOvertime', () => {
  let component: ManageOvertime;
  let fixture: ComponentFixture<ManageOvertime>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManageOvertime]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManageOvertime);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
