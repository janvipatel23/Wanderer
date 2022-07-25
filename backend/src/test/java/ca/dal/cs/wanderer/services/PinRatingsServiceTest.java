package ca.dal.cs.wanderer.services;

import ca.dal.cs.wanderer.models.Pin;
import ca.dal.cs.wanderer.models.PinComment;
import ca.dal.cs.wanderer.models.PinRating;
import ca.dal.cs.wanderer.models.User;
import ca.dal.cs.wanderer.repositories.PinRatingRepository;
import ca.dal.cs.wanderer.repositories.PinRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

class PinRatingsServiceTest {

    @Mock
    private PinRatingRepository pinRatingRepository;

    @Mock
    private PinRepository pinRepository;

    @InjectMocks
    private PinRatingsService pinRatingsService;

    private User user;
    private Pin pin1;
    private Pin pin2;
    List<PinRating> pinRatingList = new ArrayList<>();
    PinRating pinRatingObject1 = new PinRating();
    PinRating pinRatingObject2 = new PinRating();
    private Integer pinRating1 = 4;
    private Integer pinRating2 = 3;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        user = getUser();

        pinRatingObject1.setRating(pinRating1);
        pinRatingObject2.setRating(pinRating2);

        pin1 = getPin1Details();
        pin2 = getPin2Details();

        pinRatingList.add(pinRatingObject1);
        pinRatingList.add(pinRatingObject2);

    }

    private User getUser() {
        User user = new User();
        user.setFirstName("Test First name");
        user.setLastName("Test last name");
        user.setId(1);
        user.setEmailId("test@gmail.com");
        return user;
    }

    private Pin getPin1Details() {
        Pin pin = new Pin(1, "Halifax", 10.0, 10.0, "Test description");
        pin.setPinId(1);
        return pin;
    }

    private Pin getPin2Details() {
        Pin pin = new Pin(1, "Truro", 20.0, 20.0, "Test2 description");
        pin.setPinId(2);
        return pin;
    }

    @Test
    void getRatingsTest() {

        when(pinRatingRepository.findAllByPinId(pin1.getPinId())).thenReturn(pinRatingList);
        when(pinRatingRepository.findAllByPinId(pin2.getPinId())).thenReturn(pinRatingList);

        List<PinRating> pinRatings1 = pinRatingsService.getRatings(pin1.getPinId());
        List<PinRating> pinRatings2 = pinRatingsService.getRatings(pin2.getPinId());

        assertSame(pinRatingList, pinRatings1);
        assertSame(pinRatingList, pinRatings2);

        assertNotNull(pinRatings1);
        assertNotNull(pinRatings2);

        verify(pinRatingRepository, times(1)).findAllByPinId(pin1.getPinId());
        verify(pinRatingRepository, times(1)).findAllByPinId(pin2.getPinId());
    }

    @Test
    void getRatingsByIDTest() {

        when(pinRatingRepository.findPinRatingByUserIdAndPinId(user.getId(), pin1.getPinId())).thenReturn(pinRatingObject1);

        PinRating pinRatings = pinRatingsService.getRatingsByID(user.getId(), pin1.getPinId());

        assertSame(pinRatingObject1, pinRatings);
        assertNotNull(pinRatings);
        verify(pinRatingRepository, times(1)).findPinRatingByUserIdAndPinId(user.getId(), pin1.getPinId());
    }
}