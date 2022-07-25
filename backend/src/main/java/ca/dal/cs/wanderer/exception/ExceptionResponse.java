package ca.dal.cs.wanderer.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class ExceptionResponse {
    private final Date timeStamp;
    private final String message;
    private final String description;
}
