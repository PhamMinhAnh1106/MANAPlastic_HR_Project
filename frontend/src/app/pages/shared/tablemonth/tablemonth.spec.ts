import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Tablemonth } from './tablemonth';

describe('Tablemonth', () => {
  let component: Tablemonth;
  let fixture: ComponentFixture<Tablemonth>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Tablemonth]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Tablemonth);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
