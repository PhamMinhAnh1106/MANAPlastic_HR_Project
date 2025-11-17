import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ADdleaverequest } from './addleaverequest';

describe('ADdleaverequest', () => {
  let component: ADdleaverequest;
  let fixture: ComponentFixture<ADdleaverequest>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ADdleaverequest]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ADdleaverequest);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
