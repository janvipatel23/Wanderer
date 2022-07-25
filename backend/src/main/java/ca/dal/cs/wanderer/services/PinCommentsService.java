package ca.dal.cs.wanderer.services;

import ca.dal.cs.wanderer.models.Pin;
import ca.dal.cs.wanderer.models.PinComment;
import ca.dal.cs.wanderer.models.User;
import ca.dal.cs.wanderer.repositories.PinCommentRepository;
import ca.dal.cs.wanderer.repositories.PinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PinCommentsService {

    @Autowired
    PinCommentRepository pinCommentRepository;

    @Autowired
    PinRepository pinRepository;

    //Get comments by pinID
    public List<PinComment> getComments(Integer pinId) {
        return pinCommentRepository.findAllByPinId(pinId);
    }

    //Add comment and the save in the parent object(Pin)
    public PinComment addComment(User user, Pin pin, String comment) {
        PinComment pinComment = new PinComment(user, comment, java.time.LocalDate.now());
        pinComment.setPin(pin);
        pin.addPinComment(pinComment);
        pinRepository.save(pin);
        return pinComment;
    }
}
