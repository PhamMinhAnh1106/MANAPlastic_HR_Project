import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DataAttendant } from './data-attendant';

describe('DataAttendant', () => {
  let component: DataAttendant;
  let fixture: ComponentFixture<DataAttendant>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DataAttendant]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DataAttendant);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
