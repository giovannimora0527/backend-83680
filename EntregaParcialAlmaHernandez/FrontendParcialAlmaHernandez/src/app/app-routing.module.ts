import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AdminComponent } from './theme/layout/admin/admin.component';
import { UsuarioComponent } from './demo/pages/usuario/usuario.component';
import { MascotaComponent } from './demo/pages/mascota/mascota.component';
import { ClienteComponent } from './demo/pages/cliente/cliente.component';
import { PacientesComponent } from './demo/pages/pacientes/pacientes.component';
import { MedicosComponent } from './demo/pages/medicos/medicos.component';
import { EspecializacionesComponent } from './demo/pages/especializaciones/especializaciones.component';
import { CitasComponent } from './demo/pages/citas/citas.component';
import { HistoriasMedicasComponent } from './demo/pages/historias-medicas/historias-medicas.component';
import { AnotacionesComponent } from './demo/pages/anotaciones/anotaciones.component';
import { MedicamentosComponent } from './demo/pages/medicamentos/medicamentos.component';
import { FormulasMedicasComponent } from './demo/pages/formulas-medicas/formulas-medicas.component';
import { DespachoComponent } from './demo/pages/despacho/despacho.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'inicio',
    pathMatch: 'full'
  },
  {
    path: 'inicio',
    component: AdminComponent,
    data: { title: 'Inicio' },
    children: [
      { path: 'usuarios', component: UsuarioComponent, data: { title: 'Usuarios' } },
      { path: 'clientes', component: ClienteComponent, data: { title: 'Clientes' } },
      { path: 'mascotas', component: MascotaComponent, data: { title: 'Mascotas' } },
      { path: 'pacientes', component: PacientesComponent, data: { title: 'Pacientes' } },
      { path: 'medicos', component: MedicosComponent, data: { title: 'Médicos' } },
      { path: 'especializaciones', component: EspecializacionesComponent, data: { title: 'Especializaciones' } },
      { path: 'citas', component: CitasComponent, data: { title: 'Citas' } },
      { path: 'historias-medicas', component: HistoriasMedicasComponent, data: { title: 'Historias Médicas' } },
      { path: 'anotaciones', component: AnotacionesComponent, data: { title: 'Anotaciones' } },
      { path: 'medicamentos', component: MedicamentosComponent, data: { title: 'Medicamentos' } },
      { path: 'formulas-medicas', component: FormulasMedicasComponent, data: { title: 'Fórmulas Médicas' } },
      { path: 'despacho', component: DespachoComponent, data: { title: 'Despacho Médico' } }
    ]
  },
  { path: '**', redirectTo: 'inicio' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}