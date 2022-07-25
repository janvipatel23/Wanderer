package ca.dal.cs.wanderer.controllers;

import ca.dal.cs.wanderer.exception.category.pinexception.InvalidPinId;
import ca.dal.cs.wanderer.models.Pin;
import ca.dal.cs.wanderer.models.PinRating;
import ca.dal.cs.wanderer.models.User;
import ca.dal.cs.wanderer.services.PinRatingsService;
import ca.dal.cs.wanderer.services.PinService;
import ca.dal.cs.wanderer.services.UserProfileService;
import ca.dal.cs.wanderer.util.ErrorMessages;
import ca.dal.cs.wanderer.util.SuccessMessages;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PinRatingsController.class)
@ActiveProfiles("test")
class PinRatingsControllerTest {

    @MockBean
    private PinRatingsService pinRatingsService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private UserProfileService userProfileService;

    @MockBean
    private PinService pinService;

    private MockMvc mockMvc;

    private User user;
    private Pin pin;
    private Integer pinRating = 4;
    private PinRating pinRatingObject = new PinRating();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        UserAuthenticationHelper.setAuthentication();
        pin = getPinDetails();
        user = getUser();
        pinRatingObject.setRating(pinRating);
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
    void getRatingsTest() throws Exception {

        List<PinRating> pinRatingList = new ArrayList<>();
        pinRatingList.add(pinRatingObject);
        when(userProfileService.fetchByEmail(anyString())).thenReturn(user);
        when(pinRatingsService.getRatings(pin.getPinId())).thenReturn(pinRatingList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/pinRating/getRatings").
                param("pinId", "1")).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains(SuccessMessages.PIN_RATING_RETRIEVE_SUCCESS.getSuccessMessage()));

    }

    @Test
    void getRatingsWithInvalidPinIdTest() throws Exception {

        List<PinRating> pinRatingList = new ArrayList<>();
        pinRatingList.add(pinRatingObject);
        when(userProfileService.fetchByEmail(anyString())).thenReturn(user);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/pinRating/getRatings").
                param("pinId", "-1")).andReturn();

        assertEquals(500, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains(ErrorMessages.INVALID_PIN_ID.getErrorMessage()));
    }

    @Test
    void getRatingByIdTest() throws Exception {

        when(userProfileService.fetchByEmail(anyString())).thenReturn(user);
        when(pinRatingsService.getRatingsByID(user.getId(), pin.getPinId())).thenReturn(pinRatingObject);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/pinRating/getRatingsById").
                param("pinId", "1")).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains(SuccessMessages.PIN_RATING_RETRIEVE_SUCCESS.getSuccessMessage()));
    }

    @Test
    void getRatingByIdWithInvalidPinIdTest() throws Exception {

        when(userProfileService.fetchByEmail(anyString())).thenReturn(user);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/pinRating/getRatingsById").
                param("pinId", "-1")).andReturn();
        assertEquals(500, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains(ErrorMessages.INVALID_PIN_ID.getErrorMessage()));
    }

    @Test
    void getRatingByIdWithNullUserTest() throws Exception {

        when(userProfileService.fetchByEmail(anyString())).thenReturn(null);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/pinRating/getRatingsById").
                param("pinId", "1")).andReturn();
        assertEquals(500, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains(ErrorMessages.PRINCIPAL_NOT_FOUND.getErrorMessage()));
    }

    @Test
    void addRatingTest() throws Exception {

        List<PinRating> pinRatingList = new ArrayList<>();
        pinRatingList.add(pinRatingObject);
        when(userProfileService.fetchByEmail(anyString())).thenReturn(user);
        when(pinService.getPinById(anyInt())).thenReturn(pin);

        when(pinRatingsService.addRatings(user.getId(), pin, pinRating)).thenReturn(pinRatingList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/pinRating/addRating").
                param("pinId", "1").param("pinRating","4")).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains(SuccessMessages.PIN_RATING_SUCCESS.getSuccessMessage()));
    }

    @Test
    void addRatingWithNullUserTest() throws Exception {

        List<PinRating> pinRatingList = new ArrayList<>();
        pinRatingList.add(pinRatingObject);
        when(userProfileService.fetchByEmail(anyString())).thenReturn(null);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/pinRating/addRating").
                param("pinId", "1").param("pinRating","4")).andReturn();
        assertEquals(500, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains(ErrorMessages.PRINCIPAL_NOT_FOUND.getErrorMessage()));
    }

    @Test
    void addRatingWithInvalidPinIdTest() throws Exception {

        List<PinRating> pinRatingList = new ArrayList<>();
        pinRatingList.add(pinRatingObject);
        when(userProfileService.fetchByEmail(anyString())).thenReturn(user);
        when(pinService.getPinById(anyInt())).thenReturn(pin);

        when(pinRatingsService.addRatings(user.getId(), pin, pinRating)).thenReturn(pinRatingList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/pinRating/addRating").
                param("pinId", "-1").param("pinRating","4")).andReturn();
        assertEquals(500, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains(ErrorMessages.INVALID_PIN_ID.getErrorMessage()));
    }

    @Test
    void addRatingWithInvalidPinRatingTest() throws Exception {

        List<PinRating> pinRatingList = new ArrayList<>();
        pinRatingList.add(pinRatingObject);
        when(userProfileService.fetchByEmail(anyString())).thenReturn(user);
        when(pinService.getPinById(anyInt())).thenReturn(pin);

        when(pinRatingsService.addRatings(user.getId(), pin, pinRating)).thenReturn(pinRatingList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/pinRating/addRating").
                param("pinId", "1").param("pinRating","-1")).andReturn();
        assertEquals(500, mvcResult.getResponse().getStatus());
        assertTrue(mvcResult.getResponse().getContentAsString().contains(ErrorMessages.INVALID_PIN_RATING.getErrorMessage()));
    }
}