package org.reservation.system.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = AvailabilityRequest.Builder.class)
public class AvailabilityRequest {
    private final String origin;
    private final String destination;
    private final int passengerCount;
    private final String travelDate;

    public AvailabilityRequest(Builder builder) {
        this.origin = builder.origin;
        this.destination = builder.destination;
        this.passengerCount = builder.passengerCount;
        this.travelDate = builder.travelDate;
    }

    // Getters
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public int getPassengerCount() { return passengerCount; }
    public String getTravelDate() { return travelDate; }

    public static class Builder {
        private String origin;
        private String destination;
        private int passengerCount;
        private String travelDate;

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


        @JsonProperty("passengerCount")
        public Builder passengerCount(int passengerCount) {
            this.passengerCount = passengerCount;
            return this;
        }

        @JsonProperty("travelDate")
        public Builder travelDate(String travelDate) {
            this.travelDate = travelDate;
            return this;
        }

        public AvailabilityRequest build() {
            return new AvailabilityRequest(this);
        }
    }
}
