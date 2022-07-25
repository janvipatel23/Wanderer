// import { ComponentFixture, TestBed } from '@angular/core/testing';
// import { SharedModule } from 'src/app/shared/shared.module';
// import { HttpClientTestingModule } from '@angular/common/http/testing';

// import { UserProfileComponent } from './user-profile.component';
// import { of, throwError } from 'rxjs';
// import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

// describe('UserProfileComponent', () => {
//   let component: UserProfileComponent;
//   let fixture: ComponentFixture<UserProfileComponent>;

//   beforeEach(async () => {
//     await TestBed.configureTestingModule({
//       imports: [
//         SharedModule,
//         HttpClientTestingModule,
//         BrowserAnimationsModule
//       ],
//       declarations: [UserProfileComponent],
//     }).compileComponents();
//   });

//   beforeEach(() => {
//     fixture = TestBed.createComponent(UserProfileComponent);
//     component = fixture.componentInstance;
//     fixture.detectChanges();
//   });

//   it('should create', () => {
//     expect(component).toBeTruthy();
//   });

//   it('should display modal', ()=>{
//     component.modalDisplay = false;

//     component.showModal();

//     expect(component.modalDisplay).toBeTrue;
//     expect(component.saveForm.controls['profileImage'].value).toBe('');
//     expect(component.uploadedFilename).toBe('');
//   });

//   it('should submit form',()=>{
//     const userProfileServiceSpy = jasmine.createSpyObj('UserProfileService', ['updateUserDetails']);    

//     userProfileServiceSpy.updateUserDetails.and.returnValue(of({success: true}));

//     userProfileServiceSpy.updateUserDetails().subscribe({
//       next: (data:any) =>{
//         expect(data.success).toBeTrue;
//       }
//     });

//     const userProfileServiceSpy2 = jasmine.createSpyObj('UserProfileService', ['updateUserDetails']);

//     userProfileServiceSpy2.updateUserDetails.and.returnValue(throwError(()=>{
//       return of({status: 500})
//     }));

//     userProfileServiceSpy2.updateUserDetails().subscribe({
//       error: (err: any)=>{
//         expect(err.status).not.toEqual(200);
//       }
//     });

//   });

//   it('should upload file to frontend',()=>{
//     const event: any = {
//       files: ['capture.png']
//     }

//     component.myUploader(event);

//     expect(component.saveForm.controls['profileImage'].value).toBe(event.files[0]);
//   });

//   it('should clear profile image from form', ()=>{
//     const event: any = {
//       files: ['capture.png']
//     }

//     component.saveForm.controls['profileImage'].setValue('capture.png');

//     component.onProfileImageClear(event);

//     expect(component.saveForm.controls['profileImage'].value).toBe('');

//     expect(component.uploadedFilename).toBe('');
//   });
// });
