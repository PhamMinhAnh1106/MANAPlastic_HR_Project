import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RewaPunis } from './rewa-punis';

describe('RewaPunis', () => {
  let component: RewaPunis;
  let fixture: ComponentFixture<RewaPunis>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RewaPunis]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RewaPunis);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
