import { HttpErrorResponse } from '@angular/common/http';
import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DomSanitizer } from '@angular/platform-browser';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { finalize } from 'rxjs';
import { pinIconUrl } from 'src/app/data/schema/pin-icon';
import { PinImage } from 'src/app/data/schema/pin-image';
import { PinService } from 'src/app/data/service/pin-service/pin.service';

@Component({
  selector: 'app-pin-form',
  templateUrl: './pin-form.component.html',
  styleUrls: ['./pin-form.component.scss'],
})
export class PinFormComponent implements OnInit, OnChanges {
  @Input() savedPins: any[] = [];
  @Output() savedPinsUpdate: EventEmitter<any> = new EventEmitter();

  @Input() newlyCreatedPins: any[] = [];
  @Output() newlyCreatedPinsUpdate: EventEmitter<any> = new EventEmitter();

  @Input() updatingPinsMap: any = {};
  @Output() updatingPinsMapUpdate: EventEmitter<any> = new EventEmitter();

  @Input() currentPin: any = {};
  @Input() currentPinIndex: number = -1;
  @Input() currentUserId: any;

  @Input() displayPinModal: boolean = false;
  @Output() displayPinModalUpdate: EventEmitter<boolean> = new EventEmitter();

  @Input() enablePinEditMode: boolean = false;
  @Output() enablePinEditModeUpdate: EventEmitter<boolean> = new EventEmitter();

  currentPinExtended: any;

  readonly pinIconUrl: any = pinIconUrl;

  enablePinModalSaveButton: boolean = true;

  pinForm: FormGroup = this.fb.group({
    locationName: ['', [Validators.required, Validators.maxLength(255)]],
    description: ['', [Validators.required, Validators.maxLength(1000)]],
    pinLatitude: ['', [Validators.required]],
    pinLongitude: ['', [Validators.required]],
  });
  @Input() submitted: boolean = false;

  ratingForm: FormGroup = this.fb.group({
    pinRating: [
      '',
      [Validators.required, Validators.min(1), Validators.max(5)],
    ],
  });
  ratingFormSubmitted: boolean = false;
  averagePinRating: number = 0;

  commentForm: FormGroup = this.fb.group({
    comment: ['', [Validators.required, Validators.maxLength(255)]],
  });
  commentFormSubmitted: boolean = false;

  responsiveOptions: any[] = [
    {
      breakpoint: '1024px',
      numVisible: 3,
      numScroll: 3,
    },
    {
      breakpoint: '768px',
      numVisible: 2,
      numScroll: 2,
    },
    {
      breakpoint: '560px',
      numVisible: 1,
      numScroll: 1,
    },
  ];
  
  @ViewChild('imageUpload') imageUpload: any;
  savedPinImages: PinImage[] = [];
  newPinImages: any[] = [];

  showBucketListButton: boolean = false;

  displayDeleteModal: boolean = false;

  constructor(
    private fb: FormBuilder,
    private pinService: PinService,
    private toast: ToastrService,
    private spinner: NgxSpinnerService,
    private sanitizer: DomSanitizer
  ) {}

