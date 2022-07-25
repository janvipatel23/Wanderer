import { Component, OnInit } from '@angular/core';
import { Firestore, collectionData, collection } from '@angular/fire/firestore';
import { debounceTime, finalize, Observable, Subject, map } from 'rxjs';
import { MouseEvent } from '@agm/core';
import { PinService } from 'src/app/data/service/pin-service/pin.service';
import { Pin } from 'src/app/data/schema/pin';
import { HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { NgxSpinnerService } from 'ngx-spinner';
import { FutureTripService } from 'src/app/data/service/future-trip/future-trip.service';
import { FutureTripRequestDto } from 'src/app/data/schema/future-trip-request-dto';
import { formatDate } from '@angular/common';
import { FutureTrip } from 'src/app/data/schema/future-trip';
import { DomSanitizer } from '@angular/platform-browser';
import { pinIconUrl } from 'src/app/data/schema/pin-icon';
import { UserProfileService } from 'src/app/data/service/user-profile/user-profile.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
  providers: [],
})
export class HomeComponent implements OnInit {
  //Firestore Data
  pinCreationUpdates!: Observable<any>;
  readonly PIN_UPDATE_COLLECTION: string = 'pin_updates';

  // forn group for future trip edit form
  futureTripEditForm = new FormGroup({
    tripName: new FormControl(''),
    tripDescription: new FormControl(''),
    tripDate: new FormControl(''),
    locationName: new FormControl(''),
    tripId: new FormControl(''),
    pinId: new FormControl('')
});

// getting the current date to allow the user to set the future trip for the past date
currentDate = formatDate(new Date(), 'yyyy-MM-dd','en');
// holding current pin future trips
currentPinFutureTrips: FutureTrip[] = [];

  // Pin Center and Radius Subjects for 2-way communication between Map UI and Keyboard input
  centerChangeSubject: Subject<any> = new Subject();
  radiusChangeSubject: Subject<any> = new Subject();

  // google maps default zoom level
  zoom: number = 8;

  // default radius (in meters)
  radius: number = 50000;

  // initial/default center position for the map
  readonly DEFAULT_LAT: number = 21.7645;
  readonly DEFAULT_LNG: number = 72.1519;

  latitude: number = this.DEFAULT_LAT;
  longitude: number = this.DEFAULT_LNG;

  // Saved Pins
  savedPins: Pin[] = [];

  // Pins created by User
  newlyCreatedPins: Pin[] = [];

  // currently updating pins
  updatingPinsMap: any = {};

  // Pin Modal Data
  readonly mapPanelHeader: string = 'Map Explorer';
  readonly pinModalHeaderLabel: string = 'Add/Edit Pin Details';
  displayPinModal: boolean = false;
  enablePinModalSaveButton: boolean = true;

  currentPin!: Pin;
  currentPinIndex!: number;
  
  submitted: boolean = false;
  enablePinEditMode: boolean = false;

  readonly pinIconUrl: any = pinIconUrl;

  currentUserId!: any;  

  readonly defaultUserIdErrorMsg: string = 'Error getting user id';
  readonly unauthorizedErrorMsg: string = 'Unauthorized';
  readonly userIdPropName: string = 'userId';
  readonly rootSpinnerName: string = 'root-spinner';

  showInfoWindow: boolean = false;

  constructor(
    private firestore: Firestore,
    private pinService: PinService,
    private fb: FormBuilder,
    private toast: ToastrService,
    private spinner: NgxSpinnerService,
    private futureTripService: FutureTripService,
    private sanitizer : DomSanitizer,
    private userProfileService: UserProfileService,
  ) {}

  ngOnInit(): void {
    // get user id
    this.getUserId();

    // get user's current location
    this.setCurrentLocation();

    // firestore setup
    const dataList = collection(this.firestore, this.PIN_UPDATE_COLLECTION);
    this.pinCreationUpdates = collectionData(dataList);

    // subscribe to pin creation updates from firestore
    this.pinCreationUpdates.pipe(debounceTime(1000)).subscribe((update) => {
      console.log(update);
      this.getPinsByRadiusFromService();
    });

    // listen to center change events
    this.centerChangeSubject.pipe(debounceTime(0)).subscribe((data: any) => {      

      this.latitude = data.lat;
      this.longitude = data.lng;
      this.getPinsByRadiusFromService();
    });

    // listen to radius change events
    this.radiusChangeSubject
      .pipe(debounceTime(1000))
      .subscribe((newRadius: any) => {

        this.radius = parseInt(newRadius.toString());

        this.getPinsByRadiusFromService();
      });
  }

