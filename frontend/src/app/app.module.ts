import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';
import { FooterComponent } from './layout/footer/footer.component';
import { NavbarComponent } from './layout/navbar/navbar.component';

import { provideFirebaseApp, initializeApp } from '@angular/fire/app';
import { getFirestore, provideFirestore } from '@angular/fire/firestore';

import { AgmCoreModule } from '@agm/core';

// PrimeNg Imports
import {MenubarModule} from 'primeng/menubar';
import {CardModule} from 'primeng/card';

import { environment } from 'src/environments/environment';
import { SharedModule } from './shared/shared.module';
import { LoginModule } from './modules/login/login.module';
import { CookieService } from 'ngx-cookie-service';
import { authInterceptorProviders } from './data/service/httpinterceptor/auth.interceptor';

//ngx-toastr import
import { ToastrModule, ToastrService } from 'ngx-toastr';
import { FutureTripModule } from './modules/future-trip/future-trip.module';

@NgModule({
  declarations: [
    AppComponent,
    MainLayoutComponent,
    FooterComponent,
    NavbarComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    HttpClientModule,
    SharedModule,
    MenubarModule,
    CardModule,
    LoginModule,
    FutureTripModule,
    provideFirebaseApp(() => initializeApp(environment.firebase)),
    provideFirestore(() => getFirestore()),
    AgmCoreModule.forRoot({
      apiKey: environment.googleMaps
    }),    
    ToastrModule.forRoot()
  ],
  providers: [CookieService, authInterceptorProviders, ToastrService],
  bootstrap: [AppComponent]
})
export class AppModule { }
