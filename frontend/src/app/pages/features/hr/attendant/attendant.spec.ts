import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Attendant } from './attendant';

describe('Attendant', () => {
  let component: Attendant;
  let fixture: ComponentFixture<Attendant>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Attendant]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Attendant);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
