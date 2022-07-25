package ca.dal.cs.wanderer.controllers;

import ca.dal.cs.wanderer.exception.GenericResponse;
import ca.dal.cs.wanderer.exception.category.EmailNotFound;
import ca.dal.cs.wanderer.exception.category.PrincipalNotFound;
import ca.dal.cs.wanderer.exception.category.pinexception.InvalidCoordinates;
import ca.dal.cs.wanderer.exception.category.pinexception.InvalidPinId;
import ca.dal.cs.wanderer.exception.category.pinexception.PinNotFound;
import ca.dal.cs.wanderer.exception.category.pinexception.PinNotSaved;
import ca.dal.cs.wanderer.models.Pin;
import ca.dal.cs.wanderer.models.User;
import ca.dal.cs.wanderer.repositories.PinRepository;
import ca.dal.cs.wanderer.services.PinService;
import ca.dal.cs.wanderer.services.UserProfileService;
import ca.dal.cs.wanderer.util.ErrorMessages;
import ca.dal.cs.wanderer.util.SuccessMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pin")
public class PinController {

    @Autowired
    private PinService pinService;

    @Autowired
    private UserProfileService userProfileService;

    private final static int MAX_LOCATION_NAME_LENGTH = 255;
    private final static int MAX_DESCRIPTION_LENGTH = 1000;
    private final static int MIN_LATITUDE = -90;
    private final static int MAX_LATITUDE = 90;
    private final static int MIN_LONGITUDE = -180;
    private final static int MAX_LONGITUDE = 180;

    // method to create or update a pin
    // @param pinId - if null, create a new pin, else update the pin with the given pinId
    // @param files - image files
    // @return - created or updated pin
    @PostMapping("/createPin")
    public ResponseEntity<GenericResponse<Pin>> createPin(@AuthenticationPrincipal OidcUser principal, @RequestPart(value = "pin") Pin pin,
                                                          @RequestPart(value = "images", required = false) MultipartFile[] files) throws Exception {
        // get authenticated user
        User authenticatedUser = getUser(principal);

        if(pin!=null){
            System.out.println("Pin Location Name: " + pin.getLocationName());
        }
        if(pin==null){
            throw new PinNotFound(ErrorMessages.PIN_NOT_FOUND);
        }

        // validate pin information
        if(checkInvalidString(pin.getLocationName(), MAX_LOCATION_NAME_LENGTH)){
            throw new Exception(ErrorMessages.INVALID_PIN_LOCATION_NAME.getErrorMessage());
        }
        if(checkInvalidString(pin.getDescription(), MAX_DESCRIPTION_LENGTH)){
            throw new Exception(ErrorMessages.INVALID_PIN_DESCRIPTION.getErrorMessage());
        }

        // validate latitude and longitude
        if(checkInvalidCoordinates(pin.getLatitude(), pin.getLongitude())){
            throw new InvalidCoordinates(ErrorMessages.INVALID_COORDINATES);
        }

        Pin savedPin = pinService.savePin(pin, files, authenticatedUser);

        if(savedPin==null){
            throw new PinNotSaved(ErrorMessages.PIN_NOT_SAVED);
        }
        GenericResponse<Pin> pinGenericResponse = new GenericResponse<>(true, SuccessMessages.PIN_SAVE_SUCCESS.getSuccessMessage(), savedPin);
        return new ResponseEntity<>(pinGenericResponse, HttpStatus.OK);
    }

