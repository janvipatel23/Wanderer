package ca.dal.cs.wanderer.services;

import ca.dal.cs.wanderer.models.FutureTrip;
import ca.dal.cs.wanderer.models.Pin;
import ca.dal.cs.wanderer.models.User;
import ca.dal.cs.wanderer.repositories.FutureTripRepository;
import ca.dal.cs.wanderer.repositories.PinRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class FutureTripServiceTest {

    @Mock
    private PinService pinService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private FutureTripRepository futureTripRepository;

    @Mock
    private PinRepository pinRepository;

    @InjectMocks
    FutureTripService futureTripService;
    private String email;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSaveFutureTrip() {

        // Arrange
        FutureTrip futureTrip = new FutureTrip();
        Pin pin = new Pin();
        pin.setPinId(1);
        futureTrip.setPin(pin);
        futureTrip.setTripName("dummytripname");
        futureTrip.setTripId(1);
        futureTrip.setTripDescription("dummytripdesctiption");

        when(pinService.getPinById(pin.getPinId())).thenReturn(pin);
        when(futureTripRepository.save(futureTrip)).thenReturn(futureTrip);

        // Act
        FutureTrip savedFutureTrip = futureTripService.saveFutureTrip(futureTrip);

        // Assert
        assertNotNull(savedFutureTrip, "Future Trip saved successfully");
        assertSame(futureTrip, savedFutureTrip);

        verify(pinService, times(1)).getPinById(pin.getPinId());
        verify(futureTripRepository, times(1)).save(futureTrip);
    }

    @Test
    public void testFetchFutureTrip() {

        // Arrange
        String email = "dummy@gmail.com";

        FutureTrip futureTrip1 = new FutureTrip();
        futureTrip1.setTripName("dummytripname1");
        futureTrip1.setTripId(1);
        futureTrip1.setTripDescription("dummytripdesctiption1");

        FutureTrip futureTrip2 = new FutureTrip();
        futureTrip2.setTripName("dummytripname2");
        futureTrip2.setTripId(2);
        futureTrip2.setTripDescription("dummytripdesctiption1");

        List<FutureTrip> futureTrips = new ArrayList<>();
        futureTrips.add(futureTrip1);
        futureTrips.add(futureTrip2);

        User user = new User();
        user.setEmailId(email);
        user.setFirstName("DummyName");
        user.setLastName("DummyLastName");
        user.setId(1);

        when(userProfileService.fetchByEmail(email)).thenReturn(user);
        when(futureTripRepository.findByUserId(user.getId())).thenReturn(futureTrips);

        // Act
        List<FutureTrip> futureTripsResponse = futureTripService.fetchFutureTripsByUserId(email);

        // Assert
        assertNotNull(futureTripsResponse, "Future Trips are not null");
        assertSame(futureTrips, futureTripsResponse);

        verify(userProfileService, times(1)).fetchByEmail(email);
        verify(futureTripRepository, times(1)).findByUserId(user.getId());
    }

    @Test
    public void testUpdateFutureTrip() {

        // Arrange
        int futureTripId = 1;

        FutureTrip futureTrip = new FutureTrip();
        futureTrip.setTripName("futureTripName");
        futureTrip.setTripDescription("futureTripDescription");

        FutureTrip updatedFutureTrip = new FutureTrip();
        updatedFutureTrip.setTripName("updatedFutureTripName");
        updatedFutureTrip.setTripDescription("updatedFutureTripDescription");

        when(futureTripRepository.getById(futureTripId)).thenReturn(futureTrip);
        when(futureTripRepository.save(updatedFutureTrip)).thenReturn(updatedFutureTrip);

        // Act
        FutureTrip updatedFutureTripResponse = futureTripService.updateFutureTrip(futureTripId, updatedFutureTrip);

        // Assert
        assertNotNull(updatedFutureTripResponse, "Future List Updated is Not Null");
        assertSame(updatedFutureTrip, updatedFutureTripResponse);

        verify(futureTripRepository, times(1)).getById(futureTripId);
        verify(futureTripRepository, times(1)).save(updatedFutureTrip);
    }

    @Test
    public void testFetchFutureTripsByPinId() {

        // Arrange
        int pinId = 1;

        FutureTrip futureTrip1 = new FutureTrip();

        Pin pin = new Pin();
        pin.setPinId(1);

        futureTrip1.setTripName("dummytripname1");
        futureTrip1.setTripId(1);
        futureTrip1.setTripDescription("dummytripdesctiption1");
        futureTrip1.setPin(pin);

        FutureTrip futureTrip2 = new FutureTrip();
        futureTrip2.setTripName("dummytripname2");
        futureTrip2.setTripId(2);
        futureTrip2.setTripDescription("dummytripdesctiption1");
        futureTrip2.setPin(pin);

        List<FutureTrip> futureTrips = new ArrayList<>();
        futureTrips.add(futureTrip1);
        futureTrips.add(futureTrip2);

        when(futureTripRepository.findByPinPinId(pinId)).thenReturn(futureTrips);

        // Act
        List<FutureTrip> futureTripsResponse = futureTripService.fetchFutureTripsByPinId(pinId);

        // Assert
        assertSame(futureTrips, futureTripsResponse);
        assertNotNull(futureTripsResponse);
        verify(futureTripRepository, times(1)).findByPinPinId(pinId);
    }

    @Test
    public void testDeleteFutureTrip() {

        // Arrange
        int futureTripId = 1;
        doNothing().when(futureTripRepository).deleteById(futureTripId);

        // Act
        futureTripService.deleteFutureTrip(futureTripId);

        // Assert
        verify(futureTripRepository).deleteById(futureTripId);
    }
}