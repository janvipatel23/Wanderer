package ca.dal.cs.wanderer.services;

import ca.dal.cs.wanderer.exception.category.blogexception.*;
import ca.dal.cs.wanderer.models.Blog;
import ca.dal.cs.wanderer.models.BlogComment;
import ca.dal.cs.wanderer.models.User;
import ca.dal.cs.wanderer.repositories.BlogCommentRepository;
import ca.dal.cs.wanderer.repositories.BlogPostRepository;
import ca.dal.cs.wanderer.util.ErrorMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class BlogPostService {

    @Autowired
    BlogPostRepository blogPostRepository;

    @Autowired
    BlogCommentRepository blogCommentRepository;

    public Blog createBlog(User user, Blog blog, MultipartFile blogImage) {

        Integer blogId = blog.getBlogId();
        String blogTitle = blog.getBlogTitle();
        String blogDescription = blog.getBlogDescription();
        String blogAuthor = user.getFirstName() + " " + user.getLastName();

        Blog blogToUpdate;

        if(blogTitle == null || blogTitle.isEmpty()) {
            throw new BlogTitleIsEmpty(ErrorMessages.BLOG_TITLE_NOT_FOUND);
        }

        if(blogDescription == null || blogDescription.isEmpty()) {
            throw new BlogDescriptionIsEmpty(ErrorMessages.BLOG_DESCRIPTION_NOT_FOUND);
        }

        byte[] blogImageBytes = null;
        if(blogImage != null) {
            try {
                blogImageBytes = blogImage.getBytes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(blogId != null && blogId <=0) {
            throw new InvalidBlogId(ErrorMessages.INVALID_BLOG_ID);
        }

        if(blogId != null) {
            blogToUpdate = showSingleBlog(blogId);
            if(blogToUpdate == null) {
                throw new BlogNotFound(ErrorMessages.BLOG_NOT_FOUND);
            }
            // check if blog is owned by user
            if(!Objects.equals(blogToUpdate.getUser().getId(), user.getId())) {
                throw new UnauthorizedBlogAccess(ErrorMessages.UNAUTHORIZED_BLOG_ACCESS);
            }

            blogToUpdate.setBlogDescription(blogDescription);
            blogToUpdate.setBlogTitle(blogTitle);
            blogToUpdate.setBlogImage(blogImageBytes);
        } else {
            blogToUpdate = new Blog(blogDescription, blogTitle, blogAuthor, blogImageBytes, java.time.LocalDate.now(), user);
        }
        return blogPostRepository.save(blogToUpdate);
    }

    public List<Blog> showAllBlogs() {
        return blogPostRepository.findAll();
    }

    public Blog showSingleBlog(Integer blogId) {
        return blogPostRepository.findById(blogId).orElse(null);
    }

    public void deleteBlogPost(Integer blogId, User user) {
        Blog blogToDelete = blogPostRepository.findById(blogId).orElse(null);
        if(blogToDelete == null) {
            throw new BlogNotFound(ErrorMessages.BLOG_NOT_FOUND);
        }
        // check if blog is owned by user
        if(!Objects.equals(blogToDelete.getUser().getId(), user.getId())) {
            throw new UnauthorizedBlogAccess(ErrorMessages.UNAUTHORIZED_BLOG_ACCESS);
        }
        blogPostRepository.deleteById(blogId);
    }

    public List<BlogComment> getComments(Integer blogId) {
        return blogCommentRepository.findAllByBlogId(blogId);
    }

    public BlogComment addComment(User user, Blog blog, String comment) {
        BlogComment blogComment = new BlogComment(user, comment, java.time.LocalDate.now());
        blogComment.setBlog(blog);
        blog.addBlogComment(blogComment);
        blogPostRepository.save(blog);
        return blogComment;
    }

    // get Blogs by User
    public List<Blog> getBlogsByUser(User user) {
        return blogPostRepository.findAllByUserId(user.getId());
    }
}
