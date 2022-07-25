import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Pin } from '../../schema/pin';
@Injectable({
  providedIn: 'root',
})
export class PinService {
  controllerEndPoint: string = 'api/v1/pin';
  pinRatingControllerEndPoint: string = 'api/v1/pinRating';
  bucketListControllerEndPoint: string = 'api/v1/bucketList';

  constructor(private http: HttpClient) {}

  insertPinWithImages(pin: Pin, images: any[]) {
    const formData: FormData = new FormData();
    formData.append('pin', new Blob([JSON.stringify(pin)], {
      type: 'application/json'
    }));
    for (const image of images) {
      formData.append('images', image);
    }
    return this.http.post(
      this.controllerEndPoint + '/createPin',
      formData
    );
  }

  getPinsByRadius(radius: number, centerLat: number, centerLng: number) {
    return this.http.get(
      this.controllerEndPoint + '/getPinsByRadius',
      {
        params: {
          radius,
          centerLat,
          centerLng,
        },
      }
    );
  }

  getPinsByIds(pinIds: number[]) {
    return this.http.get(
      this.controllerEndPoint + '/getPinsByIds',
      {
        params: {
          pinIds: pinIds.join(','),
        },
      }
    );
  }

  deletePin(pinId: number) {
    return this.http.delete(
      this.controllerEndPoint + '/deletePinById',
      {
        params: {
          pinId,
        },
      }
    );
  }

  getSinglePinById(pinId: number) {
    return this.http.get(
      this.controllerEndPoint + '/getPinById',
      {
        params: {
          pinId,
        },
      }
    );
  }

  addRating(pinId: number, rating: number) {
    // post pinId and rating to backend as params
    return this.http.post(
      this.pinRatingControllerEndPoint + '/addRating',
      null,
      {
        params: {
          pinId: pinId,
          pinRating: rating,
        },
      });
  }

  addComment(pinId: number, comment: string) {
    // send post request with comment as body and pinId as params
    return this.http.post(
      this.controllerEndPoint + '/addComments',
      comment,
      {
        params: {
          pinId: pinId,
        },       
      });
  }

  // add to bucket list using pinId as http params
  addToBucketList(pinId: number) {
    return this.http.post(
      this.bucketListControllerEndPoint + '/addPinToBucketList',
      null,
      {
        params: {
          pinId: pinId,
        },
      }
    );
  }

  // remove from bucket list using pinId as http params
  deleteFromBucketList(pinId: number) {
    return this.http.delete(
      this.bucketListControllerEndPoint + '/deletePinFromBucketList',
      {
        params: {
          pinId: pinId,
        },
      });
  }

  // get bucket list
  getBucketList() {
    return this.http.get(
      this.bucketListControllerEndPoint + '/listOfAllPins',
    );
  }

  // check if pin is in bucket list
  checkIfPinInBucketList(pinId: number) {
    return this.http.get(
      this.bucketListControllerEndPoint + '/checkIfPinInBucketList',
      {
        params: {
          pinId: pinId,
        },
      }
    );
  }
}
