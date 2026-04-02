package com.megaminds.govisaviya.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

/**
 * Custom exception for handling external service errors.
 * This exception is thrown when an error occurs while calling an external service.
 */

@Getter
public class ExternalServiceException extends RuntimeException {
    private final HttpStatus status;

    /**
     * Constructor for ExternalServiceException.
     * @param message The error message.
     * @param cause The cause of the exception.
     */
    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.BAD_GATEWAY;
    }

    /**
     * Constructor for ExternalServiceException.
     * @param message The error message.
     * @param status The HTTP status code.
     * @param cause The cause of the exception.
     */
    public ExternalServiceException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
}
