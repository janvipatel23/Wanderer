package ca.dal.cs.wanderer.controllers;

import ca.dal.cs.wanderer.exception.GenericResponse;
import ca.dal.cs.wanderer.exception.category.EmailNotFound;
import ca.dal.cs.wanderer.exception.category.PrincipalNotFound;
import ca.dal.cs.wanderer.exception.category.pinexception.CommentNotFound;
import ca.dal.cs.wanderer.exception.category.pinexception.InvalidPinId;
import ca.dal.cs.wanderer.exception.category.pinexception.PinNotFound;
import ca.dal.cs.wanderer.models.Pin;
import ca.dal.cs.wanderer.models.PinComment;
import ca.dal.cs.wanderer.models.User;
import ca.dal.cs.wanderer.services.PinCommentsService;
import ca.dal.cs.wanderer.services.PinService;
import ca.dal.cs.wanderer.services.UserProfileService;
import ca.dal.cs.wanderer.util.ErrorMessages;
import ca.dal.cs.wanderer.util.SuccessMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pin")
public class PinCommentsController {

    @Autowired
    private PinCommentsService pinCommentsService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private PinService pinService;

    //Get all the comments for a particular pinId
    @GetMapping("/getComments")
    public ResponseEntity<GenericResponse<List<PinComment>>> getComments(@AuthenticationPrincipal OidcUser principal, @RequestParam Integer pinId) {

        getUser(principal);
        if (pinId <= 0) {
            throw new InvalidPinId(ErrorMessages.INVALID_PIN_ID);
        }
        List<PinComment> comments = pinCommentsService.getComments(pinId);

        return ResponseEntity.ok(new GenericResponse<>(true, SuccessMessages.COMMENTS_RETRIEVE_SUCCESS.getSuccessMessage(), comments));
    }

    //Add comments for a particular pin
    @PostMapping("/addComments")
    public ResponseEntity<GenericResponse<PinComment>> addComments(@AuthenticationPrincipal OidcUser principal, @RequestBody String comment, @RequestParam Integer pinId) {

        User user = getUser(principal);

        if (pinId <= 0) {
            throw new InvalidPinId(ErrorMessages.INVALID_PIN_ID);
        }

        Pin pin = pinService.getPinById(pinId);
        if (pin == null) {
            throw new PinNotFound(ErrorMessages.PIN_NOT_FOUND);
        }

        if(comment == null) {
            throw new CommentNotFound(ErrorMessages.COMMENT_NOT_FOUND);
        }

        PinComment pinComment = pinCommentsService.addComment(user, pin, comment);
        return ResponseEntity.ok(new GenericResponse<>(true, SuccessMessages.COMMENT_ADD_SUCCESS.getSuccessMessage(), pinComment));
    }

    private User getUser(OidcUser principal) {
        if (principal == null) {
            throw new PrincipalNotFound(ErrorMessages.PRINCIPAL_NOT_FOUND);
        }
        String email = principal.getEmail();
        if (email == null) {
            throw new EmailNotFound(ErrorMessages.EMAIL_NOT_FOUND);
        }

        User user = userProfileService.fetchByEmail(email);
        if(user==null) {
            throw new PrincipalNotFound(ErrorMessages.PRINCIPAL_NOT_FOUND);
        }
        return user;
    }
}