  getUserId(){
    this.spinner.show();
    // get user id from user service
    this.userProfileService
      .getUserId()
      .pipe(
        finalize(() => {
          this.spinner.hide();
        })
      )
      .subscribe({
        next: (data: any) => {
          if (data && data?.payload?.userId) {
            this.currentUserId = data.payload.userId;
          } else {
            this.toast.error(this.defaultUserIdErrorMsg);
          }
        },
        error: (err: HttpErrorResponse) => {
          console.log(err);
          this.toast.error(
            this.getUserErrorMsg(err, this.defaultUserIdErrorMsg)
          );
        },
      });
  }

  getUserErrorMsg(err: HttpErrorResponse, defaultMsg: string): string {
    if (err.status === 401) {
      return this.unauthorizedErrorMsg;
    } else {
      const error = err.error;
      return (
        error?.payload?.message || error?.message || err?.message || defaultMsg
      );
    }
  }

  // render pin on map
  createPin(event: MouseEvent) {    

    // remove last rendered pin from map
    if (
      this.savedPins.length > 0 &&
      !this.savedPins[this.savedPins.length - 1].isSaved
    ) {
      this.savedPins.splice(this.savedPins.length - 1, 1);
    }

    // render new pin on map
    this.savedPins.push({
      latitude: event.coords.lat,
      longitude: event.coords.lng,
      locationName: '',
      isSaved: false,
      isDraggable: true,
      pinId: -1,
      iconUrl: this.pinIconUrl.temporaryPinIcon,
    });
  }

  openPinModal(pin: Pin, index: number) {
    this.setCurrentPin(pin, index);
    this.setFutureTripForm(pin);
    this.displayPinModal = true;
  }

  closePinModal(event:any) {
    
    this.enablePinEditMode = false;
    this.displayPinModal = false;
    this.submitted = false;
  }

  // set double clicked pin as current pin to set values in pin modal form
  setCurrentPin(pin: Pin, index: number) {
    this.currentPin = Object.assign({}, pin);       
    this.currentPinIndex = index;
  }

  // create the future trip for the requested pin
  setFutureTripForm(pin: Pin) {
    this.futureTripEditForm.reset();
    if(pin.locationName) {
      this.futureTripEditForm.get('locationName')?.setValue(pin.locationName);
      this.futureTripEditForm.get('pinId')?.setValue(pin.pinId);
      this.futureTripEditForm.get('locationName')?.disable();
    }
  }

  // getting the all future trips created for the current pin
  setFutureTripsForCurrentPin(pin: Pin) {
    this.futureTripService.getAllPinFutureTrips(pin.pinId).subscribe(response => {
      this.currentPinFutureTrips = response.payload;
      this.currentPinFutureTrips.forEach(key => key.htmlDescription = this.sanitizer.bypassSecurityTrustHtml(key.tripDescription));
      this.spinner.hide();
    })
  }

  // handling the change while getting the future trip created for the current pin
  handleChange(event: any) {
    var index = event.index;
    if(index == 2) {
      // showing the spinner
      this.spinner.show();
      // invoking the future trips call for current pin
      // to fetch the data when user navigate to other's future trip tab
      this.setFutureTripsForCurrentPin(this.currentPin);
    }
  }

  getPinsByRadiusFromService() {
    if (this.radius < 0) {
      this.radius = Math.abs(this.radius);
    }
    // validate latitude and longitude
    if (
      this.latitude < -90.0 ||
      this.latitude > 90.0 ||
      this.longitude < -180.0 ||
      this.longitude > 180.0
    ) {
      this.latitude = this.DEFAULT_LAT;
      this.longitude = this.DEFAULT_LNG;
      this.toast.error('Invalid latitude or longitude');
    }
    const meterToKmUnit = 1000;
    const radiusInKms: number = this.radius / meterToKmUnit;
    this.pinService
      .getPinsByRadius(radiusInKms, this.latitude, this.longitude)
      .pipe(
        map((data: any) => {
          return this.performPinPostProcessing(data);
        })
      )
      .subscribe({
        next: (pinsByRadiusData: any) => {          

          // refresh data of newly created pins
          if (this.newlyCreatedPins.length > 0) {
            // get pinIds from newlyCreatedPins array and pass to getPinsByIds service
            const pinIds = this.newlyCreatedPins.map((pin) => pin.pinId);

            this.pinService
              .getPinsByIds(pinIds)
              .pipe(
                map((data: any) => {
                  return this.performPinPostProcessing(data);
                }),
                finalize(() => {
                  this.mergePins(pinsByRadiusData);
                })
              )
              .subscribe({
                next: (data: any) => {
                  this.newlyCreatedPins = data.payload;
                },
                error: (err: HttpErrorResponse) => {
                  console.log(err);
                  const error = err.error;
                  this.toast.error(
                    error?.payload?.message ||
                      error?.message ||
                      'Unable to retrieve pins'
                  );
                },
              });
          } else {
            this.mergePins(pinsByRadiusData);
          }
        },
        error: (err: HttpErrorResponse) => {
          console.log(err);
          const error = err.error;
          this.toast.clear();
          this.toast.error(
            error?.payload?.message ||
              error?.message ||
              'Unable to retrieve pins'
          );
        },
      });
  }

