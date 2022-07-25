import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class BlogService {
  controllerEndPoint: string = 'api/v1/blogs';

  constructor(private http: HttpClient) { }

  // add blog data as json string in form data and image in form data
  addBlog(blogData: any, blogImage: any) {
    const formData: FormData = new FormData();
    // formData.append('blogData', JSON.stringify(blogData));
    // add blogData as Blob
    formData.append('blog', new Blob([JSON.stringify(blogData)], {
      type: 'application/json'
    }));
    // check if blogImage is not null
    if (blogImage) {
      formData.append('blogImage', blogImage);
    }
    return this.http.post(this.controllerEndPoint + '/addBlog', formData);
  }

  // get all blogs
  getBlogs() {
    return this.http.get(this.controllerEndPoint + '/getAllBlogs');
  }

  // get blog by id
  getBlogById(blogId: number) {
    // pass blogId as http params
    return this.http.get(this.controllerEndPoint + '/getBlogById', {
      params: {
        blogId
      }
    });
  }

  // get all blogs by user
  getBlogsByUser() {
    return this.http.get(this.controllerEndPoint + '/getBlogsByUser');
  }

  // delete blog by id
  deleteBlogById(blogId: number) {
    // pass blogId as http params
    return this.http.delete(this.controllerEndPoint + '/deleteBlogById', {
      params: {
        blogId: blogId
      }
    });
  }

  // add comment to blog
  addComment(blogId: number, comment: string) {
    return this.http.post(
      this.controllerEndPoint + '/addComments',
      comment,
      {
        params: {
          blogId: blogId,
        },       
      });
    }
}
