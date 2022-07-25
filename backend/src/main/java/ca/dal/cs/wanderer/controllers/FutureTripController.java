package ca.dal.cs.wanderer.controllers;

import ca.dal.cs.wanderer.dtos.FutureTripRequestDto;
import ca.dal.cs.wanderer.exception.GenericResponse;
import ca.dal.cs.wanderer.exception.category.FutureTripNotSaved;
import ca.dal.cs.wanderer.exception.category.pinexception.PinNotFound;
import ca.dal.cs.wanderer.exception.category.UserNotFound;
import ca.dal.cs.wanderer.models.FutureTrip;
import ca.dal.cs.wanderer.models.User;
import ca.dal.cs.wanderer.services.FutureTripService;
import ca.dal.cs.wanderer.services.UserProfileService;
import ca.dal.cs.wanderer.util.ErrorMessages;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wanderer/futuretrip")
@AllArgsConstructor

// class for managing future trip feature
public class FutureTripController {

    private FutureTripService futureTripService;

    private UserProfileService userProfileService;

    // method for creating future trip
    // @param principal - oauth principal containing user details
    // @param futureTripRequestDto - future trip request dto
    // @return - returns future trip
    @PostMapping("/createFutureTrip")
    public ResponseEntity<GenericResponse<FutureTrip>> createFutureTrip(@AuthenticationPrincipal OidcUser principal, @RequestBody FutureTripRequestDto futureTripRequestDto) {

        if(futureTripRequestDto.getPinId() < 0) {
            throw new PinNotFound(ErrorMessages.PINID_NOT_FOUND);
        }

        // converting request dto into model
        FutureTrip futureTrip = FutureTripRequestDto.convertToModel(futureTripRequestDto);

        // fetching user current logged in user
        User user = userProfileService.fetchByEmail(principal.getEmail());

        // user not found
        if(user.getId() == null) {
            throw new UserNotFound(ErrorMessages.USERID_NOT_FOUND);
        }

        // setting current logged-in user in future trip
        futureTrip.setUser(user);

        FutureTrip savedFutureTrip = futureTripService.saveFutureTrip(futureTrip);

        if(savedFutureTrip == null) {
            throw new FutureTripNotSaved(ErrorMessages.FUTURETRIP_NOT_SAVED);
        }

        GenericResponse<FutureTrip> futureTripGenericResponse = new GenericResponse<>(true, "Future Trip is saved", savedFutureTrip);
        return new ResponseEntity<>(futureTripGenericResponse, HttpStatus.OK);
    }

    // method for fetching future trips by user id
    // @param principal - oauth principal containing user details
    // @return - returns list of future trip
    @GetMapping("/fetchFutureTripsByUserId")
    public ResponseEntity<GenericResponse<List<FutureTrip>>> getFutureTripsByUserId(@AuthenticationPrincipal OidcUser principal) {

        // fetch all future trips of the current logged-in user
        List<FutureTrip> futureTrips = futureTripService.fetchFutureTripsByUserId(principal.getEmail());

        GenericResponse<List<FutureTrip>> futureTripsGenericResponse = new GenericResponse<>(true, "Future Trips for requested user fetched successfully", futureTrips);
        return new ResponseEntity<>(futureTripsGenericResponse, HttpStatus.OK);
    }

    // method for fetching future trips by pin id
    // @param pinId - pinId
    // @return - returns list of future trip
    @GetMapping(value = "/fetchFutureTripsByPinId")
    public ResponseEntity<GenericResponse<List<FutureTrip>>> getFutureTripsByPinId(@RequestParam(value = "pinId") int pinId) throws Exception {

        // fetch all the future trips for given pinId
        List<FutureTrip> futureTrips = futureTripService.fetchFutureTripsByPinId(pinId);

        GenericResponse<List<FutureTrip>> futureTripsGenericResponse = new GenericResponse<>(true, "Future Trips for requested pin id fetched successfully", futureTrips);
        return new ResponseEntity<>(futureTripsGenericResponse, HttpStatus.OK);
    }

    // method for deleting future trip
    // @param futureTripId - future trip id
    // @return - returns the status code
    @DeleteMapping(value = "/deleteFutureTripById")
    public ResponseEntity<GenericResponse<FutureTrip>> deleteFutureTripById(@RequestParam(value = "futureTripId") int futureTripId) {
        // delete future trip by future-trip id
        futureTripService.deleteFutureTrip(futureTripId);
        GenericResponse<FutureTrip> futureTripGenericResponse = new GenericResponse<>(true, "Future Trip is deleted", null);
        return new ResponseEntity<>(futureTripGenericResponse, HttpStatus.OK);
    }

    // method for updating future trip
    // @param futureTripId -  future trip id
    // @param futureTripRequestDto - future trip request dto
    // @return - returns future trip
    @PutMapping(value = "/updateFutureTrip/{futureTripId}")
    public ResponseEntity<GenericResponse<FutureTrip>> updateFutureTripById(@PathVariable(value = "futureTripId") int futureTripId, @RequestBody FutureTripRequestDto futureTripRequestDto) {

        // converting DTO into model
        FutureTrip futureTrip = FutureTripRequestDto.convertToModel(futureTripRequestDto);

        // update the current future trip value
        FutureTrip updatedFutureTrip = futureTripService.updateFutureTrip(futureTripId, futureTrip);

        GenericResponse<FutureTrip> futureTripGenericResponse = new GenericResponse<>(true, "Future Trip is updated", updatedFutureTrip);
        return new ResponseEntity<>(futureTripGenericResponse, HttpStatus.OK);
    }
}
