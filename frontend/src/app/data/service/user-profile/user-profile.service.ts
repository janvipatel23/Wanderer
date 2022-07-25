import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { UserProfile } from '../../schema/user-profile';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserProfileService {
  apiEndPoint: string = 'api/v1/wanderer/user/';

  constructor(private http: HttpClient) { }

  getUserId(): Observable<any> {
    return this.http.get(this.apiEndPoint + 'getUserId');
  }
  
  getUserDetails(): Observable<any> {
    return this.http.get(this.apiEndPoint + 'getDetails');
  }

  updateUserDetails(userProfile: UserProfile): Observable<any>{
    let formData: FormData = new FormData();
    formData.append('firstName', userProfile.firstName);
    formData.append('lastName', userProfile.lastName);
    formData.append('image',userProfile.profileImage);
    // formData.append('profileImage', userProfile.profileImage);

    return this.http.put(this.apiEndPoint + 'updateProfile', formData);
  }
}