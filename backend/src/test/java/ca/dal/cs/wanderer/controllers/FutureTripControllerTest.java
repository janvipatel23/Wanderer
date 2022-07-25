package ca.dal.cs.wanderer.controllers;

import ca.dal.cs.wanderer.dtos.FutureTripRequestDto;
import ca.dal.cs.wanderer.models.FutureTrip;
import ca.dal.cs.wanderer.models.User;
import ca.dal.cs.wanderer.services.FutureTripService;
import ca.dal.cs.wanderer.services.UserProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FutureTripController.class)
@ActiveProfiles("test")
public class FutureTripControllerTest {

    @Autowired
    private WebApplicationContext WebApplicationContext;

    @MockBean
    private FutureTripService futureTripService;

    @MockBean
    private UserProfileService userProfileService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(WebApplicationContext).build();
        UserAuthenticationHelper.setAuthentication();
    }

    @Test
    void testCreateFutureTrip() throws Exception {

        FutureTripRequestDto request = new FutureTripRequestDto();
        request.setPinId(1);
        request.setTripDate(Calendar.getInstance());
        request.setTripName("Test trip");
        request.setTripDescription("test Description");

        User expectedUser = getUser();
        when(userProfileService.fetchByEmail(anyString())).thenReturn(expectedUser);

        FutureTrip expectedFutureTrip = FutureTripRequestDto.convertToModel(request);
        when(futureTripService.saveFutureTrip(any(FutureTrip.class))).thenReturn(expectedFutureTrip);

        mockMvc.perform(post("/api/v1/wanderer/futuretrip/createFutureTrip")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("payload.tripName").value(request.getTripName()))
                .andExpect(jsonPath("payload.tripDescription").value(request.getTripDescription()));

        verify(userProfileService).fetchByEmail(anyString());
        verify(futureTripService).saveFutureTrip(any(FutureTrip.class));
    }

    @Test
    void testCreateFutureTrip_whenSaveNotWork() throws Exception {

        FutureTripRequestDto request = new FutureTripRequestDto();
        request.setPinId(1);
        request.setTripDate(Calendar.getInstance());
        request.setTripName("Test trip");
        request.setTripDescription("test Description");

        User expectedUser = getUser();
        when(userProfileService.fetchByEmail(anyString())).thenReturn(expectedUser);

        when(futureTripService.saveFutureTrip(any(FutureTrip.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/wanderer/futuretrip/createFutureTrip")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        verify(userProfileService).fetchByEmail(anyString());
        verify(futureTripService).saveFutureTrip(any(FutureTrip.class));
    }


    @Test
    void testCreateFutureTrip_whenInvalidPinId() throws Exception {
        FutureTripRequestDto request = new FutureTripRequestDto();
        request.setPinId(-1);
        mockMvc.perform(post("/api/v1/wanderer/futuretrip/createFutureTrip")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testCreateFutureTrip_whenUserIdIsNull() throws Exception {
        FutureTripRequestDto request = new FutureTripRequestDto();
        request.setPinId(1);

        User expectedUser = new User();
        when(userProfileService.fetchByEmail(anyString())).thenReturn(expectedUser);

        mockMvc.perform(post("/api/v1/wanderer/futuretrip/createFutureTrip")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        verify(userProfileService).fetchByEmail(anyString());
    }

    @Test
    void testFetchFutureTripsByUserId() throws Exception {

        when(futureTripService.fetchFutureTripsByUserId(anyString())).thenReturn(getFutureTrips());

        mockMvc.perform(get("/api/v1/wanderer/futuretrip/fetchFutureTripsByUserId"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("isSuccessful").value("true"));

        verify(futureTripService).fetchFutureTripsByUserId(anyString());
    }

    @Test
    void testFetchFutureTripsByPinId() throws Exception {
        when(futureTripService.fetchFutureTripsByPinId(anyInt())).thenReturn(getFutureTrips());

        mockMvc.perform(get("/api/v1/wanderer/futuretrip/fetchFutureTripsByPinId").queryParam("pinId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("isSuccessful").value("true"))
                .andExpect(jsonPath("payload.length()").value(2));

        verify(futureTripService).fetchFutureTripsByPinId(anyInt());
    }

    @Test
    void testDeleteFutureTripById() throws Exception {
        mockMvc.perform(delete("/api/v1/wanderer/futuretrip/deleteFutureTripById").queryParam("futureTripId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("isSuccessful").value("true"));
    }

    @Test
    void testUpdateFutureTripById() throws Exception {

        FutureTripRequestDto request = new FutureTripRequestDto();
        request.setPinId(1);
        request.setTripDate(Calendar.getInstance());
        request.setTripName("Test trip");
        request.setTripDescription("test Description");

        when(futureTripService.updateFutureTrip(anyInt(), any(FutureTrip.class))).thenReturn(FutureTripRequestDto.convertToModel(request));

        mockMvc.perform(put("/api/v1/wanderer/futuretrip/updateFutureTrip/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("isSuccessful").value("true"))
                .andExpect(jsonPath("payload.tripName").value(request.getTripName()))
                .andExpect(jsonPath("payload.tripDescription").value(request.getTripDescription()));

        verify(futureTripService).updateFutureTrip(anyInt(), any(FutureTrip.class));
    }


    private List<FutureTrip> getFutureTrips() {
        List<FutureTrip> futureTrips = new ArrayList<>();

        FutureTrip futureTrip = new FutureTrip();
        futureTrip.setTripId(1);

        futureTrips.add(futureTrip);

        futureTrip = new FutureTrip();
        futureTrip.setTripId(2);
        futureTrips.add(futureTrip);

        return futureTrips;
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