package ca.dal.cs.wanderer.services;

import ca.dal.cs.wanderer.exception.category.pinexception.PinNotFound;
import ca.dal.cs.wanderer.exception.category.pinexception.UnauthorizedPinAccess;
import ca.dal.cs.wanderer.models.Pin;
import ca.dal.cs.wanderer.models.PinImage;
import ca.dal.cs.wanderer.models.User;
import ca.dal.cs.wanderer.repositories.PinImageRepo;
import ca.dal.cs.wanderer.repositories.PinRepository;
import ca.dal.cs.wanderer.util.ErrorMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Service
public class PinService {
    @Autowired
    private PinRepository pinRepository;

    @Autowired
    PinUpdateService pinUpdateService;

    @Autowired
    private PinImageRepo pinImageRepo;

    // create or update pin
    // @param pin - pin to be created or updated
    // @param user - user who created the pin
    // @param files - image files to be uploaded
    // @return pin - pin created or updated
    public Pin savePin(Pin pin, MultipartFile[] files, User user) throws Exception {
        Pin newPin;

        // if pinId is null, create new pin
        if (pin.getPinId() == null || pin.getPinId() <= 0) {
            newPin = new Pin(user.getId(), pin.getLocationName(), pin.getLatitude(), pin.getLongitude(), pin.getDescription());
        } else { // else update existing pin
            newPin = getPinById(pin.getPinId());
            if (newPin == null) {
                throw new PinNotFound(ErrorMessages.PIN_NOT_FOUND);
            }

            // check if user is the owner of the pin
            if(!Objects.equals(newPin.getUserId(), user.getId())){
                throw new UnauthorizedPinAccess(ErrorMessages.UNAUTHORIZED_PIN_ACCESS);
            }
            newPin.setLocationName(pin.getLocationName());
            newPin.setLatitude(pin.getLatitude());
            newPin.setLongitude(pin.getLongitude());
            newPin.setDescription(pin.getDescription());

            // delete all images associated with pin before updating
            pinImageRepo.deleteAllByPinId(pin.getPinId());
        }
        List<PinImage> pinImageList = new ArrayList<>();
        if(files != null) {
            // add images to image list
            for (MultipartFile file : files) {
                try {
                    PinImage image = new PinImage(file.getBytes());
                    image.setPin(newPin);
                    pinImageList.add(image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // set image list to pin
        if(pinImageList.size()>0){
            newPin.setPinImages(pinImageList);
        }

        // save pin
        Pin savedPin = pinRepository.save(newPin);

        // send firestore update
        pinUpdateService.sendPinUpdate();
        return savedPin;

    }

    // get pins by radius and coordinates
    // @param centerLat - latitude of center point
    // @param centerLng - longitude of center point
    // @param radius - radius from center point
    // @return pins - pins within radius of center point
    public List<PinRepository.PinBasicInfo> getPinsByRadius(double radius, double centerLat, double centerLng){
        return pinRepository.getPinsByRadius(radius, centerLat, centerLng);
    }

    // get list of pins by pinIds
    // @param pinIds - list of pin ids
    // @return list of pins
    public List<Pin> getPinsByIds(List<Integer> pinIds){
        return pinRepository.findAllById(pinIds);
    }

    // delete pin by id
    // @param pinId - id of pin to be deleted
    public void deletePin(Integer pinId, Integer userId) throws ExecutionException, InterruptedException {
        Pin pin = getPinById(pinId);

        if(pin==null){
            throw new PinNotFound(ErrorMessages.PIN_NOT_FOUND);
        }

        // check if user is the owner of the pin
        if(!Objects.equals(pin.getUserId(), userId)){
            throw new UnauthorizedPinAccess(ErrorMessages.UNAUTHORIZED_PIN_ACCESS);
        }

        // delete pin from database
        pinRepository.deleteById(pin.getPinId());

        // send firestore update
        pinUpdateService.sendPinUpdate();
    }

    // get pin by id
    // @param pinId - id of pin to be retrieved
    // @return pin - pin with id as pinId
    public Pin getPinById(int pinId) {
        return pinRepository.findById(pinId).orElse(null);
    }
}
