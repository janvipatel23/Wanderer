import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BlogEditorComponent } from './pages/blog-editor/blog-editor.component';
import { BlogFeedComponent } from './pages/blog-feed/blog-feed.component';
import { SingleBlogComponent } from './pages/single-blog/single-blog.component';
import { UserBlogsComponent } from './pages/user-blogs/user-blogs.component';

const routes: Routes = [
  {
    path: '',
    component: BlogFeedComponent,
  },
  {
    path: 'blog-editor/:blogId',
    component: BlogEditorComponent,
  },
  {
    path: 'blog-editor',
    component: BlogEditorComponent,
  },
  {
    path: 'single-blog/:blogId',
    component: SingleBlogComponent
  },
  {
    path: 'user-blogs',
    component: UserBlogsComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class BlogRoutingModule {}
