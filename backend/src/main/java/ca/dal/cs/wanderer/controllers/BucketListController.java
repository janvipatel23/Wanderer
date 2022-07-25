package ca.dal.cs.wanderer.controllers;

import ca.dal.cs.wanderer.exception.GenericResponse;
import ca.dal.cs.wanderer.exception.category.EmailNotFound;
import ca.dal.cs.wanderer.exception.category.pinexception.InvalidPinId;
import ca.dal.cs.wanderer.exception.category.pinexception.PinNotFound;
import ca.dal.cs.wanderer.exception.category.PrincipalNotFound;
import ca.dal.cs.wanderer.models.BucketListPin;
import ca.dal.cs.wanderer.models.Pin;
import ca.dal.cs.wanderer.models.User;
import ca.dal.cs.wanderer.services.BucketListService;
import ca.dal.cs.wanderer.services.PinService;
import ca.dal.cs.wanderer.services.UserProfileService;
import ca.dal.cs.wanderer.util.ErrorMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bucketList")
public class BucketListController {

    @Autowired
    BucketListService bucketListService;

    @Autowired
    UserProfileService userProfileService;

    @Autowired
    PinService pinService;

    //Adding pin to bucket list
    @PostMapping("/addPinToBucketList")
    public ResponseEntity<GenericResponse<Boolean>> insertPin(@AuthenticationPrincipal OidcUser principal, @RequestParam Integer pinId) throws Exception {
        User user = getUser(principal);

        if (pinId <= 0) {
            throw new Exception("Invalid Pin ID");
        }

        Pin pin = pinService.getPinById(pinId);

        if (pin == null) {
            throw new PinNotFound(ErrorMessages.PIN_NOT_FOUND);
        }

        Boolean result = bucketListService.insertToBucketList(user, pin);

        GenericResponse<Boolean> listGenericResponse = new GenericResponse<>(true, "Pin added to Bucket List successfully", result);
        return new ResponseEntity<>(listGenericResponse, HttpStatus.OK);
    }

    //Deleting pin from bucket list
    @DeleteMapping("/deletePinFromBucketList")
    public ResponseEntity<GenericResponse<Boolean>> deletePin(@AuthenticationPrincipal OidcUser principal, @RequestParam Integer pinId) throws Exception {
        User user = getUser(principal);

        if (pinId <= 0) {
            throw new InvalidPinId(ErrorMessages.INVALID_PIN_ID);
        }

        bucketListService.deleteFromBucketList(pinId,user.getId());
        GenericResponse<Boolean> listGenericResponse = new GenericResponse<>(true, "Pin deleted from Bucket List successfully", true);
        return new ResponseEntity<>(listGenericResponse, HttpStatus.OK);
    }

     //Getting list of all the pins for a particular user
    @GetMapping("/listOfAllPins")
    public ResponseEntity<GenericResponse<List<BucketListPin>>> listOfAllPins(@AuthenticationPrincipal OidcUser principal) throws Exception {
        User user = getUser(principal);
        List<BucketListPin> pinList= bucketListService.allPins(user.getId());

        GenericResponse<List<BucketListPin>> listGenericResponse = new GenericResponse<>(true, "Fetched all pins for a user successfully",pinList);
        return new ResponseEntity<>(listGenericResponse, HttpStatus.OK);
    }

    // check if pin is in bucket list
    @GetMapping("/checkIfPinInBucketList")
    public ResponseEntity<GenericResponse<Boolean>> checkIfPinInBucketList(@AuthenticationPrincipal OidcUser principal, @RequestParam Integer pinId) throws Exception {
        User user = getUser(principal);

        if (pinId <= 0) {
            throw new InvalidPinId(ErrorMessages.INVALID_PIN_ID);
        }

        Boolean result = bucketListService.pinExists(pinId,user.getId());

        GenericResponse<Boolean> listGenericResponse = new GenericResponse<>(true, "Checked if pin is in Bucket List successfully", result);
        return new ResponseEntity<>(listGenericResponse, HttpStatus.OK);
    }

    private User getUser(OidcUser principal) {
        if (principal == null) {
            throw new PrincipalNotFound(ErrorMessages.PRINCIPAL_NOT_FOUND);
        }
        String email = principal.getEmail();
        System.out.println(email);
        if (email == null) {
            throw new EmailNotFound(ErrorMessages.EMAIL_NOT_FOUND);
        }
        return userProfileService.fetchByEmail(email);
    }

}
