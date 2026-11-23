import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HistoriasMedicasComponent } from './historias-medicas.component';

describe('HistoriasMedicasComponent', () => {
  let component: HistoriasMedicasComponent;
  let fixture: ComponentFixture<HistoriasMedicasComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HistoriasMedicasComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HistoriasMedicasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
