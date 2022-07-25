package ca.dal.cs.wanderer.exception.category;

import ca.dal.cs.wanderer.util.ErrorMessages;
import org.springframework.http.HttpStatus;

public class FutureTripNotSaved extends RuntimeException{
    private final String message;
    private final HttpStatus status;
    public FutureTripNotSaved(ErrorMessages messages)
    {
        this.message=messages.getErrorMessage();
        this.status=messages.getHttpStatus();
    }
}
