package ca.dal.cs.wanderer.services;

import ca.dal.cs.wanderer.models.BucketListPin;
import ca.dal.cs.wanderer.models.Pin;
import ca.dal.cs.wanderer.models.User;
import ca.dal.cs.wanderer.repositories.BucketListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;


class BucketListServiceTest {

    @Mock
    BucketListRepository bucketListRepository;

    @InjectMocks
    BucketListService bucketListService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void deleteFromBucketList(){
        Integer pinId =1;
        Integer userId=1;

        doNothing().when(bucketListRepository).deleteByPinIdAndUserId(pinId,userId);

        bucketListService.deleteFromBucketList(pinId,userId);

        verify(bucketListRepository, times(1)).deleteByPinIdAndUserId(pinId,userId);
    }

    @Test
    public void allPins(){
        String email ="dummy@gmail.com";

        BucketListPin bucketListPin1= new BucketListPin();
        bucketListPin1.setBucketListId(1);

        BucketListPin bucketListPin2=new BucketListPin();
        bucketListPin2.setBucketListId(2);

        List<BucketListPin> BucketListPins = new ArrayList<>();

        BucketListPins.add(bucketListPin1);
        BucketListPins.add(bucketListPin2);

        User user = new User();
        user.setEmailId(email);
        user.setFirstName("DummyName");
        user.setLastName("DummyLastName");
        user.setId(1);

        when(bucketListRepository.findAllByUser(user.getId())).thenReturn(BucketListPins);

        List<BucketListPin> bucketListResponse = bucketListService.allPins(user.getId());

        assertNotNull(bucketListResponse, "BucketList pins are not null");

        verify(bucketListRepository, times(1)).findAllByUser(user.getId());

    }

    @Test
    public void pinExists(){
        String email ="dummy@gmail.com";
        User user = new User();
        user.setEmailId(email);
        user.setFirstName("DummyName");
        user.setLastName("DummyLastName");
        user.setId(1);

        Pin pin = new Pin();
        pin.setPinId(1);
        pin.setLocationName("Halifax");
        pin.setUserId(1);

        BucketListPin bucketListPin = new BucketListPin();
        bucketListPin.setBucketListId(1);

        when(bucketListRepository.findByPinIdAndUserId(pin.getPinId(), user.getId())).thenReturn(bucketListPin);

        boolean bucketListResponse1 = bucketListService.pinExists(pin.getPinId(), user.getId());

        assertNotNull(bucketListResponse1, "BucketList pins are not null");

        verify(bucketListRepository, times(1)).findByPinIdAndUserId(pin.getPinId(), user.getId());
    }
}