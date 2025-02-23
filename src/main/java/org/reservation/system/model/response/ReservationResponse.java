package org.reservation.system.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = ReservationResponse.Builder.class)
public class ReservationResponse {
    private final String reservationId;
    private final String origin;
    private final String destination;
    private final List<String> allocatedSeatNumbers;
    private final BigDecimal totalPrice;
    private final String departureTime;
    private final String arrivalTime;

    private ReservationResponse(Builder builder) {
        this.reservationId = builder.reservationId;
        this.origin = builder.origin;
        this.destination = builder.destination;
        this.allocatedSeatNumbers = builder.allocatedSeatNumbers;
        this.totalPrice = builder.totalPrice;
        this.departureTime = builder.departureTime;
        this.arrivalTime = builder.arrivalTime;
    }

    // Getters
    public String getReservationId() { return reservationId; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public List<String> getAllocatedSeatNumbers() { return allocatedSeatNumbers; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalTime() { return arrivalTime; }

    // Builder class
    public static class Builder {
        private String reservationId;
        private String origin;
        private String destination;
        private List<String> allocatedSeatNumbers;
        private BigDecimal totalPrice;
        private String departureTime;
        private String arrivalTime;

        @JsonProperty("reservationId")
        public Builder reservationId(String ticketNumber) {
            this.reservationId = ticketNumber;
            return this;
        }

        @JsonProperty("origin")
        public Builder origin(String origin) {
            this.origin = origin;
            return this;
        }

        @JsonProperty("destination")
        public Builder destination(String destination) {
            this.destination = destination;
            return this;
        }

        @JsonProperty("allocatedSeatNumbers")
        public Builder allocatedSeatNumbers(List<String> allocatedSeatNumbers) {
            this.allocatedSeatNumbers = allocatedSeatNumbers;
            return this;
        }

        @JsonProperty("totalPrice")
        public Builder totalPrice(BigDecimal totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        @JsonProperty("departureTime")
        public Builder departureTime(String departureTime) {
            this.departureTime = departureTime;
            return this;
        }

        @JsonProperty("arrivalTime")
        public Builder arrivalTime(String arrivalTime) {
            this.arrivalTime = arrivalTime;
            return this;
        }

        public ReservationResponse build() {
            return new ReservationResponse(this);
        }
    }

    // Static factory method for easier use
    public static Builder builder() {
        return new Builder();
    }
}
