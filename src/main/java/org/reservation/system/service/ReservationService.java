package org.reservation.system.service;


import org.reservation.system.model.request.AvailabilityRequest;
import org.reservation.system.model.request.ReservationRequest;
import org.reservation.system.model.response.AvailabilityResponse;
import org.reservation.system.model.response.ReservationResponse;

public interface ReservationService {
    AvailabilityResponse checkAvailability(AvailabilityRequest availabilityRequest);
    ReservationResponse reserveTicket(ReservationRequest request);
}

