package ca.dal.cs.wanderer.controllers;

import ca.dal.cs.wanderer.models.User;
import ca.dal.cs.wanderer.services.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserProfileController.class)
@ActiveProfiles("test")
public class UserProfileControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private UserProfileService userProfileService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        UserAuthenticationHelper.setAuthentication();
    }

    @Test
    public void testFetchSingle() throws Exception {
        User expectedUser = getUser();
        when(userProfileService.fetchByEmail(anyString())).thenReturn(expectedUser);

        mockMvc.perform(get("/api/v1/wanderer/user/getDetails")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("payload.emailId").value("test@gmail.com"))
                .andExpect(jsonPath("payload.lastName").value("Test last name"))
                .andExpect(jsonPath("payload.firstName").value("Test First name"));

        verify(userProfileService, times(1)).fetchByEmail(anyString());
    }

    @Test
    public void testUpdateUser() throws Exception {
        User expectedUser = getUser();
        when(userProfileService.fetchByEmail(anyString())).thenReturn(expectedUser);
        when(userProfileService.updateProfile(any(),any(),any(),any())).thenReturn(expectedUser);

        mockMvc.perform(put("/api/v1/wanderer/user/updateProfile")
                        .param("fName","user")
                        .param("lname","user"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testGetUserId() throws Exception {
        User expectedUser = getUser();
        when(userProfileService.fetchByEmail(anyString())).thenReturn(expectedUser);
        when(userProfileService.updateProfile(any(),any(),any(),any())).thenReturn(expectedUser);

        mockMvc.perform(get("/api/v1/wanderer/user/getUserId"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    private User getUser() {
        User user = new User();
        user.setFirstName("Test First name");
        user.setLastName("Test last name");
        user.setId(10);
        user.setEmailId("test@gmail.com");
        return user;
    }
}