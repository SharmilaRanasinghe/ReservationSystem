package org.reservation.system.service;

import org.reservation.system.exception.RouteNotFoundException;
import org.reservation.system.exception.NotEnoughSeatException;
import org.reservation.system.model.*;
import org.reservation.system.validator.RequestValidator;
import org.reservation.system.model.request.AvailabilityRequest;
import org.reservation.system.model.request.ReservationRequest;
import org.reservation.system.model.response.AvailabilityResponse;
import org.reservation.system.model.response.ReservationResponse;
import org.reservation.system.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class BusReservationService implements ReservationService {
    private static final Logger logger = LoggerFactory.getLogger(BusReservationService.class);
    private final Bus bus;
    private final ConcurrentHashMap<LocalDate, ConcurrentHashMap<String, AtomicBoolean>> bookedSeats = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<LocalDate, ConcurrentHashMap<String, AtomicBoolean>> bookedReturnSeats = new ConcurrentHashMap<>();
    private final List<Reservation> reservations = new ArrayList<>();

    public BusReservationService(Bus bus) {
        this.bus = bus;
    }

    /**
     * Checks seat availability for a given route and date.
     *
     * @param request Availability request containing route details and passenger count
     * @return AvailabilityResponse with availability status and total price if available
     */
    @Override
    public AvailabilityResponse checkAvailability(AvailabilityRequest request) {
        logger.info("Checking availability for bus reservation");
        Route route = validateRoute(request.getOrigin(), request.getDestination());
        int passengerCount = request.getPassengerCount();

        RequestValidator.validateAvailabilityRequest(request);

        LocalDate travelDate = DateUtils.toLocalDate(request.getTravelDate());
        boolean isAvailable = getAvailableSeats(route, travelDate, passengerCount).size() >= passengerCount;
        logger.info("Availability - {}", isAvailable);
        if (isAvailable) {
            PricingInfo price = PriceCalculator.calculatePrice(route, Bus.getTicketPriceList(), passengerCount);
            return new AvailabilityResponse.Builder().seatAvailability(isAvailable).pricingInfo(price).build();
        }
        return new AvailabilityResponse.Builder().seatAvailability(isAvailable).build();
    }

    /**
     * Reserves tickets for a given route and date.
     *
     * @param request Reservation request containing passenger details
     * @return ReservationResponse containing reservation details
     */
    @Override
    public ReservationResponse reserveTicket(ReservationRequest request) {
        logger.info("Reserving ticket for bus reservation");
        RequestValidator.validateReservationRequest(request);
        Route route = validateRoute(request.getOrigin(), request.getDestination());
        LocalDate travelDate = DateUtils.toLocalDate(request.getTravelDate());

        List<String> seats = reserveSeats(route, travelDate, request.getPassengerCount());

        logger.info("Reserved seats - {}", seats);
        LocalDateTime departureTime = TimeDurationCalculator.getEstimatedDepartureTime(route, travelDate);
        LocalDateTime arrivalTime = TimeDurationCalculator.getEstimatedArrivalTime(departureTime, route);

        Reservation reservation = new Reservation(route, bus, seats, request.getPaymentAmount(), departureTime, arrivalTime);
        reservations.add(reservation);

        logger.info("Reserved reservation id- {}", reservation.getReservationId());
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

    /**
     * Validates if a given origin and destination exist in the bus routes.
     *
     * @param origin Origin location
     * @param destination Destination location
     * @return Validated Route object
     * @throws RouteNotFoundException if route does not exist
     */
    private Route validateRoute(String origin, String destination) {
        Route route = bus.getRoute(origin, destination);
        if (route == null) {
            throw new RouteNotFoundException("Invalid origin or destination");
        }
        return route;
    }

    /**
     * Reserves the required number of seats for a route on a specific date.
     *
     * @param route Route information
     * @param travelDate Date of travel
     * @param passengers Number of passengers
     * @return List of allocated seat numbers
     * @throws NotEnoughSeatException if not enough seats are available
     */
    private List<String> reserveSeats(Route route, LocalDate travelDate, int passengers) {
    ConcurrentHashMap<String, AtomicBoolean> seatMap = getSeatMap(route, travelDate);
    List<String> reservedSeats = new ArrayList<>();

    synchronized (seatMap) { // Lock seatMap to avoid concurrent modifications
        for (Seat seat : bus.getSeats()) {
            if (reservedSeats.size() >= passengers) {
                break;
            }
            seatMap.computeIfAbsent(seat.getSeatNumber(), key -> new AtomicBoolean(false));
            if (!seatMap.get(seat.getSeatNumber()).get()) {
                seatMap.get(seat.getSeatNumber()).set(true); // Mark as booked
                reservedSeats.add(seat.getSeatNumber());
            }
        }
    }

    if (reservedSeats.size() < passengers) {
        throw new NotEnoughSeatException("Not enough seats available for " + travelDate);
    }
    return reservedSeats;
}


    /**
     * Retrieves available seats for a given route and date.
     *
     * @param route Route information
     * @param travelDate Date of travel
     * @param passengers Number of passengers
     * @return List of available seat numbers
     */
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
        logger.info("Available seats - {}", availableSeats);
        return availableSeats;
    }

    /**
     * Retrieves the seat map for a given route and date.
     *
     * @param route Route information
     * @param travelDate Date of travel
     * @return Seat map with seat numbers and their availability status
     */
    private ConcurrentHashMap<String, AtomicBoolean> getSeatMap(Route route, LocalDate travelDate) {
        ConcurrentHashMap<LocalDate, ConcurrentHashMap<String, AtomicBoolean>> seatsMap =
                route.isReturnRoute() ? bookedReturnSeats : bookedSeats;
        return seatsMap.computeIfAbsent(travelDate, k -> new ConcurrentHashMap<>());
    }
}
