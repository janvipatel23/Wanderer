package ca.dal.cs.wanderer.controllers;

import ca.dal.cs.wanderer.exception.GenericResponse;
import ca.dal.cs.wanderer.exception.category.EmailNotFound;
import ca.dal.cs.wanderer.exception.category.PrincipalNotFound;
import ca.dal.cs.wanderer.exception.category.blogexception.BlogNotFound;
import ca.dal.cs.wanderer.exception.category.blogexception.InvalidBlogId;
import ca.dal.cs.wanderer.exception.category.pinexception.CommentNotFound;
import ca.dal.cs.wanderer.models.Blog;
import ca.dal.cs.wanderer.models.BlogComment;
import ca.dal.cs.wanderer.models.User;
import ca.dal.cs.wanderer.services.BlogPostService;
import ca.dal.cs.wanderer.services.UserProfileService;
import ca.dal.cs.wanderer.util.ErrorMessages;
import ca.dal.cs.wanderer.util.SuccessMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/blogs")
public class BlogPostController {

    @Autowired
    BlogPostService blogPostService;

    @Autowired
    UserProfileService userProfileService;

    // accept a blog post and blogImage as a multipart file
    @PostMapping("/addBlog")
    public ResponseEntity<GenericResponse<Blog>> createBlogs(@AuthenticationPrincipal OidcUser principal, @RequestPart("blog") Blog blog, @RequestPart(value = "blogImage", required = false) MultipartFile blogImage) {

        User user = getUser(principal);

        if(blog == null) {
            throw new BlogNotFound(ErrorMessages.BLOG_NOT_FOUND);
        }

        Blog newBlog = blogPostService.createBlog(user, blog, blogImage);
        return ResponseEntity.ok(new GenericResponse<>(true, SuccessMessages.BLOG_CREATE_SUCCESS.getSuccessMessage(), newBlog));
    }

    //Get all blogs in the database
    @GetMapping("/getAllBlogs")
    public ResponseEntity<GenericResponse<List<Blog>>> showAllBlogs(@AuthenticationPrincipal OidcUser principal) {
        getUser(principal);

        List<Blog> blogs = blogPostService.showAllBlogs();
        return ResponseEntity.ok(new GenericResponse<>(true, SuccessMessages.ALL_BLOG_RETRIEVE_SUCCESS.getSuccessMessage(), blogs));
    }

    //Get blog by blogId in the database
    @GetMapping("/getBlogById")
    public ResponseEntity<GenericResponse<Blog>> showSingleBlog(@AuthenticationPrincipal OidcUser principal, @RequestParam Integer blogId) {
        getUser(principal);

        if(blogId <= 0) {
            throw new InvalidBlogId(ErrorMessages.INVALID_BLOG_ID);
        }
        Blog blog = blogPostService.showSingleBlog(blogId);

        if(blog == null) {
            throw new BlogNotFound(ErrorMessages.BLOG_NOT_FOUND);
        }
        return ResponseEntity.ok(new GenericResponse<>(true, SuccessMessages.BLOG_RETRIEVE_SUCCESS.getSuccessMessage(), blog));
    }

    //Delete blog by blogId in the database
    @DeleteMapping("/deleteBlogById")
    public ResponseEntity<GenericResponse<Boolean>> deleteBlogById(@AuthenticationPrincipal OidcUser principal, @RequestParam Integer blogId) {
        User user = getUser(principal);

        if (blogId == null || blogId <= 0) {
            throw new InvalidBlogId(ErrorMessages.INVALID_BLOG_ID);
        }

        blogPostService.deleteBlogPost(blogId, user);

        return ResponseEntity.ok(new GenericResponse<>(true, SuccessMessages.BLOG_DELETE_SUCCESS.getSuccessMessage(), true));
    }

    //Get all comments for a particular blog
    @GetMapping("/getComments")
    public ResponseEntity<GenericResponse<List<BlogComment>>> getComments(@AuthenticationPrincipal OidcUser principal, @RequestParam Integer blogId) {
        getUser(principal);

        if (blogId <= 0) {
            throw new InvalidBlogId(ErrorMessages.INVALID_BLOG_ID);
        }
        List<BlogComment> comments = blogPostService.getComments(blogId);

        return ResponseEntity.ok(new GenericResponse<>(true, SuccessMessages.COMMENTS_RETRIEVE_SUCCESS.getSuccessMessage(), comments));
    }

    //Add comment for a particular blog
    @PostMapping("/addComments")
    public ResponseEntity<GenericResponse<BlogComment>> addComments(@AuthenticationPrincipal OidcUser principal, @RequestParam Integer blogId,
                                                                   @RequestBody String comment) {

        User user = getUser(principal);

        if (blogId <= 0) {
            throw new InvalidBlogId(ErrorMessages.INVALID_BLOG_ID);
        }

        Blog blog = blogPostService.showSingleBlog(blogId);
        if (blog == null) {
            throw new BlogNotFound(ErrorMessages.BLOG_NOT_FOUND);
        }

        if(comment == null) {
            throw new CommentNotFound(ErrorMessages.COMMENT_NOT_FOUND);
        }

        BlogComment blogComment = blogPostService.addComment(user, blog, comment);
        return ResponseEntity.ok(new GenericResponse<>(true, SuccessMessages.COMMENT_ADD_SUCCESS.getSuccessMessage(), blogComment));
    }

    //Get all blogs that belongs to user
    @GetMapping("/getBlogsByUser")
    public ResponseEntity<GenericResponse<List<Blog>>> getBlogByUser(@AuthenticationPrincipal OidcUser principal) {

        User user = getUser(principal);

        List<Blog> blogs = blogPostService.getBlogsByUser(user);

        return ResponseEntity.ok(new GenericResponse<>(true, SuccessMessages.BLOG_RETRIEVE_SUCCESS.getSuccessMessage(), blogs));
    }

    private User getUser(OidcUser principal) {
        if (principal == null) {
            throw new PrincipalNotFound(ErrorMessages.PRINCIPAL_NOT_FOUND);
        }
        String email = principal.getEmail();
        if (email == null) {
            throw new EmailNotFound(ErrorMessages.EMAIL_NOT_FOUND);
        }

        User user = userProfileService.fetchByEmail(email);
        if(user==null) {
            throw new PrincipalNotFound(ErrorMessages.PRINCIPAL_NOT_FOUND);
        }
        return user;
    }
}