    // method to get pins based on the given radius and coordinates
    // @param radius - radius of the circle
    // @param latitude - latitude of the center of the circle
    // @param longitude - longitude of the center of the circle
    // @return - list of pins based on the given radius and coordinates
    @GetMapping("/getPinsByRadius")
    public ResponseEntity<GenericResponse<List<PinRepository.PinBasicInfo>>> getPinsByRadius(@AuthenticationPrincipal OidcUser principal, @RequestParam Double radius, @RequestParam Double centerLat, @RequestParam Double centerLng) throws Exception {
        System.out.println(radius + " "+ centerLat + " "+ centerLng);

        // confirm authenticated user
        getUser(principal);

        // validate radius
        if(radius<0){
            throw new Exception(ErrorMessages.INVALID_PIN_RADIUS.getErrorMessage());
        }

        // validate latitude and longitude
        if(checkInvalidCoordinates(centerLat, centerLng)){
            throw new InvalidCoordinates(ErrorMessages.INVALID_COORDINATES);
        }

        List<PinRepository.PinBasicInfo> pinList = pinService.getPinsByRadius(radius, centerLat, centerLng);

        if(pinList==null){
            throw new PinNotFound(ErrorMessages.PIN_NOT_FOUND);
        }

        GenericResponse<List<PinRepository.PinBasicInfo>> listGenericResponse = new GenericResponse<>(true, SuccessMessages.PINS_RETRIEVE_SUCCESS.getSuccessMessage(), pinList);
        return new ResponseEntity<>(listGenericResponse, HttpStatus.OK);
    }

    // method to get pins based on the given pinIds
    // @param pinIds - list of pinIds
    // @return - list of pins
    @GetMapping("/getPinsByIds")
    public ResponseEntity<GenericResponse<List<Pin>>> getPinsByIds(@AuthenticationPrincipal OidcUser principal, @RequestParam Integer[] pinIds){

        System.out.println(principal);

        // confirm authenticated user
        getUser(principal);

        List<Pin> pinList = pinService.getPinsByIds(Arrays.asList(pinIds));

        if(pinList==null){
            throw new PinNotFound(ErrorMessages.PIN_NOT_FOUND);
        }

        GenericResponse<List<Pin>> pinGenericResponse = new GenericResponse<>(true, SuccessMessages.PINS_RETRIEVE_SUCCESS.getSuccessMessage(), pinList);
        return new ResponseEntity<>(pinGenericResponse, HttpStatus.OK);
    }

    // get pin by id
    // @param pinId - pin id
    // @return - pin
    @GetMapping("/getPinById")
    public ResponseEntity<GenericResponse<Pin>> getSinglePinById(@AuthenticationPrincipal OidcUser principal, @RequestParam Integer pinId) throws Exception {
        System.out.println("Pin ID: " + pinId);

        // confirm authenticated user
        getUser(principal);

        // validate pin id
        if(pinId<=0){
            throw new Exception(ErrorMessages.INVALID_PIN_ID.getErrorMessage());
        }

        Pin pin = pinService.getPinById(pinId);
        if(pin==null){
            throw new PinNotFound(ErrorMessages.PIN_NOT_FOUND);
        }

        GenericResponse<Pin> pinGenericResponse = new GenericResponse<>(true, SuccessMessages.SINGLE_PIN_RETRIEVE_SUCCESS.getSuccessMessage(), pin);
        return new ResponseEntity<>(pinGenericResponse, HttpStatus.OK);
    }

    // delete pin by id
    // @param pinId
    // @return true if pin is deleted, else custom exception that pin is not found
    @DeleteMapping("/deletePinById")
    public ResponseEntity<GenericResponse<Boolean>> deletePinById(@AuthenticationPrincipal OidcUser principal, @RequestParam Integer pinId) throws Exception {
        System.out.println("Pin ID: " + pinId);

        // get authenticated user
        User authenticatedUser = getUser(principal);

        // validate pin id
        if(pinId<=0){
            throw new InvalidPinId(ErrorMessages.INVALID_PIN_ID);
        }

        // get id of authenticated user
        Integer userId = authenticatedUser.getId();

        // delete pin with given pin id and user id
        pinService.deletePin(pinId, userId);

        GenericResponse<Boolean> pinGenericResponse = new GenericResponse<>(true, SuccessMessages.PIN_DELETE_SUCCESS.getSuccessMessage(), true);
        return new ResponseEntity<>(pinGenericResponse, HttpStatus.OK);
    }

    // check if string is not empty and is within the maximum length
    private boolean checkInvalidString(String str, int maxLength){
        return str==null || str.trim().isEmpty() || str.length()>maxLength;
    }

    // check if latitude and longitude are valid
    private boolean checkInvalidCoordinates(double latitude, double longitude){
        return latitude<MIN_LATITUDE || latitude>MAX_LATITUDE || longitude<MIN_LONGITUDE || longitude>MAX_LONGITUDE;
    }

    // get authenticated user
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
