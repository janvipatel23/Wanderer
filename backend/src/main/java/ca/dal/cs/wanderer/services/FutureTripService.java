package ca.dal.cs.wanderer.services;

import ca.dal.cs.wanderer.models.FutureTrip;
import ca.dal.cs.wanderer.models.User;
import ca.dal.cs.wanderer.repositories.FutureTripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// class for managing future trip service
@Service
public class FutureTripService {

    @Autowired
    private FutureTripRepository futureTripRepository;

    @Autowired
    private PinService pinService;

    @Autowired
    private UserProfileService userProfileService;

    // method for saving the future trip
    // @param futureTrip - the future trip
    // @return - returns future trip
    public FutureTrip saveFutureTrip(FutureTrip futureTrip) {
        futureTrip.setPin(pinService.getPinById(futureTrip.getPin().getPinId()));
        return futureTripRepository.save(futureTrip);
    }

    // method for fetching the future trip by user id
    // @param email - the email
    // @return - returns the list of future trip
    public List<FutureTrip> fetchFutureTripsByUserId(String email) {
        User user = userProfileService.fetchByEmail(email);
        return futureTripRepository.findByUserId(user.getId());
    }

    // method for fetching the future trip by pin id
    // @param pinId - the pinId
    // @return - returns the list of future trip
    public List<FutureTrip> fetchFutureTripsByPinId(int pinId) {
        return futureTripRepository.findByPinPinId(pinId);
    }

    // method for updating the future trip
    // @param futureTripId - the future trip id
    // @param updatedFutureTrip - the updated future trip
    // @return - returns the future trip
    public FutureTrip updateFutureTrip(int futureTripId, FutureTrip updatedFutureTrip) {
        FutureTrip futureTrip = futureTripRepository.getById(futureTripId);
        futureTrip.setTripName(updatedFutureTrip.getTripName());
        futureTrip.setTripDescription(updatedFutureTrip.getTripDescription());
        futureTrip.setTripDate(updatedFutureTrip.getTripDate());
        return futureTripRepository.save(futureTrip);
    }

    // method for deleting the future trip
    // @param futureTripId - the future trip id
    public void deleteFutureTrip(int futureTripId) {
        futureTripRepository.deleteById(futureTripId);
    }
}
