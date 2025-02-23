package org.reservation.system.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = AvailabilityResponse.Builder.class)
public class AvailabilityResponse {
    private final boolean isAvailable;
    private final BigDecimal totalPrice;

    private AvailabilityResponse(Builder builder) {
        this.isAvailable = builder.isAvailable;
        this.totalPrice = builder.basePrice;
    }

    // Getters
    public boolean isAvailable() { return isAvailable; }

    public BigDecimal getTotalPrice() { return totalPrice; }

    public static class Builder {
        private boolean isAvailable;

        private BigDecimal basePrice;

        @JsonProperty("isAvailable")
        public Builder isAvailable(boolean isAvailable) {
            this.isAvailable = isAvailable;
            return this;
        }

        @JsonProperty("totalPrice")
        public Builder totalPrice(BigDecimal basePrice) {
            this.basePrice = basePrice;
            return this;
        }

        public AvailabilityResponse build() {
            return new AvailabilityResponse(this);
        }
    }
}