import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpHeaders,
  HttpErrorResponse,
  HTTP_INTERCEPTORS
} from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { AuthService } from '../auth-service/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService, private router: Router) { }

  intercept(
    request: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    let authReq = request;
    const token = this.authService.getToken();

    if(token !== null) {
      authReq = request.clone({
        withCredentials: true,
        headers: request.headers.set('Access-Control-Allow-Origin', '*')
          .set('Access-Control-Allow-Credentials', 'true')
          .set('Authorization', 'Bearer ' + token)
      });
    } else {
      authReq = request.clone({
        withCredentials: true,
        headers: request.headers.set('Access-Control-Allow-Origin', '*')
          .set('Access-Control-Allow-Credentials', 'true')
      });
    }

    return next.handle(authReq).pipe(tap(() => { }, (err) => {
      if (err instanceof HttpErrorResponse) {
        if (err.status !== 401 || window.location.pathname === '/login') {
          return;
        }
        // this.token.signOut();
        this.router.navigate(['login']);
      }
    }));
  }
}

export const authInterceptorProviders = [
  { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
];
