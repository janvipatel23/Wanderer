import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { HomeRoutingModule } from './home-routing.module';
import { HomeComponent } from './pages/home/home.component';
import { AgmCoreModule } from '@agm/core';
import { SharedModule } from 'src/app/shared/shared.module';
import { PinFormComponent } from './pages/pin-form/pin-form.component';


@NgModule({
  declarations: [
    HomeComponent,
    PinFormComponent
  ],
  imports: [
    CommonModule,
    HomeRoutingModule,
    AgmCoreModule,
    SharedModule,
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class HomeModule { }
