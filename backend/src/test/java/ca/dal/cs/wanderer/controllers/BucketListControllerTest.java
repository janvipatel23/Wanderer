package ca.dal.cs.wanderer.controllers;

import ca.dal.cs.wanderer.models.Pin;
import ca.dal.cs.wanderer.models.User;
import ca.dal.cs.wanderer.services.BucketListService;
import ca.dal.cs.wanderer.services.PinService;
import ca.dal.cs.wanderer.services.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static ca.dal.cs.wanderer.controllers.UserAuthenticationHelper.setAuthentication;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BucketListController.class)
@ActiveProfiles("test")
class BucketListControllerTest {
    @Autowired
   private WebApplicationContext webApplicationContext;

    @MockBean
    private BucketListService bucketListService;

    @MockBean
    private UserProfileService userProfileService;

    @MockBean
    private PinService pinService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        UserAuthenticationHelper.setAuthentication();
    }

    @Test
    void insertPinTestWhenPinIdIsInvalid() throws Exception {

        mockMvc.perform(post("/api/v1/bucketList/addPinToBucketList").queryParam("pinId", String.valueOf(-1)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void insertPinTestWhenPinIdIsNull() throws Exception {

        when(pinService.getPinById(anyInt())).thenReturn(null);
        mockMvc.perform(post("/api/v1/bucketList/addPinToBucketList").queryParam("pinId", String.valueOf(1)))
                .andExpect(status().isInternalServerError());
        verify(pinService).getPinById(anyInt());
    }

    @Test
    void insertPinTestSuccess() throws Exception {
        User authenticatedUser = getUser();

        Pin pin = new Pin();
        pin.setPinId(1);

        User user = new User();
        user.setId(1);
        user.setEmailId("test@gmail.com");

        when(pinService.getPinById(anyInt())).thenReturn(pin);
        when(userProfileService.fetchByEmail(anyString())).thenReturn(user);
        mockMvc.perform(post("/api/v1/bucketList/addPinToBucketList").queryParam("pinId", String.valueOf(1)))
                .andExpect(status().isOk());

        verify(pinService).getPinById(anyInt());
        verify(userProfileService).fetchByEmail(anyString());
    }

    @Test
    void deletePinTestWhenPinIdIsInvalid() throws Exception {
        mockMvc.perform(delete("/api/v1/bucketList/deletePinFromBucketList").queryParam("pinId", String.valueOf(-1)))
                .andExpect(status().isInternalServerError());

    }

    @Test
    void deletePinTestSuccess() throws Exception {
        User user = new User();
        user.setId(1);
        user.setEmailId("test@gmail.com");

        when(userProfileService.fetchByEmail(anyString())).thenReturn(user);

        mockMvc.perform(delete("/api/v1/bucketList/deletePinFromBucketList").queryParam("pinId", String.valueOf(1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("isSuccessful").value(true));

        verify(userProfileService).fetchByEmail(anyString());
    }

    @Test
    void listAllPinsTestSuccess() throws Exception {

        User user = new User();
        user.setId(1);
        user.setEmailId("test@gmail.com");

        when(userProfileService.fetchByEmail(anyString())).thenReturn(user);

        mockMvc.perform(get("/api/v1/bucketList/listOfAllPins").queryParam("userId", String.valueOf(1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("isSuccessful").value(true));

        verify(userProfileService).fetchByEmail(anyString());
    }

    @Test
    void checkIfPinInBucketListTestWhenPinIdIsInvalid() throws Exception {
        mockMvc.perform(get("/api/v1/bucketList/checkIfPinInBucketList").queryParam("pinId", String.valueOf(-1)))
                .andExpect(status().isInternalServerError());

    }

    @Test
    void checkIfPinInBucketListTestSuccess() throws Exception {
        User user = new User();
        user.setId(1);
        user.setEmailId("test@gmail.com");

        when(userProfileService.fetchByEmail(anyString())).thenReturn(user);

        mockMvc.perform(get("/api/v1/bucketList/checkIfPinInBucketList").queryParam("pinId", String.valueOf(1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("isSuccessful").value(true));

        verify(userProfileService).fetchByEmail(anyString());
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

