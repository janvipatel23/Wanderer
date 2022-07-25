import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { finalize } from 'rxjs';
import { BlogService } from 'src/app/data/service/blog-service/blog.service';
import { UserProfileService } from 'src/app/data/service/user-profile/user-profile.service';

@Component({
  selector: 'app-blog-editor',
  templateUrl: './blog-editor.component.html',
  styleUrls: ['./blog-editor.component.scss'],
})
export class BlogEditorComponent implements OnInit {
  blogForm: FormGroup = this.fb.group({
    blogTitle: ['', [Validators.required, Validators.maxLength(255)]],
    blogDescription: ['', [Validators.required]],
  });
  blogFormSubmitted: boolean = false;

  @ViewChild('imageUpload') imageUpload: any;
  blogImage: any = null;
  uploadedFilename: string = '';
  savedBlogImage: any = null;

  currentBlogId: any;
  currentUserId: any;

  readonly defaultUserIdErrorMsg: string = 'Error getting user id';
  readonly unauthorizedErrorMsg: string = 'Unauthorized';

  constructor(
    private fb: FormBuilder,
    private spinner: NgxSpinnerService,
    private toast: ToastrService,
    private blogService: BlogService,
    private userProfileService: UserProfileService,
    private sanitizer: DomSanitizer,
    private activatedRoute: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    // get blog id from route using activated route observable
    this.activatedRoute.paramMap.subscribe((params: any) => {
      const blogId = params.get('blogId');
      if (blogId) {
        // get blog details
        this.spinner.show();
        // convert blogId to number
        this.currentBlogId = parseInt(blogId, 10);
        // check if blogId is a number
        if (isNaN(this.currentBlogId)) {
          this.toast.error('Invalid blog id');
          this.spinner.hide();
          return;
        }
        // get current user id
        this.checkBlogAccess();
      }
    });
  }

  // getter for blog form control
  get blogFormControls() {
    return this.blogForm.controls;
  }

  submitBlogForm() {
    this.blogFormSubmitted = true;
    if (this.blogForm.valid) {
      this.spinner.show();

      // if savedBlogImage is not null then convert it to Blob using fetch api
      if (this.savedBlogImage) {
        fetch(this.savedBlogImage?.changingThisBreaksApplicationSecurity)
          .then((res: any) => {
            return res.blob();
          })
          .then((blob: any) => {
            this.blogImage = blob;
          })
          .catch((err: any) => {
            console.log(err);
            this.toast.error('Error while converting base64 image to blob');
          })
          .finally(() => {
            this.saveBlog();
          });
      } else {
        this.saveBlog();
      }
    }
  }

  saveBlog() {
    const blogData: any = this.blogForm.value;
    // if currentBlogId is a not null and a number then add blogId to blogData
    if (this.currentBlogId && !isNaN(this.currentBlogId)) {
      blogData.blogId = this.currentBlogId;
    }
    // add blog using blog service
    this.blogService
      .addBlog(blogData, this.blogImage)
      .pipe(
        finalize(() => {
          this.spinner.hide();
          this.blogFormSubmitted = false;
        })
      )
      .subscribe({
        next: (data: any) => {
          // if currentBlogId is a not null and a number then show update toast
          if (this.currentBlogId && !isNaN(this.currentBlogId)) {
            this.toast.info('Blog updated successfully');
          } else {
            this.toast.success('Blog added successfully');
          }
          this.imageUpload.clear();
          this.blogImage = null;
          this.uploadedFilename = '';
          this.savedBlogImage = null;
          // check if blogImage is not null in payload
          if (data.payload.blogImage) {
            this.savedBlogImage = this.sanitizer.bypassSecurityTrustResourceUrl(
              'data:image/png;base64, ' + data?.payload?.blogImage
            );
          }
        },
        error: (err: any) => {
          console.log(err);
          const error = err?.error?.payload;
          this.toast.error(error?.message || err?.error?.message || err?.message || 'Error while adding blog');
        },
      });
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
    // if savedBlogImage is not null then show toast error
    if (this.savedBlogImage) {
      this.toast.error('You can only have one image for the blog');
      this.imageUpload.clear();
      return;
    }
    // if file size is greater than 1mb then show error
    if (event.files[0].size > 1000000) {
      this.imageUpload.clear();
      return;
    }
    // clear files if number of files is greater than 5
    if (event.files.length > 1) {
      this.toast.error('Please upload a maximum of 1 file');
      this.imageUpload.clear();
      return;
    }
    this.blogImage = event.files[0];
    this.uploadedFilename = event.files[0].name;
  }

  onBlogImageClear(event: any) {
    this.blogImage = null;
    this.uploadedFilename = '';
  }

  deleteSavedBlogImage() {
    this.savedBlogImage = null;
  }

  cancelBlogForm() {
    this.router.navigate(['/blog/user-blogs']);
  }

  checkBlogAccess() {
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
            this.blogService
              .getBlogById(this.currentBlogId)
              .pipe(
                finalize(() => {
                  this.spinner.hide();
                })
              )
              .subscribe({
                next: (data: any) => {
                  const blog: any = data?.payload;

                  if(blog?.user?.id!==this.currentUserId){
                    this.toast.error('You do not have access to edit this blog');
                    this.router.navigate(['/blog']);
                    return;
                  }
                  this.blogForm.patchValue({
                    blogTitle: blog?.blogTitle || '',
                    blogDescription: blog?.blogDescription || '',
                  });
                  // check if blogImage is not null in payload
                  if (blog.blogImage) {
                    this.savedBlogImage =
                      this.sanitizer.bypassSecurityTrustResourceUrl(
                        'data:image/png;base64, ' + blog.blogImage
                      );
                  }
                },
                error: (err: any) => {
                  console.log(err);
                  const error = err?.error?.payload;
                  this.toast.error(
                    error?.message || 
                    err?.error?.message ||
                    err?.message ||
                    'Unable to retrieve blog details'
                  );
                  this.router.navigate(['/blog/blog-editor']);
                },
              });
          } else {
            this.toast.error(this.defaultUserIdErrorMsg);
            this.toast.error('Unable to check blog access');
            this.router.navigate(['/blog']);
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
}
