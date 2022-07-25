package ca.dal.cs.wanderer.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorMessages {
    //add your fields here
    EMAIL_NOT_FOUND("Email can not be found, please check the principal", HttpStatus.FORBIDDEN),
    PRINCIPAL_NOT_FOUND("Principal not found, please login", HttpStatus.FORBIDDEN),
    PIN_NOT_SAVED("Error while saving Pin",HttpStatus.INTERNAL_SERVER_ERROR),
    PIN_NOT_FOUND("Unable to retrieve Pins", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_COORDINATES("Invalid Center Coordinates provided", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED_PIN_ACCESS("Unauthorized access to Pin", HttpStatus.FORBIDDEN),
    INVALID_PIN_ID("Invalid Pin Id provided", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_PIN_LOCATION_NAME("Invalid Pin Location Name provided", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_PIN_COORDINATES("Invalid Pin Location Coordinates provided", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_PIN_DESCRIPTION("Invalid Pin Location Description provided", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_PIN_RADIUS("Invalid Map Radius provided", HttpStatus.INTERNAL_SERVER_ERROR),
    RATING_NOT_FOUND("Rating not found", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_PIN_RATING("Invalid Pin Rating", HttpStatus.INTERNAL_SERVER_ERROR),
    COMMENT_NOT_FOUND("Comment not found", HttpStatus.INTERNAL_SERVER_ERROR),
    PINID_NOT_FOUND("Pin Id not found in the request", HttpStatus.BAD_REQUEST),
    USERID_NOT_FOUND("User Id not found in the request", HttpStatus.BAD_REQUEST),
    FUTURETRIP_NOT_SAVED("Future Trip not saved", HttpStatus.INTERNAL_SERVER_ERROR),
    BLOG_TITLE_NOT_FOUND("Blog Title is Empty", HttpStatus.INTERNAL_SERVER_ERROR),
    BLOG_NOT_FOUND("Unable to retrieve blogs", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_BLOG_ID("Invalid Blog ID", HttpStatus.INTERNAL_SERVER_ERROR),
    BLOG_DESCRIPTION_NOT_FOUND("Blog Description is Empty", HttpStatus.INTERNAL_SERVER_ERROR),
    BLOG_AUTHOR_NOT_FOUND("Blog Author is Empty", HttpStatus.INTERNAL_SERVER_ERROR),
    IMAGE_NOT_FOUND("Image is Empty", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED_BLOG_ACCESS("Unauthorized access to Blog", HttpStatus.FORBIDDEN);

    //below should be always fixed
    private final String errorMessage;
    private final HttpStatus httpStatus;
}
