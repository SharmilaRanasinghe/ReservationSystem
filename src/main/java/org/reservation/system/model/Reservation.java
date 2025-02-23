package org.reservation.system.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Reservation {
    private final String reservationId;
    private final Route route;
    private final Bus bus;
    private final List<String> reservedSeats;
    private final BigDecimal price;
    private ReservationStatus status;
    private final LocalDateTime departureTime;
    private final LocalDateTime arrivalTime;

    public Reservation(Route route, Bus bus, List<String> reservedSeats, BigDecimal price, LocalDateTime departureTime, LocalDateTime arrivalTime) {
        this.reservationId = UUID.randomUUID().toString();
        this.route = route;
        this.bus = bus;
        this.reservedSeats = reservedSeats;
        this.price = price;
        this.status = ReservationStatus.CONFIRMED;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

//    public void cancel() {
//        this.status = ReservationStatus.CANCELLED;
//        for (String seat : reservedSeats) {
//            seat.release();
//        }
//    }

    // Getters
    public String getReservationId() { return reservationId; }
    public Route getRoute() { return route; }
    public Bus getBus() { return bus; }
    public List<String> getReservedSeats() { return new ArrayList<>(reservedSeats); }
    public BigDecimal getPrice() { return price; }
    public ReservationStatus getStatus() { return status; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }

    public enum ReservationStatus {
        CONFIRMED, CANCELLED
    }
}