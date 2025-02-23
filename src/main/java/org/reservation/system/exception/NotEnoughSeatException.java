package org.reservation.system.exception;

public class NotEnoughSeatException extends RuntimeException {
    public NotEnoughSeatException(String message) {
        super(message);
    }
}
