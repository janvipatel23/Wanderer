package ca.dal.cs.wanderer.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class GenericResponse<T> implements Serializable {

    private static final long serialVersionUID = -4132255065389144491L;
    private final Boolean isSuccessful;
    private final String message;
    @Setter
    private transient T payload;

    public GenericResponse(Boolean isSuccessful, String message) {
        this.isSuccessful = isSuccessful;
        this.message = message;
    }


}
