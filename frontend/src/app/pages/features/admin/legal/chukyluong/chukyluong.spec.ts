import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Chukyluong } from './chukyluong';

describe('Chukyluong', () => {
  let component: Chukyluong;
  let fixture: ComponentFixture<Chukyluong>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Chukyluong]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Chukyluong);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
