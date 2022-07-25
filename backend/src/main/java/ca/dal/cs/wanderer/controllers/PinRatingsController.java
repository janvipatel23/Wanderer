package ca.dal.cs.wanderer.controllers;

import ca.dal.cs.wanderer.exception.GenericResponse;
import ca.dal.cs.wanderer.exception.category.EmailNotFound;
import ca.dal.cs.wanderer.exception.category.PrincipalNotFound;
import ca.dal.cs.wanderer.exception.category.pinexception.InvalidPinId;
import ca.dal.cs.wanderer.exception.category.pinexception.InvalidPinRating;
import ca.dal.cs.wanderer.exception.category.pinexception.PinNotFound;
import ca.dal.cs.wanderer.models.Pin;
import ca.dal.cs.wanderer.models.PinRating;
import ca.dal.cs.wanderer.models.User;
import ca.dal.cs.wanderer.services.PinRatingsService;
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
@RequestMapping("/api/v1/pinRating")
public class PinRatingsController {

    @Autowired
    private PinRatingsService pinRatingsService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private PinService pinService;

    //Get all the ratings for a pin
    @GetMapping("/getRatings")
    public ResponseEntity<GenericResponse<List<PinRating>>> getRatings(@AuthenticationPrincipal OidcUser principal, @RequestParam Integer pinId) {

        getUserId(principal);
        if (pinId <= 0) {
            throw new InvalidPinId(ErrorMessages.INVALID_PIN_ID);
        }
        List<PinRating> ratings = pinRatingsService.getRatings(pinId);
        return ResponseEntity.ok(new GenericResponse<>(true, SuccessMessages.PIN_RATING_RETRIEVE_SUCCESS.getSuccessMessage(), ratings));
    }

    //Get all the ratings for a pin of a user
    @GetMapping("/getRatingsById")
    public ResponseEntity<GenericResponse<PinRating>> getRatingById(@AuthenticationPrincipal OidcUser principal, @RequestParam Integer pinId) {

        Integer userId = getUserId(principal);

        if (pinId <= 0) {
            throw new InvalidPinId(ErrorMessages.INVALID_PIN_ID);
        }
        PinRating rating = pinRatingsService.getRatingsByID(userId, pinId);

        return ResponseEntity.ok(new GenericResponse<>(true, SuccessMessages.PIN_RATING_RETRIEVE_SUCCESS.getSuccessMessage(), rating));
    }

    //Add user ratings for a pin
    @PostMapping("/addRating")
    public ResponseEntity<GenericResponse<List<PinRating>>> addRating(@AuthenticationPrincipal OidcUser principal, @RequestParam Integer pinId,
                                                        @RequestParam Integer pinRating) {

        Integer userId = getUserId(principal);

        if (pinId <= 0) {
            throw new InvalidPinId(ErrorMessages.INVALID_PIN_ID);
        }

        Pin pin = pinService.getPinById(pinId);
        if (pin == null) {
            throw new PinNotFound(ErrorMessages.PIN_NOT_FOUND);
        }

        if (pinRating <= 0) {
            throw new InvalidPinRating(ErrorMessages.INVALID_PIN_RATING);
        }

        List<PinRating> rating = pinRatingsService.addRatings(userId, pin, pinRating);
        return ResponseEntity.ok(new GenericResponse<>(true, SuccessMessages.PIN_RATING_SUCCESS.getSuccessMessage(), rating));
    }

    private Integer getUserId(OidcUser principal) {
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
        return user.getId();
    }
}
