package ca.dal.cs.wanderer.controllers;

import ca.dal.cs.wanderer.models.Pin;
import ca.dal.cs.wanderer.models.PinComment;
import ca.dal.cs.wanderer.models.PinRating;
import ca.dal.cs.wanderer.models.User;
import ca.dal.cs.wanderer.services.PinCommentsService;
import ca.dal.cs.wanderer.services.PinService;
import ca.dal.cs.wanderer.services.UserProfileService;
import ca.dal.cs.wanderer.util.ErrorMessages;
import ca.dal.cs.wanderer.util.SuccessMessages;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebMvcTest(PinCommentsController.class)
@ActiveProfiles("test")
class PinCommentsControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private PinCommentsService pinCommentsService;

    @MockBean
    private UserProfileService userProfileService;

    @MockBean
    private PinService pinService;

    private MockMvc mockMvc;
    private User user;
    private Pin pin;
    private String comment = "Test comment";
    PinComment pinCommentObject = new PinComment();
    List<PinComment> pinCommentList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        UserAuthenticationHelper.setAuthentication();

        pin = getPinDetails();
        user = getUser();

        pinCommentObject.setComment(comment);
        pinCommentObject.setUser(user);
        pinCommentList.add(pinCommentObject);
    }

    private User getUser() {
        User user = new User();
        user.setFirstName("Test First name");
        user.setLastName("Test last name");
        user.setId(1);
        user.setEmailId("test@gmail.com");
        return user;
    }

    private Pin getPinDetails() {
        Pin pin = new Pin(1, "Halifax", 10.0, 10.0, "Test description");
        return pin;
    }

    @Test
    void getCommentsTest() throws Exception {

        when(userProfileService.fetchByEmail(anyString())).thenReturn(user);
        when(pinCommentsService.getComments(pin.getPinId())).thenReturn(pinCommentList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/pin/getComments")
                .param("pinId","1")).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains(SuccessMessages.COMMENTS_RETRIEVE_SUCCESS.getSuccessMessage()));

    }

    @Test
    void getCommentsWithInvalidPinIdTest() throws Exception {

        when(userProfileService.fetchByEmail(anyString())).thenReturn(user);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/pin/getComments")
                .param("pinId","-1")).andReturn();
        assertEquals(500, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains(ErrorMessages.INVALID_PIN_ID.getErrorMessage()));

    }

    @Test
    void addCommentsTest() throws Exception {

        when(userProfileService.fetchByEmail(anyString())).thenReturn(user);
        when(pinService.getPinById(anyInt())).thenReturn(pin);

        when(pinCommentsService.addComment(user, pin, comment)).thenReturn(pinCommentObject);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/pin/addComments").
                content(new ObjectMapper().writeValueAsString(comment)).param("pinId","1")).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains(SuccessMessages.COMMENT_ADD_SUCCESS.getSuccessMessage()));

    }

    @Test
    void addCommentsWithInvalidPinIdTest() throws Exception {

        when(userProfileService.fetchByEmail(anyString())).thenReturn(user);

        when(pinCommentsService.addComment(user, pin, comment)).thenReturn(pinCommentObject);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/pin/addComments").
                content(new ObjectMapper().writeValueAsString(comment)).param("pinId","-1")).andReturn();
        assertEquals(500, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains(ErrorMessages.INVALID_PIN_ID.getErrorMessage()));

    }

    @Test
    void addCommentsWithNullUserTest() throws Exception {

        when(userProfileService.fetchByEmail(anyString())).thenReturn(null);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/pin/addComments").
                content(new ObjectMapper().writeValueAsString(comment)).param("pinId","1")).andReturn();
        assertEquals(500, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains(ErrorMessages.PRINCIPAL_NOT_FOUND.getErrorMessage()));

    }

    @Test
    void addCommentsWithNullCommentTest() throws Exception {

        when(userProfileService.fetchByEmail(anyString())).thenReturn(user);
        when(pinService.getPinById(anyInt())).thenReturn(pin);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/pin/addComments").
                content(new String()).param("pinId","1")).andReturn();
        assertEquals(400, mvcResult.getResponse().getStatus());

    }
}