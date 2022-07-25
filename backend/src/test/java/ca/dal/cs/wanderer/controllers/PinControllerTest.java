package ca.dal.cs.wanderer.controllers;

import ca.dal.cs.wanderer.models.Pin;
import ca.dal.cs.wanderer.models.User;
import ca.dal.cs.wanderer.repositories.PinRepository;
import ca.dal.cs.wanderer.services.PinService;
import ca.dal.cs.wanderer.services.UserProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PinController.class)
@ActiveProfiles("test")
class PinControllerTest {

    @Autowired
    private WebApplicationContext WebApplicationContext;

    @MockBean
    private PinService pinService;

    @MockBean
    private UserProfileService userProfileService;

    private MockMvc mockMvc;

    @Autowired
    PinController pinController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(WebApplicationContext).build();
        UserAuthenticationHelper.setAuthentication();
    }

    @Test
    void createPin() throws Exception {
        User authenticatedUser = getUser();

        Pin pin = new Pin(10, "Halifax", 10.0, 10.0, "Testing description");
        Pin savedPin = new Pin();

        ObjectMapper Obj = new ObjectMapper();
        String jsonStr = Obj.writeValueAsString(pin);
        when(pinService.savePin(any(), any(), any())).thenReturn(savedPin);

        MockMultipartFile firstFile = new MockMultipartFile("pin", "", "application/json", jsonStr.getBytes());
        MockMultipartFile secondFile = new MockMultipartFile("images", "", "application/json", "".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/pin/createPin")
                .file(firstFile)
                .file(secondFile))
                .andExpect(status().isOk());
    }

    @Test
    void createPinWithMaxLocationLimit() throws Exception {
        Pin pin = new Pin(10, "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum. Naaaaaa", 10.0, 10.0, "Testing description");

        Pin savedPin = new Pin();

        ObjectMapper Obj = new ObjectMapper();
        String jsonStr = Obj.writeValueAsString(pin);
        when(pinService.savePin(any(), any(), any())).thenReturn(savedPin);

        MockMultipartFile firstFile = new MockMultipartFile("pin", "", "application/json",jsonStr.getBytes());
        MockMultipartFile secondFile = new MockMultipartFile("images", "", "application/json", "".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/pin/createPin")
                .file(firstFile)
                        .file(secondFile))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createPinWithMaxDescriptionLimit() throws Exception {
        Pin pin = new Pin(10, "Halifax", 10.0, 10.0, "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum. Naaaaaa");

        Pin savedPin = new Pin();

        ObjectMapper Obj = new ObjectMapper();
        String jsonStr = Obj.writeValueAsString(pin);
        when(pinService.savePin(any(), any(), any())).thenReturn(savedPin);

        MockMultipartFile firstFile = new MockMultipartFile("pin", "", "application/json",jsonStr.getBytes());
        MockMultipartFile secondFile = new MockMultipartFile("images", "", "application/json", "".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/pin/createPin")
                        .file(firstFile)
                        .file(secondFile))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createPinWithInvalidLatitude() throws Exception {
        Pin pin = new Pin(10, "Halifax", 91.0, 10.0, "g elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum. Naaaaaa");

        Pin savedPin = new Pin();

        ObjectMapper Obj = new ObjectMapper();
        String jsonStr = Obj.writeValueAsString(pin);
        when(pinService.savePin(any(), any(), any())).thenReturn(savedPin);

        MockMultipartFile firstFile = new MockMultipartFile("pin", "", "application/json",jsonStr.getBytes());
        MockMultipartFile secondFile = new MockMultipartFile("images", "", "application/json", "".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/pin/createPin")
                        .file(firstFile)
                        .file(secondFile))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createPinWithNotSavedPin() throws Exception {
        Pin pin = new Pin(10, "Halifax", 10.0, 10.0, "Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing sem neque sed ipsum. Naaaaaa");

        Pin savedPin = null;

        ObjectMapper Obj = new ObjectMapper();
        String jsonStr = Obj.writeValueAsString(pin);
        when(pinService.savePin(any(), any(), any())).thenReturn(savedPin);

        MockMultipartFile firstFile = new MockMultipartFile("pin", "", "application/json",jsonStr.getBytes());
        MockMultipartFile secondFile = new MockMultipartFile("images", "", "application/json", "".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/pin/createPin")
                        .file(firstFile)
                        .file(secondFile))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getPinsByRadius() throws Exception {
        List<PinRepository.PinBasicInfo> pinList = new ArrayList<>();
        when(pinService.getPinsByRadius(anyDouble(), anyDouble(), anyDouble())).thenReturn(pinList);
        mockMvc.perform(get("/api/v1/pin/getPinsByRadius")
                        .param("radius", "5.0")
                        .param("centerLat", "5.0")
                        .param("centerLng", "5.0"))
                .andExpect(status().isOk());
    }

    @Test
    void getPinsByRadiusLessThanZero() throws Exception {
        List<PinRepository.PinBasicInfo> pinList = new ArrayList<>();
        when(pinService.getPinsByRadius(anyDouble(), anyDouble(), anyDouble())).thenReturn(pinList);
        mockMvc.perform(get("/api/v1/pin/getPinsByRadius")
                        .param("radius", "-5.0")
                        .param("centerLat", "5.0")
                        .param("centerLng", "5.0"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getPinsByRadiusInvalidLatitude() throws Exception {
        List<PinRepository.PinBasicInfo> pinList = new ArrayList<>();
        when(pinService.getPinsByRadius(anyDouble(), anyDouble(), anyDouble())).thenReturn(pinList);
        mockMvc.perform(get("/api/v1/pin/getPinsByRadius")
                        .param("radius", "5.0")
                        .param("centerLat", "91.0")
                        .param("centerLng", "5.0"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getPinsByRadiusNullPinList() throws Exception {
        List<PinRepository.PinBasicInfo> pinList = null;
        when(pinService.getPinsByRadius(anyDouble(), anyDouble(), anyDouble())).thenReturn(pinList);
        mockMvc.perform(get("/api/v1/pin/getPinsByRadius")
                        .param("radius", "5.0")
                        .param("centerLat", "5.0")
                        .param("centerLng", "5.0"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getPinsByIds() throws Exception {
        List<Pin> pinList = new ArrayList<>();
        when(pinService.getPinsByIds(any())).thenReturn(pinList);
        mockMvc.perform(get("/api/v1/pin/getPinsByIds?pinIds=1,2"))
                .andExpect(status().isOk());
    }

    @Test
    void getPinsByIdsNullPinList() throws Exception {
        List<Pin> pinList = null;
        when(pinService.getPinsByIds(any())).thenReturn(pinList);
        mockMvc.perform(get("/api/v1/pin/getPinsByIds?pinIds=1,2"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getSinglePinById() throws Exception {
        Pin pin = new Pin(1,"Halifax",10.0,10.0,"Hello");
        when(pinService.getPinById(anyInt())).thenReturn(pin);
        mockMvc.perform(get("/api/v1/pin/getPinById?pinId=1"))
                .andExpect(status().isOk());
    }

    @Test
    void getSinglePinByIdWithMinusId() throws Exception {
        Pin pin = new Pin(1,"Halifax",10.0,10.0,"Hello");
        when(pinService.getPinById(anyInt())).thenReturn(pin);
        mockMvc.perform(get("/api/v1/pin/getPinById?pinId=-1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getSinglePinByIdWithNullPin() throws Exception {
        Pin pin = null;
        when(pinService.getPinById(anyInt())).thenReturn(pin);
        mockMvc.perform(get("/api/v1/pin/getPinById?pinId=-1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deletePinById() throws Exception {
        doNothing().when(pinService).deletePin(any(),any());
        User user = new User();
        user.setEmailId("test@test.com");
        user.setId(1);
        when(userProfileService.fetchByEmail(any())).thenReturn(user);
        mockMvc.perform(delete("/api/v1/pin/deletePinById?pinId=1"))
                .andExpect(status().isOk());
    }

    @Test
    void deletePinByIdWithMinusId() throws Exception {
        doNothing().when(pinService).deletePin(any(),any());
        mockMvc.perform(delete("/api/v1/pin/deletePinById?pinId=-1"))
                .andExpect(status().isInternalServerError());
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