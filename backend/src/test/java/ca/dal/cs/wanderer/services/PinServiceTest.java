package ca.dal.cs.wanderer.services;

import ca.dal.cs.wanderer.exception.category.pinexception.PinNotFound;
import ca.dal.cs.wanderer.exception.category.pinexception.UnauthorizedPinAccess;
import ca.dal.cs.wanderer.models.Pin;
import ca.dal.cs.wanderer.models.User;
import ca.dal.cs.wanderer.repositories.PinImageRepo;
import ca.dal.cs.wanderer.repositories.PinRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

public class PinServiceTest {
    @Mock
    private PinRepository pinRepository;

    @Mock
    private PinImageRepo pinImageRepo;

    @Mock
    private PinUpdateService pinUpdateService;

    @InjectMocks
    private PinService pinService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        pinService = Mockito.spy(pinService);
        mockMvc = MockMvcBuilders.standaloneSetup(pinService).build();
    }

    @Test
    void testSavePinForUpdate() {
        Pin pin = new Pin();
        pin.setPinId(78);
        pin.setUserId(1);
        MultipartFile[] files = new MultipartFile[1];
        files[0] = Mockito.mock(MultipartFile.class);
        User user = getUser();
        user.setId(1);
        pin.setUserId(user.getId());

        when(pinRepository.save(any(Pin.class))).thenReturn(pin);
        doNothing().when(pinImageRepo).deleteAllByPinId(anyInt());
        doReturn(pin).when(pinService).getPinById(anyInt());

        Pin savedPin = null;
        try {
            savedPin = pinService.savePin(pin, files, user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertNotNull(savedPin);
        assertSame(pin, savedPin);

        verify(pinRepository, times(1)).save(any(Pin.class));
        verify(pinImageRepo, times(1)).deleteAllByPinId(anyInt());
        verify(pinService, times(1)).getPinById(anyInt());
        try {
            verify(pinUpdateService, times(1)).sendPinUpdate();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testSavePinForUpdateWhenPinNotFound() {
        Pin pin = new Pin();
        pin.setPinId(78);
        pin.setUserId(1);
        MultipartFile[] files = new MultipartFile[1];
        files[0] = Mockito.mock(MultipartFile.class);
        User user = getUser();
        user.setId(1);
        pin.setUserId(user.getId());

        doNothing().when(pinImageRepo).deleteAllByPinId(anyInt());
        doReturn(null).when(pinService).getPinById(anyInt());

        assertThrows(PinNotFound.class, () -> {
            Pin savedPin = pinService.savePin(pin, files, user);
            assertNull(savedPin);
        });

        verify(pinService, times(1)).getPinById(anyInt());
    }

    @Test
    void testSavePinForUpdateWhenPinUpdatedByAnotherUser() {
        Pin pin = new Pin();
        pin.setPinId(78);
        pin.setUserId(1);
        MultipartFile[] files = new MultipartFile[1];
        files[0] = Mockito.mock(MultipartFile.class);
        User user = getUser();

        // owner of pin is different from user trying to update pin
        user.setId(1);
        pin.setUserId(2);

        doNothing().when(pinImageRepo).deleteAllByPinId(anyInt());
        doReturn(pin).when(pinService).getPinById(anyInt());

        assertThrows(UnauthorizedPinAccess.class, () -> {
            pinService.savePin(pin, files, user);
        });

        verify(pinService, times(1)).getPinById(anyInt());
    }

    @Test
    void testSavePinForCreate() {
        Pin pin = new Pin();
        pin.setPinId(-1);
        pin.setUserId(1);
        MultipartFile[] files = new MultipartFile[1];
        files[0] = Mockito.mock(MultipartFile.class);
        User user = getUser();

        when(pinRepository.save(any(Pin.class))).thenReturn(pin);

        Pin savedPin = null;
        try {
            savedPin = pinService.savePin(pin, files, user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertNotNull(savedPin);

        verify(pinService, times(0)).getPinById(anyInt());
        verify(pinRepository, times(1)).save(any(Pin.class));
        verify(pinImageRepo, times(0)).deleteAllByPinId(anyInt());
        try {
            verify(pinUpdateService, times(1)).sendPinUpdate();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    void testGetPinsByRadius() {
        // create mock of interface PinRepository.PinBasicInfo
        List<PinRepository.PinBasicInfo> pinBasicInfoList = new ArrayList<>();

        when(pinRepository.getPinsByRadius(anyDouble(), anyDouble(), anyDouble())).thenReturn(pinBasicInfoList);

        List<PinRepository.PinBasicInfo> savedPinBasicInfoList = pinService.getPinsByRadius(0.0, 0.0, 0.0);

        assertNotNull(savedPinBasicInfoList);
        assertSame(pinBasicInfoList, savedPinBasicInfoList);

        verify(pinRepository, times(1)).getPinsByRadius(anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    void testGetPinsById() {
        List<Pin> pinList = new ArrayList<>();

        when(pinRepository.findAllById(anyList())).thenReturn(pinList);

        List<Pin> savedPinList = pinService.getPinsByIds(new ArrayList<>());

        assertNotNull(savedPinList);
        assertSame(pinList, savedPinList);

        verify(pinRepository, times(1)).findAllById(anyList());
    }

    @Test
    void testDeletePin() throws ExecutionException, InterruptedException {
        Integer userId = 1;
        Pin pin = new Pin();
        pin.setPinId(78);
        pin.setUserId(userId);

        doReturn(pin).when(pinService).getPinById(anyInt());
        doNothing().when(pinRepository).deleteById(anyInt());
        when(pinUpdateService.sendPinUpdate()).thenReturn(null);

        assertDoesNotThrow(() -> pinService.deletePin(pin.getUserId(), userId));

        verify(pinRepository, times(1)).deleteById(pin.getPinId());
        verify(pinUpdateService, times(1)).sendPinUpdate();
    }

    @Test
    void testDeletePinOnInvalidPinId() throws ExecutionException, InterruptedException {
        Integer userId = 1;
        Pin pin = new Pin();
        pin.setPinId(-1);
        pin.setUserId(userId);

        doReturn(null).when(pinService).getPinById(anyInt());
        doNothing().when(pinRepository).deleteById(anyInt());
        when(pinUpdateService.sendPinUpdate()).thenReturn(null);

        assertThrows(PinNotFound.class, () -> pinService.deletePin(pin.getUserId(), userId));

        verify(pinRepository, times(0)).deleteById(pin.getPinId());
        verify(pinUpdateService, times(0)).sendPinUpdate();
    }

    @Test
    void testDeletePinOnInvalidUserId() throws ExecutionException, InterruptedException {
        Pin pin = new Pin();
        pin.setPinId(78);
        User user = getUser();

        // owner of pin is not the same as user trying to delete pin
        pin.setUserId(2);
        user.setId(1);

        doReturn(pin).when(pinService).getPinById(anyInt());
        doNothing().when(pinRepository).deleteById(anyInt());
        when(pinUpdateService.sendPinUpdate()).thenReturn(null);

        assertThrows(UnauthorizedPinAccess.class, () -> pinService.deletePin(pin.getUserId(), user.getId()));

        verify(pinRepository, times(0)).deleteById(pin.getPinId());
        verify(pinUpdateService, times(0)).sendPinUpdate();
    }

    @Test
    void testGetPinById() {
        Integer validPinId = 1;
        Integer invalidPinId = -1;
        Pin pin = new Pin();
        pin.setPinId(validPinId);

        // add valid pin id
        when(pinRepository.findById(anyInt())).thenReturn(Optional.of(pin));

        assertNotNull(pinService.getPinById(validPinId));

        // add invalid pin id
        when(pinRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertNull(pinService.getPinById(invalidPinId));

        verify(pinRepository, times(2)).findById(anyInt());
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
