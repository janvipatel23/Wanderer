import { Component, OnInit } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';
import { ToastrService } from 'ngx-toastr';
import { finalize } from 'rxjs';
import { BlogService } from 'src/app/data/service/blog-service/blog.service';

@Component({
  selector: 'app-blog-feed',
  templateUrl: './blog-feed.component.html',
  styleUrls: ['./blog-feed.component.scss']
})
export class BlogFeedComponent implements OnInit {

  blogList : any = [];
  constructor(private blogService:BlogService,
    private spinner: NgxSpinnerService,
    private toast: ToastrService,
    private sanitizer: DomSanitizer,
    private router: Router
    ) { }

  ngOnInit(): void {
    // get all blogs
    this.spinner.show();
    this.blogService.getBlogs()
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
          if(blog?.blogImage) {
            blog.blogImage = this.sanitizer.bypassSecurityTrustResourceUrl('data:image/png;base64, ' + blog.blogImage);
          } else {
            blog.blogImage = null;
          }
        });
        if(blogs) {                    
          this.blogList = blogs;          
        } else {
          this.blogList = [];
        }
      },
      error: (err: any) => {
        console.log(err);
        this.toast.error('Error while getting blogs');
      }
    });
  }

  navigateToBlog(blogId: string) {
    // navigate to blog editor
    this.router.navigate(['/blog/single-blog', blogId]);
  }
}