  performPinPostProcessing(data: any) {
    if (data.isSuccessful) {
      const processedPayload = data.payload.map((pin: any) => {
        pin.isSaved = true;
        pin.isDraggable = false;
        pin.iconUrl = this.pinIconUrl.savedPinIcon;
        return pin;
      });
      data.payload = processedPayload;
    }
    return data;
  }

  mergePins(pinsByRadiusData: any) {
    const savedPinsMap: any = {};
    const concatPins: any[] = pinsByRadiusData?.payload.concat(this.newlyCreatedPins);
    for (let pin of concatPins) {
      
      if(pin.userId === this.currentUserId) {
        pin.iconUrl = this.pinIconUrl.userCreatedPinIcon;
      }
      if (pin.pinId in this.updatingPinsMap) {        
        pin = this.updatingPinsMap[pin.pinId];
        pin.iconUrl = this.pinIconUrl.editablePinIcon;
      }      
      if (!(pin.pinId in savedPinsMap)) {        
        savedPinsMap[pin.pinId] = pin;  
        savedPinsMap[pin.pinId].iconUrl = pin.iconUrl;        
      }      
    }

    this.savedPins = Object.values(savedPinsMap);
   
  }

  pinDragEnd(m: Pin, index: number, event: MouseEvent) {
    m.latitude = event.coords.lat;
    m.longitude = event.coords.lng;
    this.savedPins[index] = m;
    
    this.savedPins = [...this.savedPins];
    if(m.pinId in this.updatingPinsMap) {
      this.updatingPinsMap[m.pinId] = m;
      this.updatingPinsMap = {...this.updatingPinsMap};      
    }
  }

  identifyPin(index: number, pin: Pin) {
    return pin.pinId;
  }

  centerChange(event: any) {
    this.centerChangeSubject.next(event);
  }

  shiftCenter(event: any) {
    this.centerChangeSubject.next(event.coords);
  }

  radiusChange(event: any) {
    this.radiusChangeSubject.next(event);
  }

  convertKilometerToMeter(valueInKilometers: number) {
    const unit = 1000.0;
    this.radius = valueInKilometers * unit;
  }

  setCurrentLocation() {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition((position) => {
        this.latitude = position.coords.latitude;
        this.longitude = position.coords.longitude;
        this.toast.success('Location Access Granted');
      }, this.showError.bind(this));
    }
  }

  showError(error: any) {
    console.log(error);

    switch (error.code) {
      case error.PERMISSION_DENIED:
        this.toast.warning('User denied the request for Geolocation');
        break;
      case error.POSITION_UNAVAILABLE:
        this.toast.warning('Location information is unavailable');
        break;
      case error.TIMEOUT:
        this.toast.warning('Location information is unavailable');
        break;
      case error.UNKNOWN_ERROR:
        this.toast.warning('An unknown error occurred');
        break;
    }
  }

  getUserAccess(): boolean {
    return !this.currentPin?.isSaved || this.currentUserId == this.currentPin?.userId || this.submitted
  }

  getUpdatedPinsMap(event: any) {    
    this.updatingPinsMap = event;      
  }

  // saving the created future trip for the user
  saveFutureTrip() {
    // showing the spinner
    this.spinner.show();

    // creating the request dto to call the requested api
    var createdRequestDto:FutureTripRequestDto = {
        tripName: this.futureTripEditForm.get('tripName')?.value,
        tripDescription: this.futureTripEditForm.get('tripDescription')?.value,
        tripDate: this.futureTripEditForm.get('tripDate')?.value,
        pinId: this.futureTripEditForm.get('pinId')?.value,
    }

    // invoking the create future trip api
    this.futureTripService.createFutureTrip(createdRequestDto).pipe(finalize(() => this.spinner.hide())).subscribe({
      next: (res) => this.toast.success(res?.message),
      error: (error) => this.toast.error(error?.message) 
    });
  }

  displayInfoWindow(){
    this.showInfoWindow=true;
  }
}
