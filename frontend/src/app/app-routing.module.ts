import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AppComponent } from './app.component';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';

const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      {
        path: '',
        loadChildren: () =>
          import('./modules/home/home.module').then((m) => m.HomeModule),
      },
      {
        path: 'blog',
        loadChildren: () =>
          import('./modules/blog/blog.module').then((m) => m.BlogModule),
      },
      {
        path: 'login',
        loadChildren: () =>
          import('./modules/login/login.module').then((m) => m.LoginModule),
      },
      {
        path: 'user-profile',
        loadChildren: () =>
          import('./modules/user-profile/user-profile.module').then((m) => m.UserProfileModule),
      },
      {
        path: 'future-trip',
        loadChildren: () =>
          import('./modules/future-trip/future-trip.module').then((m) => m.FutureTripModule),
      },
      {
        path: 'about-us',
        loadChildren: () =>
          import('./modules/about-us/about-us.module').then((m) => m.AboutUsModule),
      },
    ],
  },
  {
    path: 'app',
    component: AppComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
