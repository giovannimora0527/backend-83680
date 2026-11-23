import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormulasMedicasComponent } from './formulas-medicas.component';

describe('FormulasMedicasComponent', () => {
  let component: FormulasMedicasComponent;
  let fixture: ComponentFixture<FormulasMedicasComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormulasMedicasComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FormulasMedicasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
