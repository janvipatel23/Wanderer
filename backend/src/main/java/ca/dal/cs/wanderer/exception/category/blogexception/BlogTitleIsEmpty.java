package ca.dal.cs.wanderer.exception.category.blogexception;

import ca.dal.cs.wanderer.util.ErrorMessages;
import org.springframework.http.HttpStatus;

public class BlogTitleIsEmpty extends RuntimeException{

    private final String message;
    private final HttpStatus status;

    public BlogTitleIsEmpty(ErrorMessages messages)
    {
        this.message=messages.getErrorMessage();
        this.status=messages.getHttpStatus();
    }
}