  ngOnChanges(changes: SimpleChanges): void {

    if (changes['currentPin']?.currentValue) {
      this.newPinImages = [];
      this.savedPinImages = [];
      this.pinForm.reset();
      this.ratingForm.reset();
      this.averagePinRating = 0;
      this.showBucketListButton = false;
      this.displayDeleteModal = false;
      if (this.currentPin.isSaved && this.currentPin.pinId !== -1) {
        // show spinner
        this.spinner.show();

        // get single pin from pinservice using current pin id and update the pin form
        this.pinService
          .getSinglePinById(this.currentPin.pinId)          
          .subscribe({
            next: (data: any) => {
              this.currentPinExtended = data.payload;
              this.pinForm.patchValue({
                locationName: this.currentPinExtended.locationName,
                pinLatitude: this.savedPins[this.currentPinIndex].latitude,
                pinLongitude: this.savedPins[this.currentPinIndex].longitude,
                description: this.currentPinExtended.description,
              });              
              
              if(this.currentPinExtended?.pinImages?.length>0){                
                for(let i=0; i<this.currentPinExtended.pinImages.length; i++){                  
                  this.savedPinImages.push({
                    index: i,
                    imageUrl: this.sanitizer.bypassSecurityTrustResourceUrl('data:image/png;base64, '+ this.currentPinExtended.pinImages[i].image),
                  });
                }                
                this.savedPinImages = [...this.savedPinImages];                
                
              }

              // get average rating
              if (this.currentPinExtended?.pinRatings?.length > 0) {
                this.averagePinRating =
                  this.currentPinExtended.pinRatings.reduce(
                    (acc: any, curr: any) => {
                      if (curr.userId === this.currentUserId) {
                        this.ratingForm.patchValue({
                          pinRating: curr.rating,
                        });
                      }
                      return acc + curr.rating;
                    },
                    0
                  ) / this.currentPinExtended.pinRatings.length;                             
              }

              // check if pin is in bucket list using pinService
              this.pinService
              .checkIfPinInBucketList(this.currentPinExtended.pinId)
              .pipe(
                finalize(() => {
                  // hide spinner
                  this.spinner.hide();                
                })
              )
              .subscribe({
                next: (data: any) => {                  
                  if (data.payload) {
                    this.currentPinExtended.isInBucketList = true;
                  } else {
                    this.currentPinExtended.isInBucketList = false;
                  }
                  this.showBucketListButton = true;
                },
              });
            },
            error: (err: any) => {
              this.showBucketListButton = false;
              console.log(err);
              const error = err.error;
              this.toast.error(error?.payload?.message || error?.message);
            },
          });
      } else {
        this.pinForm.patchValue({
          locationName: this.currentPin.locationName,
          pinLatitude: this.currentPin.latitude,
          pinLongitude: this.currentPin.longitude,
        });
      }
    }
  }

  ngOnInit(): void {}

  // getter to access the pin form controls in template
  get pinFormControls() {
    return this.pinForm.controls;
  }

  get ratingFormControls() {
    return this.ratingForm.controls;
  }

  get commentFormControls() {
    return this.commentForm.controls;
  }

  getUserAccess(): boolean {
    return (
      !this.currentPin?.isSaved ||
      this.currentUserId == this.currentPin?.userId ||
      this.submitted
    );
  }

  // save or update pin to database
  savePinWithImages() {
    this.currentPin.latitude = this.pinForm.controls['pinLatitude'].value;
    this.currentPin.longitude = this.pinForm.controls['pinLongitude'].value;
    this.currentPin.locationName = this.pinForm.controls['locationName'].value;
    this.currentPin.description = this.pinForm.controls['description'].value;
    // validate latitude and longitude
    if (
      this.currentPin.latitude < -90 ||
      this.currentPin.latitude > 90 ||
      this.currentPin.longitude < -180 ||
      this.currentPin.longitude > 180
    ) {
      this.toast.error('Please enter valid latitude and longitude');
      return;
    }
    
    // if sum of length of savedPinImages and newPinImages is greater than 5 then show error
    if (this.savedPinImages.length + this.newPinImages.length > 5) {
      this.toast.error(
        'You can upload maximum 5 images for a pin. Please delete some images to upload new images.'
      );
      return;
    }
    this.submitted = true;
    this.enablePinModalSaveButton = false;
    if (this.pinForm.valid) {
      // show spinner
      this.spinner.show();

      if (this.savedPinImages.length > 0) {
        // convert each savedPinImage into blob using fetch api
        const blobPromises = this.savedPinImages.map((imageObj) => {
          const imageUrl:any = imageObj.imageUrl.changingThisBreaksApplicationSecurity;
          return fetch(imageUrl).then((response) => response.blob());
        });

        // wait for all blobs to be converted
        Promise.all(blobPromises).then((blobs) => {          
          this.insertPinWithImages(blobs);
        })
          .catch((err) => {
            console.log(err);
            this.toast.error(err);
            this.spinner.hide();
          });          
      } else {
        this.insertPinWithImages();
      }
    }
  }

