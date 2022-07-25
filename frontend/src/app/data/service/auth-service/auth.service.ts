import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';

const httpOption = {
  headers: new HttpHeaders({
    'Access-Control-Allow-Origin' : 'http://localhost:4200',
  })
}

@Injectable({
  providedIn: 'root'
})

export class AuthService {

  constructor(private cookieService: CookieService, private httpClient: HttpClient, private router:Router) { }

  isLoggedIn() {
    const token = this.cookieService.get('token'); // get token from local storage
    // console.log('Token', token);
    if(token) {
      const payload = atob(token.split('.')[1]); // decode payload of token
      const parsedPayload = JSON.parse(payload); // convert payload into an Object
      return parsedPayload.exp > Date.now() / 1000; // check if token is expired
    } else {
      return false;
    }
  }

  addToken(token: string) {
    this.cookieService.set('token', token);
  }

  logout() {
    this.cookieService.delete('token');
    this.router.navigate(['login']);
  }

  getToken() {
    // console.log('inside token method', this.cookieService.get('token'));
    return this.cookieService.get('token');
  }
}
