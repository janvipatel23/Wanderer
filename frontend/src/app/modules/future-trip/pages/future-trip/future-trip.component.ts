import { formatDate } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { DomSanitizer } from '@angular/platform-browser';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { finalize } from 'rxjs';
import { FutureTrip } from 'src/app/data/schema/future-trip';
import { FutureTripRequestDto } from 'src/app/data/schema/future-trip-request-dto';
import { FutureTripService } from 'src/app/data/service/future-trip/future-trip.service';

@Component({
    selector: 'app-future-trip',
    templateUrl: './future-trip.component.html',
    styleUrls: ['./future-trip.component.scss']
})
export class FutureTripComponent implements OnInit {

    // array for holding guture trips
    trips: FutureTrip[] = [];
    displayBasic: boolean = false;
    // maintaining current date to now show the user to select past date for future trips
    currentDate = formatDate(new Date(), 'yyyy-MM-dd', 'en');

    // form group for the future trip edit form
    futureTripEditForm = new FormGroup({
        tripName: new FormControl(''),
        tripDescription: new FormControl(''),
        tripDate: new FormControl(''),
        locationName: new FormControl(''),
        tripId: new FormControl(''),
        pinId: new FormControl('')
    });

    constructor(private futureTripService: FutureTripService, private spinner: NgxSpinnerService, private toast: ToastrService, private santizer: DomSanitizer) { }

    ngOnInit() {
        // showing the spinner
        this.spinner.show();
        // getting the user future trips
        this.futureTripService.getAllUserFutureTrips().pipe(finalize(() => this.spinner.hide())).subscribe((response) => {
            this.trips = response.payload;
            this.trips.forEach(key => key.htmlDescription = this.santizer.bypassSecurityTrustHtml(key.tripDescription));
        });
    }

    // delete function for deleting future trips
    delete(tripId: Number) {
        this.spinner.show();
        this.futureTripService.deleteFutureTrip(tripId).pipe(finalize(() => this.spinner.hide())).subscribe((response) => {
            this.trips.forEach((trip, index) => {
                if (trip.tripId === tripId) {
                    this.trips.splice(index, 1);
                    this.toast.info(response?.message);
                }
            });
        }, (error) => this.toast.error(error?.message));
    }

    // edit function for editing future trips
    edit(trip: FutureTrip) {
        this.futureTripEditForm.get('tripName')?.setValue(trip.tripName);
        this.futureTripEditForm.get('tripDescription')?.setValue(trip.tripDescription);
        this.futureTripEditForm.get('tripDate')?.setValue(formatDate(trip.tripDate, 'yyyy-MM-dd', 'en','+0000'));
        this.futureTripEditForm.get('locationName')?.setValue(trip.pin?.locationName);
        this.futureTripEditForm.get('tripId')?.setValue(trip.tripId);
        this.futureTripEditForm.get('pinId')?.setValue(trip.pin?.pinId);
        this.futureTripEditForm.get('locationName')?.disable();
        this.displayBasic = true;
    }

    // saving the edited future trip
    save() {
        // showing the sinner
        this.spinner.show();
        // getting the trip id value
        var tripId = this.futureTripEditForm.get('tripId')?.value;
        // creating the updated future request dto
        var updateRequestDto: FutureTripRequestDto = {
            tripName: this.futureTripEditForm.get('tripName')?.value,
            tripDescription: this.futureTripEditForm.get('tripDescription')?.value,
            tripDate: this.futureTripEditForm.get('tripDate')?.value,
            pinId: this.futureTripEditForm.get('pinId')?.value
        }

        // invoking the edit future trip api call
        this.futureTripService.editFutureTrip(tripId, updateRequestDto).pipe(finalize(() => this.spinner.hide())).subscribe({
            next: (res) => {
                var trip = this.trips.find(t => t.tripId == tripId);
                if (trip) {
                    trip.tripName = res.payload?.tripName;
                    trip.tripDescription = res.payload?.tripDescription;
                    trip.tripDate = res.payload?.tripDate;
                    trip.htmlDescription = this.santizer.bypassSecurityTrustHtml(trip.tripDescription);
                }
                this.toast.success(res?.message)
                console.log(trip);
            },
            error: (error) => this.toast.error(error?.message),
            complete: () => this.displayBasic = false
        });
    }
}
