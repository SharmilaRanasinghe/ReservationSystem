package org.reservation.system.service;

import org.reservation.system.exception.RouteNotFoundException;
import org.reservation.system.exception.NotEnoughSeatException;
import org.reservation.system.validator.RequestValidator;
import org.reservation.system.model.Bus;
import org.reservation.system.model.Reservation;
import org.reservation.system.model.Route;
import org.reservation.system.model.Seat;
import org.reservation.system.model.request.AvailabilityRequest;
import org.reservation.system.model.request.ReservationRequest;
import org.reservation.system.model.response.AvailabilityResponse;
import org.reservation.system.model.response.ReservationResponse;
import org.reservation.system.util.DateUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class BusReservationService implements ReservationService {
    private final Bus bus;
    private final ConcurrentHashMap<LocalDate, ConcurrentHashMap<String, AtomicBoolean>> bookedSeats = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<LocalDate, ConcurrentHashMap<String, AtomicBoolean>> bookedReturnSeats = new ConcurrentHashMap<>();

    private final List<Reservation> reservations = new ArrayList<>();

    public BusReservationService(Bus bus) {
        this.bus = bus;
    }

    @Override
    public AvailabilityResponse checkAvailability(AvailabilityRequest request) {

        Route route = validateRoute(request.getOrigin(), request.getDestination());
        int passengerCount = request.getPassengerCount();

        RequestValidator.validateAvailabilityRequest(request);

        LocalDate travelDate = DateUtils.toLocalDate(request.getTravelDate());
        boolean isAvailable = getAvailableSeats(route, travelDate, passengerCount).size() >= passengerCount;
        if (isAvailable) {
            BigDecimal price = PriceCalculator.calculatePrice(route, Bus.getTicketPriceList(), passengerCount);
            return new AvailabilityResponse.Builder().isAvailable(isAvailable).totalPrice(price).build();
        }


        return new AvailabilityResponse.Builder().isAvailable(isAvailable).build();
    }

    @Override
    public ReservationResponse reserveTicket(ReservationRequest request) {
        RequestValidator.validateReservationRequest(request);
        Route route = validateRoute(request.getOrigin(), request.getDestination());
        LocalDate travelDate = DateUtils.toLocalDate(request.getTravelDate());

        List<String> seats = reserveSeats(route, travelDate, request.getPassengerCount());

        LocalDateTime departureTime = TimeDurationCalculator.getEstimatedDepartureTime(route, travelDate);
        LocalDateTime arrivalTime = TimeDurationCalculator.getEstimatedArrivalTime(departureTime, route);

        Reservation reservation = new Reservation(route, bus, seats, request.getPaymentAmount(), departureTime, arrivalTime);
        reservations.add(reservation);

        return new ReservationResponse.Builder()
                .reservationId(reservation.getReservationId())
                .origin(request.getOrigin())
                .destination(request.getDestination())
                .allocatedSeatNumbers(seats)
                .totalPrice(request.getPaymentAmount())
                .departureTime(departureTime.toString())
                .arrivalTime(arrivalTime.toString())
                .build();
    }

    private Route validateRoute(String origin, String destination) {
        Route route = bus.getRoute(origin, destination);
        if (route == null) {
            throw new RouteNotFoundException("Incorrect origin or destination");
        }
        return route;
    }

    private List<String> reserveSeats(Route route, LocalDate travelDate, int passengers) {

        List<String> reservedSeats = getAvailableSeats(route, travelDate, passengers);
        ConcurrentHashMap<String, AtomicBoolean> seatMap = getSeatMap(route, travelDate);

        if (reservedSeats.size() < passengers) {
            throw new NotEnoughSeatException("Not enough seats available for " + travelDate);
        }

        // Mark seats as reserved
        reservedSeats.forEach(seat -> seatMap.computeIfPresent(seat, (key, value) -> { value.set(true); return value; }));

        return reservedSeats;
    }

    private List<String> getAvailableSeats(Route route, LocalDate travelDate, int passengers) {
        ConcurrentHashMap<String, AtomicBoolean> seatMap = getSeatMap(route, travelDate);
        List<String> availableSeats = new ArrayList<>();

        for (Seat seat : bus.getSeats()) {
            if (availableSeats.size() >= passengers) {
                break;
            }
            seatMap.computeIfAbsent(seat.getSeatNumber(), key -> new AtomicBoolean(false));
            if (!seatMap.get(seat.getSeatNumber()).get()) {
                availableSeats.add(seat.getSeatNumber());
            }
        }
        return availableSeats;
    }

    private ConcurrentHashMap<String, AtomicBoolean> getSeatMap(Route route, LocalDate travelDate) {
        ConcurrentHashMap<LocalDate, ConcurrentHashMap<String, AtomicBoolean>> seatsMap =
                route.isReturnRoute() ? bookedReturnSeats : bookedSeats;
        return seatsMap.computeIfAbsent(travelDate, k->new ConcurrentHashMap<>());
    }
}
