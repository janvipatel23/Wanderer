package ca.dal.cs.wanderer.controllers;

import ca.dal.cs.wanderer.models.Blog;
import ca.dal.cs.wanderer.models.BlogComment;
import ca.dal.cs.wanderer.models.User;
import ca.dal.cs.wanderer.services.BlogPostService;
import ca.dal.cs.wanderer.services.UserProfileService;
import ca.dal.cs.wanderer.util.ErrorMessages;
import ca.dal.cs.wanderer.util.SuccessMessages;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(BlogPostController.class)
@ActiveProfiles("test")
class BlogPostControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private BlogPostService blogPostService;

    @MockBean
    private UserProfileService userProfileService;

    @Autowired
    private BlogPostController blogPostController;

    private MockMvc mockMvc;
    private User user;
    private Blog workingBlog;
    private Blog nonWorkingBlog = null;
    private BlogComment blogComment;
    private String comment = "Test comment";
    List<Blog> blogsList = new ArrayList<>();
    List<BlogComment> blogComments = new ArrayList<>();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        UserAuthenticationHelper.setAuthentication();
        user = getUser();
        workingBlog = getBlog(user);
        blogComment = getBlogComment();
        blogsList.add(workingBlog);
        blogComments.add(blogComment);
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
        blog.setBlogId(1);
        blog.setBlogTitle("Test Title");
        blog.setBlogAuthor(user.getFirstName() + " " + user.getLastName());
        blog.setBlogDescription("Test Description");
        blog.setUser(user);
        return blog;
    }

    private BlogComment getBlogComment() {
        BlogComment blogComment = new BlogComment();
        blogComment.setComment(comment);
        return blogComment;
    }

    @Test
    void addCommentsTest() throws Exception {

        when(userProfileService.fetchByEmail(anyString())).thenReturn(user);
        when(blogPostService.showSingleBlog(anyInt())).thenReturn(workingBlog);

        when(blogPostService.addComment(user, workingBlog, comment)).thenReturn(blogComment);
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/blogs/addComments")
                        .param("blogId", "5")
                        .content(new ObjectMapper().writeValueAsString(comment))).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains(SuccessMessages.COMMENT_ADD_SUCCESS.getSuccessMessage()));
    }

    @Test
    void addCommentNullTest() throws Exception {
        when(userProfileService.fetchByEmail(anyString())).thenReturn(user);
        when(blogPostService.showSingleBlog(any())).thenReturn(nonWorkingBlog);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/blogs/addComments")
                        .param("blogId", "5")
                        .content(new ObjectMapper().writeValueAsString(comment))).andReturn();

        assertEquals(500, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains(ErrorMessages.BLOG_NOT_FOUND.getErrorMessage()));
    }

    @Test
    void addCommentNegativeTest() throws Exception {
        when(userProfileService.fetchByEmail(anyString())).thenReturn(user);
        when(blogPostService.showSingleBlog(any())).thenReturn(workingBlog);

        when(blogPostService.addComment(user, workingBlog, comment)).thenReturn(blogComment);
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/blogs/addComments?blogId=-5")
                        .param("blogId", "5")
                        .content("content")).andReturn();

        assertEquals(500, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains(ErrorMessages.INVALID_BLOG_ID.getErrorMessage()));
    }

    @Test
    void showAllBlogsTest() throws Exception {
        when(userProfileService.fetchByEmail(anyString())).thenReturn(user);
        when(blogPostService.showAllBlogs()).thenReturn(blogsList);
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/blogs/getAllBlogs")).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains(SuccessMessages.ALL_BLOG_RETRIEVE_SUCCESS.getSuccessMessage()));
    }

    @Test
    void showSingleBlogTest() throws Exception {
        when(userProfileService.fetchByEmail(anyString())).thenReturn(user);
        when(blogPostService.showSingleBlog(any())).thenReturn(workingBlog);
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/blogs/getBlogById")
                        .param("blogId", "5")).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains(SuccessMessages.BLOG_RETRIEVE_SUCCESS.getSuccessMessage()));
    }

    @Test
    void showSingleBlogNegativeTest() throws Exception {
        when(userProfileService.fetchByEmail(anyString())).thenReturn(user);
        when(blogPostService.showSingleBlog(any())).thenReturn(workingBlog);
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/blogs/getBlogById?blogId=-5")
                        .param("blogId", "3")).andReturn();
        assertEquals(500, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains(ErrorMessages.INVALID_BLOG_ID.getErrorMessage()));

    }

    @Test
    void showSingleBlogNullTest() throws Exception {
        when(userProfileService.fetchByEmail(anyString())).thenReturn(user);
        when(blogPostService.showSingleBlog(any())).thenReturn(nonWorkingBlog);
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/blogs/getBlogById")
                        .param("blogId", "5")).andReturn();

        assertEquals(500, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains(ErrorMessages.BLOG_NOT_FOUND.getErrorMessage()));
    }

    @Test
    void deleteBlogByIdTest() throws Exception {
        when(userProfileService.fetchByEmail(anyString())).thenReturn(user);
        doNothing().when(blogPostService).deleteBlogPost(any(), any());
        MvcResult mvcResult = mockMvc.perform(delete("/api/v1/blogs/deleteBlogById")
                        .param("blogId", "1")).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains(SuccessMessages.BLOG_DELETE_SUCCESS.getSuccessMessage()));

    }

    @Test
    void getCommentsTest() throws Exception {
        when(userProfileService.fetchByEmail(anyString())).thenReturn(user);
        when(blogPostService.getComments(any())).thenReturn(blogComments);
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/blogs/getComments")
                        .param("blogId", "5")).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains(SuccessMessages.COMMENTS_RETRIEVE_SUCCESS.getSuccessMessage()));

    }

    @Test
    void getCommentsForBlogIdNegativeTest() throws Exception {
        when(userProfileService.fetchByEmail(anyString())).thenReturn(user);
        when(blogPostService.getComments(any())).thenReturn(blogComments);
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/blogs/getComments?blogId=-5")
                        .param("blogId", "5")).andReturn();
        assertEquals(500, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains(ErrorMessages.INVALID_BLOG_ID.getErrorMessage()));
    }

    @Test
    void createBlogsTest() throws Exception {

        ObjectMapper Obj = new ObjectMapper();
        String jsonStr = Obj.writeValueAsString(workingBlog);
        when(userProfileService.fetchByEmail(any())).thenReturn(user);
        when(blogPostService.createBlog(any(), any(), any())).thenReturn(workingBlog);

        MockMultipartFile firstFile = new MockMultipartFile("blog", "", "application/json", jsonStr.getBytes());
        MockMultipartFile secondFile = new MockMultipartFile("blogImage", "", "application/json", "".getBytes());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/blogs/addBlog")
                        .file(firstFile)
                        .file(secondFile)).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains(SuccessMessages.BLOG_CREATE_SUCCESS.getSuccessMessage()));
    }

    @Test
    void createBlogsTestNullUser() throws Exception {

        ObjectMapper Obj = new ObjectMapper();
        String jsonStr = Obj.writeValueAsString(workingBlog);
        when(userProfileService.fetchByEmail(any())).thenReturn(null);

        MockMultipartFile firstFile = new MockMultipartFile("blog", "", "application/json", jsonStr.getBytes());
        MockMultipartFile secondFile = new MockMultipartFile("blogImage", "", "application/json", "".getBytes());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/blogs/addBlog")
                        .file(firstFile)
                        .file(secondFile)).andReturn();
        assertEquals(500, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains(ErrorMessages.PRINCIPAL_NOT_FOUND.getErrorMessage()));

    }

    @Test
    void getBlogByUserTest() throws Exception {

        when(userProfileService.fetchByEmail(any())).thenReturn(user);
        when(blogPostService.getBlogsByUser(any())).thenReturn(blogsList);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/blogs/getBlogsByUser")).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains(SuccessMessages.BLOG_RETRIEVE_SUCCESS.getSuccessMessage()));
    }

    @Test
    void getBlogWithNullUserTest() throws Exception {

        when(userProfileService.fetchByEmail(any())).thenReturn(null);
        when(blogPostService.getBlogsByUser(any())).thenReturn(blogsList);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/blogs/getBlogsByUser")).andReturn();
        assertEquals(500, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains(ErrorMessages.PRINCIPAL_NOT_FOUND.getErrorMessage()));

    }
}
