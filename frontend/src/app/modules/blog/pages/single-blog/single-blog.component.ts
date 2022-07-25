import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { finalize } from 'rxjs';
import { BlogService } from 'src/app/data/service/blog-service/blog.service';

@Component({
  selector: 'app-single-blog',
  templateUrl: './single-blog.component.html',
  styleUrls: ['./single-blog.component.scss'],
})
export class SingleBlogComponent implements OnInit {
  currentBlogId: any;
  savedBlogImage: any;
  blogTitle: string = '';
  blogDescription: any = '';

  commentForm: FormGroup = this.fb.group({
    comment: ['', [Validators.required, Validators.maxLength(255)]],
  });
  commentFormSubmitted: boolean = false;
  commentsList: any[] = [];

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private spinner: NgxSpinnerService,
    private toast: ToastrService,
    private blogService: BlogService,
    private sanitizer: DomSanitizer,
    private fb: FormBuilder,
  ) {}

  ngOnInit(): void {
    // get key blogId from url
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
                  // check if blogImage is not null in payload
                  if (blog?.blogImage) {
                    this.savedBlogImage =
                      this.sanitizer.bypassSecurityTrustResourceUrl(
                        'data:image/png;base64, ' + blog.blogImage
                      );
                  }
                  this.blogTitle = blog?.blogTitle || '';
                  // this.blogDescription = blog?.blogDescription || '';
                  // convert blogDescription to html using sanitizer
                  this.blogDescription = this.sanitizer.bypassSecurityTrustHtml(blog?.blogDescription || '');                
                  if(blog?.blogComments){
                    this.commentsList = blog.blogComments;
                  }
                },
                error: (err: any) => {
                  console.log(err);
                  const error = err?.error?.payload;
                  this.toast.error(
                    error?.message ||
                      err?.message ||
                      'Unable to retrieve blog details'
                  );
                  this.router.navigate(['/blog']);
                },
              });
      }
    });
  }

  get commentFormControls() {
    return this.commentForm.controls;
  }

  submitCommentForm() {
    this.commentFormSubmitted = true;
    if (this.commentForm.valid) {
      this.spinner.show();
      // add pin comment to database
      this.blogService
        .addComment(this.currentBlogId, this.commentForm.controls['comment']?.value)
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
            this.commentsList.push(newComment);
            this.commentFormSubmitted = false;
            this.commentForm.reset();
          },
          error: (err: any) => {
            console.log(err);
            const error = err.error;
            this.toast.error(error?.payload?.message || error?.message || 'Unable to add comment');
          },
        });
    }
  }
}
