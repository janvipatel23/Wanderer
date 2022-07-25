import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { BlogRoutingModule } from './blog-routing.module';
import { BlogFeedComponent } from './pages/blog-feed/blog-feed.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { BlogEditorComponent } from './pages/blog-editor/blog-editor.component';
import { SingleBlogComponent } from './pages/single-blog/single-blog.component';
import { UserBlogsComponent } from './pages/user-blogs/user-blogs.component';



@NgModule({
  declarations: [
    BlogFeedComponent,
    BlogEditorComponent,
    SingleBlogComponent,
    UserBlogsComponent
  ],
  imports: [
    CommonModule,
    BlogRoutingModule,
    SharedModule
  ]
})
export class BlogModule { }
