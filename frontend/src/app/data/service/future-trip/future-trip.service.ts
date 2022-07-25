import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { FutureTripRequestDto } from '../../schema/future-trip-request-dto';

@Injectable({
  providedIn: 'root'
})
export class FutureTripService {
  apiEndPoint: string = 'api/v1/wanderer/futuretrip/';

  constructor(private http: HttpClient) { }

  // invoking call for getting all the user future trips
  getAllUserFutureTrips() : Observable<any>{
    return this.http.get(this.apiEndPoint + 'fetchFutureTripsByUserId');
  }

  // invoking call for allowing user to create the future trip
  createFutureTrip(futureTripRequestDto: FutureTripRequestDto) : Observable<any>{
    return this.http.post(this.apiEndPoint + 'createFutureTrip', futureTripRequestDto);
  }

  // invoking the call for allowing user to delete the future trip
  deleteFutureTrip(tripId: Number) : Observable<any>{
    return this.http.delete(this.apiEndPoint + 'deleteFutureTripById?futureTripId=' + tripId);
  }

  // invoking the call for allowing user to edit future trip
  editFutureTrip(tripId: Number, updatedFutureTrip: FutureTripRequestDto) : Observable<any> {
    return this.http.put(this.apiEndPoint + 'updateFutureTrip/' + tripId, updatedFutureTrip);
  }

  // invoking the call for allowing user to get all the future trips created for current pin
  getAllPinFutureTrips(pinId: Number) : Observable<any> {
    return this.http.get(this.apiEndPoint + 'fetchFutureTripsByPinId?pinId=' + pinId);
  }
}
