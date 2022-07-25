import { Component, OnInit } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { finalize } from 'rxjs';
import { BlogService } from 'src/app/data/service/blog-service/blog.service';

@Component({
  selector: 'app-user-blogs',
  templateUrl: './user-blogs.component.html',
  styleUrls: ['./user-blogs.component.scss'],
})
export class UserBlogsComponent implements OnInit {
  blogList: any[] = [];
  displayResponsive: boolean = false;
  selectedBlogId: number = 0;

  constructor(
    private blogService: BlogService,
    private sanitizer: DomSanitizer,
    private router: Router,
    private spinner: NgxSpinnerService,
    private toast: ToastrService
  ) {}

  ngOnInit(): void {
    // get blogs by user from blogService
    this.spinner.show();
    this.blogService
      .getBlogsByUser()
      .pipe(
        finalize(() => {
          this.spinner.hide();
        })
      )
      .subscribe({
        next: (data: any) => {
          let blogs: any = data?.payload;
          // convert blog image to base64
          blogs?.forEach((blog: any) => {
            if (blog?.blogImage) {
              blog.blogImage = this.sanitizer.bypassSecurityTrustResourceUrl(
                'data:image/png;base64, ' + blog.blogImage
              );
            } else {
              blog.blogImage = null;
            }
          });
          // check if blogs is not null and blogs is an array
          if (blogs && Array.isArray(blogs)) {
            this.blogList = blogs;
          } else {
            this.blogList = [];
          }
        },
        error: (err: any) => {
          console.log(err);
          this.toast.error('Error while getting blogs');
        },
      });
  }

  deleteBlog() {
    // delete blog by id
    this.spinner.show();
    this.blogService.deleteBlogById(this.selectedBlogId)
      .pipe(
        finalize(() => {
          this.spinner.hide();
        })
      )
      .subscribe({
        next: (data: any) => {
          this.toast.success('Blog deleted successfully');
          this.blogList = this.blogList.filter(
            (blog: any) => blog.blogId !== this.selectedBlogId
          );
          this.resetSelectedBlogId();
          this.displayResponsive = false;
        },
        error: (err: any) => {
          const error: any = err?.error;
          console.log(err);
          this.toast.error(error?.message || err?.message || 'Error while deleting blog');
        },
      });
  }

  navigateToBlog(blogId: string) {
    // navigate to blog editor
    this.router.navigate(['/blog/blog-editor', blogId]);
  }

  navigateToSingleBlog(blogId: string) {
    // navigate to blog editor
    this.router.navigate(['/blog/single-blog', blogId]);
  }

  showResponsiveDialog(blogId: number) {
    this.selectedBlogId = blogId;
    this.displayResponsive = true;
  }

  resetSelectedBlogId() {
    this.selectedBlogId = 0;
    this.displayResponsive = false;
  }
}
