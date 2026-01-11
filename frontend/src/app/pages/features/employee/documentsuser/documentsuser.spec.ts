import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Documentsuser } from './documentsuser';

describe('Documentsuser', () => {
  let component: Documentsuser;
  let fixture: ComponentFixture<Documentsuser>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Documentsuser]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Documentsuser);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