  insertPinWithImages(blobs?: any) {
    this.savedPins[this.currentPinIndex].isSaved = true;

    let pin: any = {
      latitude: this.currentPin.latitude,
      longitude: this.currentPin.longitude,
      locationName: this.currentPin.locationName,
      description: this.currentPin.description,
    };

    // if Pin exists then update Pin
    if (this.currentPin.pinId !== -1) {
      pin.pinId = this.currentPin.pinId;
    }

    // concatenate newPinImages with savedPinImages
    const images = blobs ? this.newPinImages.concat(blobs) : this.newPinImages;

    // call service to insert pin
    this.pinService
      .insertPinWithImages(pin, images)
      .pipe(
        finalize(() => {          
          this.savedPinsUpdate.emit(this.savedPins);
          this.newlyCreatedPinsUpdate.emit(this.newlyCreatedPins);
          this.updatingPinsMapUpdate.emit(this.updatingPinsMap);
          this.pinFormCleanup();
          // hide spinner
          this.spinner.hide();
        })
      )
      .subscribe({
        next: (data: any) => {
          const newPin = data.payload;          
          newPin.isSaved = true;
          newPin.isDraggable = false;
          newPin.iconUrl = this.pinIconUrl.userCreatedPinIcon;
          if (this.currentPin.pinId !== -1) {
            const index = this.newlyCreatedPins.findIndex(
              (pin) => pin.pinId === newPin.pinId
            );
            if (index !== -1) {
              this.newlyCreatedPins[index] = newPin;
            }
            if (newPin?.pinId in this.updatingPinsMap) {
              delete this.updatingPinsMap[newPin.pinId];
            }
          } else {            
            this.newlyCreatedPins.push(newPin);
            this.savedPins[this.currentPinIndex] = newPin;
          }
          // get saved pin images and convert to base64 using sanitizer and push to savedPinImages
          if (newPin?.pinImages) {
            this.savedPinImages = [];
            newPin.pinImages.forEach((pinImage: any, index: any) => {
              const image = this.sanitizer.bypassSecurityTrustResourceUrl(
                'data:image/png;base64,' + pinImage.image
              );
              this.savedPinImages.push({
                imageUrl: image,
                index: index
              });
            });            
          }
          this.toast.success(data?.message);
        },
        error: (err: HttpErrorResponse) => {
          console.log(err);
          const error = err.error;
          this.toast.error(error?.payload?.message || error?.message);          
        },
      });
  }

  deletePinImage(index: number){
    // remove image from savedPinImages using for loop
    for (let i = 0; i < this.savedPinImages.length; i++) {
      if (this.savedPinImages[i].index === index) {
        this.savedPinImages.splice(i, 1);
        break;
      }
    }
  }

  deletePin() {
    this.submitted = true;
    this.spinner.show();
    this.pinService
      .deletePin(this.currentPin.pinId)
      .pipe(
        finalize(() => {
          this.displayPinModalUpdate.emit(false);
          this.submitted = false;
          this.enablePinModalSaveButton = true;

          this.pinForm.reset();
          // hide spinner
          this.spinner.hide();
        })
      )
      .subscribe({
        next: (data: any) => {          

          if (data?.message) {
            this.toast.warning(data.message);
          }
        },
        error: (err: any) => {
          console.log(err);
          const error = err.error;
          this.toast.error(error?.payload?.message || error?.message);
        },
      });
  }

  editPinCoordinates() {
    this.displayPinModalUpdate.emit(false);
    this.toast.info('Please move the selected Pin to change its coordinates');
    this.savedPins[this.currentPinIndex].isDraggable = true;
    this.savedPins[this.currentPinIndex].iconUrl =
      this.pinIconUrl.editablePinIcon;
    this.updatingPinsMap[this.currentPin.pinId] =
      this.savedPins[this.currentPinIndex];

    this.updatingPinsMapUpdate.emit(this.updatingPinsMap);
    this.savedPinsUpdate.emit(this.savedPins);
  }

  pinFormCleanup() {
    this.savedPinsUpdate.emit(this.savedPins);
    this.newlyCreatedPinsUpdate.emit(this.newlyCreatedPins);
    this.updatingPinsMapUpdate.emit(this.updatingPinsMap);

    this.displayPinModalUpdate.emit(false);
    this.submitted = false;
    this.enablePinModalSaveButton = true;

    this.pinForm.reset();
    this.imageUpload.clear();
    this.savedPinImages = [];
    this.newPinImages = [];
  }

  setPinEditMode(value: boolean) {
    this.enablePinEditMode = value;
    this.enablePinEditModeUpdate.emit(this.enablePinEditMode);
  }

