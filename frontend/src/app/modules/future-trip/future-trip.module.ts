import { NgModule } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';

import { FutureTripRoutingModule } from './future-trip-routing.module';
import { FutureTripComponent } from './pages/future-trip/future-trip.component';
import { SharedModule } from 'src/app/shared/shared.module';

@NgModule({
  declarations: [
    FutureTripComponent
  ],
  imports: [
    CommonModule,
    FutureTripRoutingModule,
    SharedModule
  ]
})
export class FutureTripModule { }
