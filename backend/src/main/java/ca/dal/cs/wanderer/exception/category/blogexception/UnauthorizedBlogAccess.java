package ca.dal.cs.wanderer.exception.category.blogexception;

import ca.dal.cs.wanderer.util.ErrorMessages;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UnauthorizedBlogAccess extends RuntimeException{
    private final String message;
    private final HttpStatus status;
    public UnauthorizedBlogAccess(ErrorMessages messages)
    {
        this.message=messages.getErrorMessage();
        this.status=messages.getHttpStatus();
    }
}
