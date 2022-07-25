package ca.dal.cs.wanderer.services;

import ca.dal.cs.wanderer.models.Pin;
import ca.dal.cs.wanderer.models.PinComment;
import ca.dal.cs.wanderer.models.User;
import ca.dal.cs.wanderer.repositories.PinCommentRepository;
import ca.dal.cs.wanderer.repositories.PinRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

class PinCommentsServiceTest {

    @Mock
    private PinCommentRepository pinCommentRepository;

    @Mock
    private PinRepository pinRepository;

    @Mock
    private Pin pin;

    @InjectMocks
    private PinCommentsService pinCommentsService;

    private User user;
    private Pin pin1;
    private Pin pin2;
    List<PinComment> pinCommentList = new ArrayList<>();
    PinComment pinCommentObject1 = new PinComment();
    PinComment pinCommentObject2 = new PinComment();
    PinComment pinCommentObject3 = new PinComment();
    private String comment1 = "Test Comment1";
    private String comment2 = "Test Comment2";
    private String comment3 = "Test Comment3";

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        user = getUser();
        pin1 = getPin1Details();
        pin2 = getPin2Details();

        pinCommentObject1.setComment(comment1);
        pinCommentObject2.setComment(comment2);
        pinCommentObject3.setComment(comment3);

        pinCommentObject1.setPin(pin1);
        pinCommentObject2.setPin(pin1);
        pinCommentObject3.setPin(pin2);

        pinCommentList.add(pinCommentObject1);
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
    void getCommentsTest() {
        when(pinCommentRepository.findAllByPinId(pin1.getPinId())).thenReturn(pinCommentList);

        List<PinComment> pinComments = pinCommentsService.getComments(pin1.getPinId());

        assertSame(pinCommentList, pinComments);
        assertNotNull(pinComments);
        verify(pinCommentRepository, times(1)).findAllByPinId(pin1.getPinId());
    }
}