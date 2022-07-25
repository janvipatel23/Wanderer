import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from 'src/app/auth.guard';
import { FutureTripComponent } from './pages/future-trip/future-trip.component';

const routes: Routes = [
  {
    path: '',
    component: FutureTripComponent,
    canActivate: [AuthGuard],
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class FutureTripRoutingModule { }
