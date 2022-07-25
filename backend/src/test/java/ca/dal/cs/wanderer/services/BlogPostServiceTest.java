package ca.dal.cs.wanderer.services;

import ca.dal.cs.wanderer.exception.category.blogexception.BlogDescriptionIsEmpty;
import ca.dal.cs.wanderer.exception.category.blogexception.BlogTitleIsEmpty;
import ca.dal.cs.wanderer.exception.category.blogexception.UnauthorizedBlogAccess;
import ca.dal.cs.wanderer.models.Blog;
import ca.dal.cs.wanderer.models.BlogComment;
import ca.dal.cs.wanderer.models.User;
import ca.dal.cs.wanderer.repositories.BlogCommentRepository;
import ca.dal.cs.wanderer.repositories.BlogPostRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

class BlogPostServiceTest {

    @Mock
    private BlogPostRepository blogPostRepository;

    @Mock
    private BlogCommentRepository blogCommentRepository;

    @InjectMocks
    private BlogPostService blogPostService;

    private User user;
    private Blog blog1;
    private Blog blog2;
    private BlogComment blogComment;
    private String comment1 = "Test comment";
    private String comment2 = "Test2 comment";
    List<Blog> list = new ArrayList<>();
    List<BlogComment> commentList = new ArrayList<>();

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        user = getUser();
        blog1 = getBlog1(user);
        blog2 = getBlog2(user);

        list.add(blog1);
        list.add(blog2);

        blogComment = getBlogComment();
        commentList.add(blogComment);
    }

    private User getUser() {
        User user = new User();
        user.setFirstName("Test First name");
        user.setLastName("Test last name");
        user.setId(1);
        user.setEmailId("test@gmail.com");
        return user;
    }

    private Blog getBlog(User user) {
        Blog blog = new Blog();
        blog.setBlogTitle("Test Title");
        blog.setBlogAuthor(user.getFirstName() + " " + user.getLastName());
        blog.setBlogDescription("Test Description");
        blog.setUser(user);
        return blog;
    }

    private Blog getBlog1(User user) {
        Blog blog = new Blog();
        blog.setBlogId(1);
        blog.setBlogTitle("Test Title");
        blog.setBlogAuthor(user.getFirstName() + " " + user.getLastName());
        blog.setBlogDescription("Test Description");
        blog.setUser(user);
        return blog;
    }

    private Blog getBlog2(User user) {
        Blog blog = new Blog();
        blog.setBlogId(2);
        blog.setBlogTitle("Test Title2");
        blog.setBlogAuthor(user.getFirstName() + " " + user.getLastName());
        blog.setBlogDescription("Test Description2");
        blog.setUser(user);
        return blog;
    }

    private BlogComment getBlogComment() {
        BlogComment blogComment = new BlogComment();
        blogComment.setComment(comment1);
        return blogComment;
    }

    @Test
    void createBlogWithNoTitle() {
        Blog blog = new Blog();
        blog.setBlogAuthor(user.getFirstName() + " " + user.getLastName());
        blog.setBlogDescription("Test Description");
        blog.setUser(user);
        Assertions.assertThrows(BlogTitleIsEmpty.class, () -> blogPostService.createBlog(user, blog, null));
    }

    @Test
    void createBlogWithNoDescription() {
        Blog blog = new Blog();
        blog.setBlogTitle("Test Title2");
        blog.setBlogAuthor(user.getFirstName() + " " + user.getLastName());
        blog.setUser(user);
        Assertions.assertThrows(BlogDescriptionIsEmpty.class, () -> blogPostService.createBlog(user, blog, null));
    }

    @Test
    void showAllBlogsTest() {

        when(blogPostRepository.findAll()).thenReturn(list);

        List<Blog> blogList = blogPostService.showAllBlogs();

        assertSame(list, blogList);
        assertNotNull(blogList);
        verify(blogPostRepository, times(1)).findAll();

    }

    @Test
    void showSingleBlogTest() {
        when(blogPostRepository.findById(blog1.getBlogId())).thenReturn(Optional.ofNullable(blog1));

        Blog newBlog1 = blogPostService.showSingleBlog(blog1.getBlogId());

        assertSame(blog1, newBlog1);
        assertNotNull(newBlog1);
        verify(blogPostRepository, times(1)).findById(blog1.getBlogId());

        when(blogPostRepository.findById(blog2.getBlogId())).thenReturn(Optional.ofNullable(blog2));

        Blog newBlog2 = blogPostService.showSingleBlog(blog2.getBlogId());

        assertSame(blog2, newBlog2);
        assertNotNull(newBlog2);
        verify(blogPostRepository, times(1)).findById(blog1.getBlogId());
    }

    @Test
    void deleteBlogPostTest() {

        Integer blogId = blog1.getBlogId();
        when(blogPostRepository.findById(blogId)).thenReturn(Optional.ofNullable(blog1));
        doNothing().when(blogPostRepository).deleteById(blogId);

        // Act
        blogPostService.deleteBlogPost(blogId, user);

        // Assert
        verify(blogPostRepository).deleteById(blogId);
    }

    @Test
    void deleteBlogPostWithNullBlogTest() {
        when(blogPostRepository.findById(blog1.getBlogId())).thenReturn(null);
        Assertions.assertThrows(NullPointerException.class, () -> blogPostService.deleteBlogPost(blog1.getBlogId(), user));
    }

    @Test
    void deleteBlogPostWithUnAuthorizedUserTest() {

        User user1 = new User();
        user1.setId(2);

        Blog blog = new Blog();
        blog.setBlogId(2);
        blog.setBlogAuthor("Test name");
        blog.setUser(user1);

        when(blogPostRepository.findById(blog.getBlogId())).thenReturn(Optional.of(blog));
        Assertions.assertThrows(UnauthorizedBlogAccess.class, () -> blogPostService.deleteBlogPost(blog.getBlogId(), user));
    }

    @Test
    void getBlogsByUserTest() {

        when(blogPostRepository.findAllByUserId(user.getId())).thenReturn(list);

        List<Blog> blogList = blogPostService.getBlogsByUser(user);

        assertSame(list, blogList);
        assertNotNull(blogList);
        verify(blogPostRepository, times(1)).findAllByUserId(user.getId());
    }

    @Test
    void getCommentsTest() {
        when(blogCommentRepository.findAllByBlogId(user.getId())).thenReturn(commentList);

        List<Blog> blogList = blogPostService.getBlogsByUser(user);

        assertNotNull(blogList);
        verify(blogPostRepository, times(1)).findAllByUserId(user.getId());
    }
}