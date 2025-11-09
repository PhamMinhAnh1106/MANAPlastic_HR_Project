import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InfoBase } from './info-base';

describe('InfoBase', () => {
  let component: InfoBase;
  let fixture: ComponentFixture<InfoBase>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InfoBase]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InfoBase);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
