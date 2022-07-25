package ca.dal.cs.wanderer.exception;

import ca.dal.cs.wanderer.exception.category.EmailNotFound;
import ca.dal.cs.wanderer.exception.category.InvalidCoordinates;
import ca.dal.cs.wanderer.exception.category.pinexception.PinNotFound;
import ca.dal.cs.wanderer.exception.category.pinexception.PinNotSaved;
import ca.dal.cs.wanderer.exception.category.blogexception.BlogNotFound;
import ca.dal.cs.wanderer.exception.category.blogexception.UnauthorizedBlogAccess;
import ca.dal.cs.wanderer.exception.category.pinexception.UnauthorizedPinAccess;
import ca.dal.cs.wanderer.util.ErrorMessages;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class CustomizedExceptionResponse extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(new Date()
                , ex.getMessage()
                , request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EmailNotFound.class)
    public final ResponseEntity<Object> emailNotFound(EmailNotFound ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
        GenericResponse<ExceptionResponse> genericResponse = new GenericResponse<>(false, ErrorMessages.EMAIL_NOT_FOUND.getHttpStatus().name(), response);
        return handleExceptionInternal(ex, genericResponse, new HttpHeaders(), ErrorMessages.EMAIL_NOT_FOUND.getHttpStatus(), request);
    }

    @ExceptionHandler(PinNotSaved.class)
    public final ResponseEntity<Object> pinNotSaved(PinNotSaved ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
        GenericResponse<ExceptionResponse> genericResponse = new GenericResponse<>(false, ErrorMessages.PIN_NOT_SAVED.getHttpStatus().name(), response);
        return handleExceptionInternal(ex, genericResponse, new HttpHeaders(), ErrorMessages.PIN_NOT_SAVED.getHttpStatus(), request);
    }

    @ExceptionHandler(PinNotFound.class)
    public final ResponseEntity<Object> pinNotFound(PinNotFound ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
        GenericResponse<ExceptionResponse> genericResponse = new GenericResponse<>(false, ErrorMessages.PIN_NOT_FOUND.getHttpStatus().name(), response);
        return handleExceptionInternal(ex, genericResponse, new HttpHeaders(), ErrorMessages.PIN_NOT_FOUND.getHttpStatus(), request);
    }

    @ExceptionHandler(InvalidCoordinates.class)
    public final ResponseEntity<Object> invalidCoordinates(InvalidCoordinates ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
        GenericResponse<ExceptionResponse> genericResponse = new GenericResponse<>(false, ErrorMessages.INVALID_COORDINATES.getHttpStatus().name(), response);
        return handleExceptionInternal(ex, genericResponse, new HttpHeaders(), ErrorMessages.INVALID_COORDINATES.getHttpStatus(), request);
    }

    // write method unauthorizedPinAccess
    @ExceptionHandler(UnauthorizedPinAccess.class)
    public final ResponseEntity<Object> unauthorizedPinAccess(UnauthorizedPinAccess ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
        GenericResponse<ExceptionResponse> genericResponse = new GenericResponse<>(false, ErrorMessages.UNAUTHORIZED_PIN_ACCESS.getHttpStatus().name(), response);
        return handleExceptionInternal(ex, genericResponse, new HttpHeaders(), ErrorMessages.UNAUTHORIZED_PIN_ACCESS.getHttpStatus(), request);
    }

    // write method for BLOG_NOT_FOUND
    @ExceptionHandler(BlogNotFound.class)
    public final ResponseEntity<Object> blogNotFound(BlogNotFound ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
        GenericResponse<ExceptionResponse> genericResponse = new GenericResponse<>(false, ErrorMessages.BLOG_NOT_FOUND.getHttpStatus().name(), response);
        return handleExceptionInternal(ex, genericResponse, new HttpHeaders(), ErrorMessages.BLOG_NOT_FOUND.getHttpStatus(), request);
    }

    // write method for UnauthorizedBlogAccess
    @ExceptionHandler(UnauthorizedBlogAccess.class)
    public final ResponseEntity<Object> unauthorizedBlogAccess(UnauthorizedBlogAccess ex, WebRequest request) {
        ExceptionResponse response = new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
        GenericResponse<ExceptionResponse> genericResponse = new GenericResponse<>(false, ErrorMessages.UNAUTHORIZED_BLOG_ACCESS.getHttpStatus().name(), response);
        return handleExceptionInternal(ex, genericResponse, new HttpHeaders(), ErrorMessages.UNAUTHORIZED_BLOG_ACCESS.getHttpStatus(), request);
    }


}
