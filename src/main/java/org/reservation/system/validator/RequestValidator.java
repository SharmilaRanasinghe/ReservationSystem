package org.reservation.system.validator;

import org.apache.commons.lang3.StringUtils;
import org.reservation.system.exception.RequestValidationException;
import org.reservation.system.model.request.AvailabilityRequest;
import org.reservation.system.model.request.ReservationRequest;
import org.reservation.system.util.DateUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.reservation.system.Constant.DefaultConfig.MAX_RESERVATION_DAYS;
import static org.reservation.system.Constant.DefaultConfig.MIN_RESERVATION_DAYS;

public class RequestValidator {

    public static void validateReservationRequest(ReservationRequest request) {
        validateCommonFields(request.getOrigin(), request.getDestination(), request.getPassengerCount(), request.getTravelDate());

        if (request.getPaymentAmount() == null || request.getPaymentAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new RequestValidationException("Paid amount must be a non-negative value.");
        }
    }

    public static void validateAvailabilityRequest(AvailabilityRequest request) {
        validateCommonFields(request.getOrigin(), request.getDestination(), request.getPassengerCount(), request.getTravelDate());
    }

    private static void validateCommonFields(String origin, String destination, int passengerCount, String travelDate) {
        if (StringUtils.isEmpty(origin)) {
            throw new RequestValidationException("Mandatory parameter origin is not specified.");
        }
        if (StringUtils.isEmpty(destination)) {
            throw new RequestValidationException("Mandatory parameter destination is not specified.");
        }
        if (passengerCount <= 0) {
            throw new RequestValidationException("Passenger count must be greater than zero. Provided: " + passengerCount);
        }

        validateTravelDate(travelDate);
    }

    private static void validateTravelDate(String travelDate) {
        if (StringUtils.isEmpty(travelDate)) {
            throw new RequestValidationException("Mandatory parameter travelDate is not specified.");
        }


        LocalDate localDate = DateUtils.toLocalDate(travelDate);
        LocalDate today = LocalDate.now();
        LocalDate reservationStartDate = today.plusDays(MIN_RESERVATION_DAYS);
        LocalDate reservationEndDate = today.plusDays(MAX_RESERVATION_DAYS);

        if (localDate.isBefore(reservationStartDate) || localDate.isAfter(reservationEndDate)) {
            String message = String.format("Reservations are only allowed from %s to %s.", reservationStartDate, reservationEndDate);
            throw new RequestValidationException(message);
        }
    }

}
