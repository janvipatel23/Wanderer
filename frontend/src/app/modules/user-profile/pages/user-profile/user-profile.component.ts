import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Validators } from '@angular/forms';
import { UserProfile } from 'src/app/data/schema/user-profile';
import { UserProfileService } from 'src/app/data/service/user-profile/user-profile.service';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss'],
})
export class UserProfileComponent implements OnInit {
  readonly title: string = 'User Profile';

  readonly editProfileButtonLabel: string = 'Edit Profile Details';

  modalDisplay: boolean = false;

  submitted: boolean = false;

  readonly DEFAULT_PROFILE_IMAGE = 'assets/images/login-logo.jpg';

  profileImage: any = this.DEFAULT_PROFILE_IMAGE;

  @ViewChild('imageUpload') imageUpload: any;
  fileLimit: number = 1;
  uploadedFilename: string = '';

  firstName: string = '';
  lastName: string = '';
  emailId: string = '';

  saveForm: FormGroup = this.fb.group({
    firstName: ['', [Validators.required]],
    lastName: ['', [Validators.required]],
    profileImage: [''],
  });

  constructor(
    private fb: FormBuilder,
    private userProfileService: UserProfileService,
    private sanitizer: DomSanitizer,
    private spinner: NgxSpinnerService,
    private toast: ToastrService
  ) {}

  ngOnInit(): void {
    this.spinner.show();
    this.userProfileService
      .getUserDetails()
      .pipe(
        finalize(() => {
          this.spinner.hide();
        })
      )
      .subscribe({
        next: (data: any) => {
          
          const userData = data.payload;
          this.firstName = userData.firstName;
          this.lastName = userData.lastName;
          this.emailId = userData.emailId;

          if (userData?.image) {
            const unsafeImageUrl = userData.image;
            this.saveForm.controls['profileImage'].patchValue(
              this.sanitizer.bypassSecurityTrustResourceUrl(unsafeImageUrl)
            );
          } else if (userData?.googlePhotoUrl) {
            this.saveForm.controls['profileImage'].patchValue(
              userData.googlePhotoUrl
            );
          } else {
            this.saveForm.controls['profileImage'].patchValue(
              this.DEFAULT_PROFILE_IMAGE
            );
          }
        },
        error: (err: any) => {
          console.log(err);
          const error = err.error;
          this.toast.error(error?.payload?.message);
        },
      });
  }

  get f() {
    return this.saveForm.controls;
  }

  showModal(): void {
    this.modalDisplay = true;
    this.saveForm.controls['firstName'].patchValue(this.firstName);
    this.saveForm.controls['lastName'].patchValue(this.lastName);
  }

  onSubmitClick() {
    this.submitted = true;
    if (this.saveForm.valid) {
      this.spinner.show();
      const userProfile: UserProfile = {
        firstName: this.saveForm.controls['firstName'].value,
        lastName: this.saveForm.controls['lastName'].value,
        profileImage: this.profileImage,
      };
      this.userProfileService
        .updateUserDetails(userProfile)
        .pipe(
          finalize(() => {
            this.imageUpload.clear();
            this.submitted = false;
            this.modalDisplay = false;
          })
        )
        .subscribe({
          next: (data: any) => {
            
            const updatedUser = data.payload;
            this.firstName = updatedUser.firstName;
            this.lastName = updatedUser.lastName;

            this.saveForm.controls['firstName'].patchValue(
              updatedUser.firstName
            );
            this.saveForm.controls['lastName'].patchValue(updatedUser.lastName);

            if (this.profileImage instanceof Blob) {
              const reader = new FileReader();
              reader.readAsDataURL(this.profileImage);
              reader.onloadend = () => {                
                this.saveForm.controls['profileImage'].patchValue(
                  reader.result
                );
                this.spinner.hide();
              };
            } else {
              this.spinner.hide();
            }
            this.toast.info('Profile updated successfully');
          },
          error: (err: any) => {
            console.log(err);
            const error = err.error;
            this.toast.error(error?.payload?.message);
            this.spinner.hide();
          },
        });
    }
  }

  myUploader(event: any) {
    if(event.files.length===0){
      return;
    }
    if (
      event.files[0].type !== 'image/jpeg' &&
      event.files[0].type !== 'image/png'
    ) {
      this.toast.error('Please upload a jpeg or png file');
      this.imageUpload.clear();
      return;
    }
    // if file size is greater than 1mb then show error
    if (event.files[0].size > 1000000) {
      this.toast.error('File size should be less than 1 MB');
      this.imageUpload.clear();
      return;
    }
    this.profileImage = event.files[0];
  }

  onProfileImageClear(event: any) {
    this.profileImage = '';
    this.uploadedFilename = '';
  }
}