  submitRatingForm() {
    if (this.ratingForm.valid) {      
      this.spinner.show();
      let pinId: any = this.currentPin.pinId;
      if (this.currentPin.pinId !== -1) {
        pinId = this.currentPin.pinId;
      } else {
        pinId = this.currentPinExtended.pinId;
      }
      // add pin rating
      this.pinService
        .addRating(pinId, this.ratingForm.controls['pinRating']?.value)
        .pipe(
          finalize(() => {
            // hide spinner
            this.spinner.hide();
          })
        )
        .subscribe({
          next: (data: any) => {            
            this.toast.info(data?.message);
            const newRatings: any = data?.payload;
            // get average rating from newRatings
            this.averagePinRating =
              newRatings?.reduce(
                (acc: any, curr: any) => acc + curr.rating,
                0
              ) / newRatings?.length;
          },
          error: (err: any) => {
            console.log(err);
            const error = err.error;
            this.toast.error(error?.payload?.message || error?.message);
          },
        });
    }
  }

  submitCommentForm() {
    this.commentFormSubmitted = true;
    if (this.commentForm.valid) {      
      let pinId: any = this.currentPin.pinId;
      if (this.currentPin.pinId !== -1) {
        pinId = this.currentPin.pinId;
      } else {
        pinId = this.currentPinExtended.pinId;
      }
      this.spinner.show();
      // add pin comment to database
      this.pinService
        .addComment(pinId, this.commentForm.controls['comment']?.value)
        .pipe(
          finalize(() => {
            // hide spinner
            this.spinner.hide();
          })
        )
        .subscribe({
          next: (data: any) => {            
            this.toast.success(data?.message);
            const newComment: any = data?.payload;
            this.currentPinExtended.pinComments.push(newComment);
            this.commentFormSubmitted = false;
            this.commentForm.reset();
          },
          error: (err: any) => {
            console.log(err);
            const error = err.error;
            this.toast.error(error?.payload?.message || error?.message);
          },
        });
    }
  }

  myUploader(event: any) {
    if (event.files.length === 0) {
      return;
    }
    // show error if file is not jpeg or png
    if (
      event.files[0].type !== 'image/jpeg' &&
      event.files[0].type !== 'image/png'
    ) {
      this.toast.error('Please upload a jpeg or png file');
      this.imageUpload.clear();
      return;
    }
    // clear files if number of files is greater than 5
    if (event.files.length > 5) {
      this.toast.error('You can only save upto 5 images per Pin');
      this.imageUpload.clear();
      return;
    }
    // if sum of length of savedPinImages and event.files is greater than 5 then show error
    if (this.savedPinImages.length + event.files.length > 5) {
      this.toast.error('You can only save upto 5 images per Pin');
      this.imageUpload.clear();
      return;
    }

    // check if file is less than 1mb using for loop
    for (let i = 0; i < event.files.length; i++) {
      if (event.files[i].size > 1000000) {
        this.toast.error('File size should be less than 1mb');
        this.imageUpload.clear();
        this.newPinImages = [];
        return;
      } else {
        this.newPinImages.push(event.files[i]);
      }
    }    
  }

  onPinImageClear(event: any) {    
    // find name of file in newPinImages and remove it
    const index = this.newPinImages.findIndex(
      (file: any) => file.name === event.file.name
    );
    if(index !== -1) {
      this.newPinImages.splice(index, 1);
    }
  }

  onAllPinImagesClear(event: any) {    
    this.newPinImages = [];
  }

  addToBucketList(){
    // add pin to bucket list using pinId
    this.spinner.show();
    let pinId: any = this.currentPinExtended?.pinId;
    this.pinService.addToBucketList(pinId).pipe(
      finalize(() => {
        // hide spinner
        this.spinner.hide();
      })
    ).subscribe({
      next: (data: any) => {
        this.currentPinExtended.isInBucketList = true;
        this.toast.success(data?.message);        
      },
      error: (err: any) => {
        console.log(err);
        const error = err.error;
        this.toast.error(error?.payload?.message || error?.message);
      },
    });
  }

  deleteFromBucketList(){
    // delete pin from bucket list using pinId
    this.spinner.show();
    let pinId: any = this.currentPinExtended?.pinId;
    this.pinService.deleteFromBucketList(pinId).pipe(
      finalize(() => {
        // hide spinner
        this.spinner.hide();
      })
    ).subscribe({
      next: (data: any) => {
        this.currentPinExtended.isInBucketList = false;
        this.toast.info(data?.message);        
      },
      error: (err: any) => {
        console.log(err);
        const error = err.error;
        this.toast.error(error?.payload?.message || error?.message);
      },
    });
  }  
}
