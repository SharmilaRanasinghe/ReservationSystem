package org.reservation.system.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a reservation for a bus journey.
 * Contains details about the route, bus, reserved seats, price, and status.
 */
public class Reservation {
    private final String reservationId;
    private final Route route;
    private final Bus bus;
    private final List<String> reservedSeats;
    private final BigDecimal totalPrice;
    private ReservationStatus status;
    private final LocalDateTime departureTime;
    private final LocalDateTime arrivalTime;

    /**
     * Constructs a new reservation.
     *
     * @param route          The route of the bus journey.
     * @param bus            The bus associated with the reservation.
     * @param reservedSeats  The list of reserved seat numbers.
     * @param price          The total price of the reservation.
     * @param departureTime  The departure time of the journey.
     * @param arrivalTime    The estimated arrival time of the journey.
     */
    public Reservation(Route route, Bus bus, List<String> reservedSeats, BigDecimal price, LocalDateTime departureTime, LocalDateTime arrivalTime) {
        this.reservationId = UUID.randomUUID().toString();
        this.route = route;
        this.bus = bus;
        this.reservedSeats = reservedSeats;
        this.totalPrice = price;
        this.status = ReservationStatus.CONFIRMED;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    /**
     * Returns the unique reservation ID.
     *
     * @return The reservation ID as a string.
     */
    public String getReservationId() {
        return reservationId;
    }

    /**
     * Returns the route associated with the reservation.
     *
     * @return The route object.
     */
    public Route getRoute() {
        return route;
    }

    /**
     * Returns the bus associated with the reservation.
     *
     * @return The bus object.
     */
    public Bus getBus() {
        return bus;
    }

    /**
     * Returns a list of reserved seat numbers.
     *
     * @return A copy of the list of reserved seat numbers.
     */
    public List<String> getReservedSeats() {
        return new ArrayList<>(reservedSeats);
    }

    /**
     * Returns the total price of the reservation.
     *
     * @return The total price as a BigDecimal.
     */
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    /**
     * Returns the current status of the reservation.
     *
     * @return The reservation status (CONFIRMED or CANCELLED).
     */
    public ReservationStatus getStatus() {
        return status;
    }

    /**
     * Returns the departure time of the journey.
     *
     * @return The departure time as a LocalDateTime object.
     */
    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    /**
     * Returns the estimated arrival time of the journey.
     *
     * @return The arrival time as a LocalDateTime object.
     */
    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    /**
     * Enumeration representing the status of a reservation.
     */
    public enum ReservationStatus {
        CONFIRMED, CANCELLED
    }
}
