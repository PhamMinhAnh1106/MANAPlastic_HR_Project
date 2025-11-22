import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Comfirm } from './comfirm';

describe('Comfirm', () => {
  let component: Comfirm;
  let fixture: ComponentFixture<Comfirm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Comfirm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Comfirm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
