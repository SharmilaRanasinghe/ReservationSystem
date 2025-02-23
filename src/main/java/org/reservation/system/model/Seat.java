package org.reservation.system.model;

public class Seat {
    private final String seatNumber;
    private boolean isAvailable;

    public Seat(String seatNumber) {
        this.seatNumber = seatNumber;
        this.isAvailable = true;
    }

    public void reserve() {
        this.isAvailable = false;
    }

    public void release() {
        this.isAvailable = true;
    }

    // Getters
    public String getSeatNumber() { return seatNumber; }
    public boolean isAvailable() { return isAvailable; }
}