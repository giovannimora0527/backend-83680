import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AnotacionesComponent } from './anotaciones.component';

describe('AnotacionesComponent', () => {
  let component: AnotacionesComponent;
  let fixture: ComponentFixture<AnotacionesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AnotacionesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AnotacionesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
