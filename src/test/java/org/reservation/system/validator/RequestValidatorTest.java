package org.reservation.system.validator;

import org.junit.jupiter.api.Test;
import org.reservation.system.exception.RequestValidationException;
import org.reservation.system.model.request.AvailabilityRequest;
import org.reservation.system.model.request.ReservationRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.reservation.system.Constant.DefaultConfig.MAX_RESERVATION_DAYS;
import static org.reservation.system.Constant.DefaultConfig.MIN_RESERVATION_DAYS;

class RequestValidatorTest {

    private static final String VALID_ORIGIN = "New York";
    private static final String VALID_DESTINATION = "Los Angeles";
    private static final int VALID_PASSENGER_COUNT = 2;
    private static final String VALID_TRAVEL_DATE = LocalDate.now().plusDays(2).format(DateTimeFormatter.ISO_DATE);
    private static final BigDecimal VALID_PAYMENT_AMOUNT = BigDecimal.valueOf(100.00);

    @Test
    void validateReservationRequest_ShouldPassForValidRequest() {
        ReservationRequest request = new ReservationRequest.Builder()
                .origin(VALID_ORIGIN)
                .destination(VALID_DESTINATION)
                .passengerCount(VALID_PASSENGER_COUNT)
                .travelDate(VALID_TRAVEL_DATE)
                .paymentAmount(VALID_PAYMENT_AMOUNT)
                .build();

        assertDoesNotThrow(() -> RequestValidator.validateReservationRequest(request));
    }

    @Test
    void validateReservationRequest_ShouldThrowExceptionForNegativePayment() {
        ReservationRequest request = new ReservationRequest.Builder()
                .origin(VALID_ORIGIN)
                .destination(VALID_DESTINATION)
                .passengerCount(VALID_PASSENGER_COUNT)
                .travelDate(VALID_TRAVEL_DATE)
                .paymentAmount(BigDecimal.valueOf(-50))
                .build();

        Exception exception = assertThrows(RequestValidationException.class, () ->
                RequestValidator.validateReservationRequest(request));

        assertEquals("Paid amount must be a non-negative value.", exception.getMessage());
    }

    @Test
    void validateReservationRequest_ShouldThrowExceptionForNullPayment() {
        ReservationRequest request = new ReservationRequest.Builder()
                .origin(VALID_ORIGIN)
                .destination(VALID_DESTINATION)
                .passengerCount(VALID_PASSENGER_COUNT)
                .travelDate(VALID_TRAVEL_DATE)
                .paymentAmount(null)
                .build();

        Exception exception = assertThrows(RequestValidationException.class, () ->
                RequestValidator.validateReservationRequest(request));

        assertEquals("Paid amount must be a non-negative value.", exception.getMessage());
    }

    @Test
    void validateAvailabilityRequest_ShouldPassForValidRequest() {
        AvailabilityRequest request = new AvailabilityRequest.Builder()
                .origin(VALID_ORIGIN)
                .destination(VALID_DESTINATION)
                .passengerCount(VALID_PASSENGER_COUNT)
                .travelDate(VALID_TRAVEL_DATE)
                .build();

        assertDoesNotThrow(() -> RequestValidator.validateAvailabilityRequest(request));
    }

    @Test
    void validateCommonFields_ShouldThrowExceptionForBlankOrigin() {
        AvailabilityRequest request = new AvailabilityRequest.Builder()
                .origin("")
                .destination(VALID_DESTINATION)
                .passengerCount(VALID_PASSENGER_COUNT)
                .travelDate(VALID_TRAVEL_DATE)
                .build();

        Exception exception = assertThrows(RequestValidationException.class, () ->
                RequestValidator.validateAvailabilityRequest(request));

        assertEquals("Mandatory parameter origin is not specified.", exception.getMessage());
    }

    @Test
    void validateCommonFields_ShouldThrowExceptionForBlankDestination() {
        AvailabilityRequest request = new AvailabilityRequest.Builder()
                .origin(VALID_ORIGIN)
                .destination("")
                .passengerCount(VALID_PASSENGER_COUNT)
                .travelDate(VALID_TRAVEL_DATE)
                .build();

        Exception exception = assertThrows(RequestValidationException.class, () ->
                RequestValidator.validateAvailabilityRequest(request));

        assertEquals("Mandatory parameter destination is not specified.", exception.getMessage());
    }

    @Test
    void validateCommonFields_ShouldThrowExceptionForZeroPassengers() {
        AvailabilityRequest request = new AvailabilityRequest.Builder()
                .origin(VALID_ORIGIN)
                .destination(VALID_DESTINATION)
                .passengerCount(0)
                .travelDate(VALID_TRAVEL_DATE)
                .build();

        Exception exception = assertThrows(RequestValidationException.class, () ->
                RequestValidator.validateAvailabilityRequest(request));

        assertEquals("Passenger count must be greater than zero. Provided: 0", exception.getMessage());
    }

    @Test
    void validateTravelDate_ShouldThrowExceptionForNullDate() {
        Exception exception = assertThrows(RequestValidationException.class, () ->
                RequestValidator.validateAvailabilityRequest(
                        new AvailabilityRequest.Builder()
                                .origin(VALID_ORIGIN)
                                .destination(VALID_DESTINATION)
                                .passengerCount(VALID_PASSENGER_COUNT)
                                .travelDate(null)
                                .build()
                ));

        assertEquals("Mandatory parameter travelDate is not specified.", exception.getMessage());
    }

    @Test
    void validateTravelDate_ShouldThrowExceptionForOutOfRangeDate() {
        String invalidDate = LocalDate.now().plusDays(10).format(DateTimeFormatter.ISO_DATE);

        Exception exception = assertThrows(RequestValidationException.class, () ->
                RequestValidator.validateAvailabilityRequest(
                        new AvailabilityRequest.Builder()
                                .origin(VALID_ORIGIN)
                                .destination(VALID_DESTINATION)
                                .passengerCount(VALID_PASSENGER_COUNT)
                                .travelDate(invalidDate)
                                .build()
                ));

        LocalDate today = LocalDate.now();
        LocalDate minDate = today.plusDays(MIN_RESERVATION_DAYS);
        LocalDate maxDate = today.plusDays(MAX_RESERVATION_DAYS);
        String expectedMessage = String.format("Reservations are only allowed from %s to %s.", minDate, maxDate);

        assertEquals(expectedMessage, exception.getMessage());
    }
}
