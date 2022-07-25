import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UserProfileRoutingModule } from './user-profile-routing.module';
import { UserProfileComponent } from './pages/user-profile/user-profile.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { BucketListComponent } from './pages/bucket-list/bucket-list.component';


@NgModule({
  declarations: [
    UserProfileComponent,
    BucketListComponent
  ],
  imports: [
    CommonModule,
    UserProfileRoutingModule,
    SharedModule,
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class UserProfileModule { }
