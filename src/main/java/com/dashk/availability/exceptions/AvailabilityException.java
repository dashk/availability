package com.dashk.availability.exceptions;

/**
 * Basic availability exceptions
 */
public class AvailabilityException extends Exception {
    public AvailabilityException(String message) {
        super(message);
    }

    public AvailabilityException(String message, Exception innerException) {
        super(message, innerException);
    }
}
