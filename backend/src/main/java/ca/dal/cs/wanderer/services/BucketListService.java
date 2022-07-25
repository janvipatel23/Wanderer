package ca.dal.cs.wanderer.services;
import ca.dal.cs.wanderer.models.BucketListPin;
import ca.dal.cs.wanderer.models.Pin;
import ca.dal.cs.wanderer.models.User;
import ca.dal.cs.wanderer.repositories.BucketListRepository;
import ca.dal.cs.wanderer.repositories.PinRepository;
import ca.dal.cs.wanderer.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BucketListService {

   @Autowired
   private BucketListRepository bucketListRepository;

   @Autowired
   private PinRepository pinRepository;

   @Autowired
   private UserRepository userRepository;

   //saving pin to bucket list
   public boolean insertToBucketList(User user, Pin pin) throws Exception {
      BucketListPin existingEntry = bucketListRepository.findByPinIdAndUserId(pin.getPinId(), user.getId());
      if (existingEntry != null) {
         throw new Exception("Pin already exists in bucket list");
      }
      List<BucketListPin> bucketListPins = new ArrayList<>();
      BucketListPin bucketListPin = new BucketListPin(user, pin);
      bucketListPins.add(bucketListPin);
      pin.setBucketListPins(bucketListPins);
      Pin savedPin = pinRepository.save(pin);
      user.setBucketListPins(bucketListPins);
      User savedUser = userRepository.save(user);
      if(savedPin == null || savedUser == null) {
         throw new Exception("Error saving pin to bucket list");
      }
      return true;

   }

   //deleting the pin
   public void deleteFromBucketList(Integer pinId, Integer userId) {
      bucketListRepository.deleteByPinIdAndUserId(pinId, userId);
   }

   //list of all pins
   public List<BucketListPin> allPins(Integer userId) {
      return bucketListRepository.findAllByUser(userId);
   }

   // check if pin exists in bucket list
   public boolean pinExists(Integer pinId, Integer userId) {
      BucketListPin bucketListPin = bucketListRepository.findByPinIdAndUserId(pinId, userId);
      return bucketListPin != null;
   }
}
