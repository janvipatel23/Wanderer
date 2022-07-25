package ca.dal.cs.wanderer.controllers;

import ca.dal.cs.wanderer.exception.GenericResponse;
import ca.dal.cs.wanderer.exception.category.EmailNotFound;
import ca.dal.cs.wanderer.exception.category.PrincipalNotFound;
import ca.dal.cs.wanderer.models.User;
import ca.dal.cs.wanderer.services.UserProfileService;
import ca.dal.cs.wanderer.util.ErrorMessages;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/wanderer/user")
public class UserProfileController {

    @Autowired
    private UserProfileService service;

    @Autowired
    private ObjectMapper mapper;
    Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    //method to get fetch the details of an individual user
    @GetMapping("/getDetails")
    public ResponseEntity<GenericResponse<JSONObject>> fetchSingle(@AuthenticationPrincipal OidcUser principal) {
        if (principal == null) {
            throw new PrincipalNotFound(ErrorMessages.PRINCIPAL_NOT_FOUND);
        }

        String email = principal.getEmail();

        if (email == null) {
            logger.warn("Empty email is captured");
            throw new EmailNotFound(ErrorMessages.EMAIL_NOT_FOUND);
        }

        String googleProfileImage = principal.getPicture();
        User user = service.fetchByEmail(email);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("firstName", user.getFirstName());
        jsonObject.put("lastName", user.getLastName());
        jsonObject.put("emailId", email);
        jsonObject.put("googlePhotoUrl", googleProfileImage);
        if (user.getImage() != null) {
            String encodedImage = Base64.getEncoder().encodeToString(user.getImage());
            jsonObject.put("image", "data:blogImage/png;base64, " + encodedImage);
        } else {
            jsonObject.put("image", null);
        }

        GenericResponse<JSONObject> jsonObjectGenericResponse = new GenericResponse<>(true, "User Profile details retrieved successfully", jsonObject);

        return new ResponseEntity<>(jsonObjectGenericResponse, HttpStatus.OK);
    }

    //below method enable to update the name and profile image, though its not mandatory fields
    @PutMapping(value = "/updateProfile")
    public ResponseEntity<GenericResponse<User>> updateUser(@AuthenticationPrincipal OidcUser principal,
                                                            @RequestParam(value = "image", required = false) MultipartFile file,
                                                            @RequestParam(value = "firstName", required = false) String fName,
                                                            @RequestParam(value = "lastName", required = false) String lName) throws IOException {
        System.out.println(principal);
        if (principal == null) {
            throw new PrincipalNotFound(ErrorMessages.PRINCIPAL_NOT_FOUND);
        }

        String email = principal.getEmail();

        if (email == null) {
            logger.warn("Empty email is captured");
            throw new EmailNotFound(ErrorMessages.EMAIL_NOT_FOUND);
        }

        User user = service.fetchByEmail(email);

        if (fName == null) {
            logger.info("Fetching first name from Principal");
            fName = principal.getGivenName();
        }
        if (lName == null) {
            logger.info("Fetching last name from Principal");
            lName = principal.getFamilyName();
        }
        User savedUser = service.updateProfile(file, user, fName, lName);

        GenericResponse<User> userGenericResponse = new GenericResponse<>(true, "User Profile Updated Successfully", savedUser);

        return new ResponseEntity<>(userGenericResponse, HttpStatus.OK);
    }

    // Below method returns the user by its id
    @GetMapping("/getUserId")
    public ResponseEntity<GenericResponse<Map<String, Object>>> getUserId(@AuthenticationPrincipal OidcUser principal) {
        if (principal == null) {
            throw new PrincipalNotFound(ErrorMessages.PRINCIPAL_NOT_FOUND);
        }

        String email = principal.getEmail();

        if (email == null) {
            throw new EmailNotFound(ErrorMessages.EMAIL_NOT_FOUND);
        }

        User user = service.fetchByEmail(email);
        Integer userId = user.getId();

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        GenericResponse<Map<String, Object>> userGenericResponse = new GenericResponse<>(true, "User Id retrieved successfully", map);

        return new ResponseEntity<>(userGenericResponse, HttpStatus.OK);
    }

}
