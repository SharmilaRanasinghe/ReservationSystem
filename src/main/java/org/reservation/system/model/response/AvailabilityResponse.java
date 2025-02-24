package org.reservation.system.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.reservation.system.model.PricingInfo;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = AvailabilityResponse.Builder.class)
public class AvailabilityResponse {
    private final boolean seatAvailability;
    private final PricingInfo pricingInfo;

    private AvailabilityResponse(Builder builder) {
        this.seatAvailability = builder.seatAvailability;
        this.pricingInfo = builder.pricingInfo;
    }

    // Getters
    public boolean isSeatAvailability() { return seatAvailability; }

    public PricingInfo getPricingInfo() { return pricingInfo; }

    public static class Builder {
        private boolean seatAvailability;

        private PricingInfo pricingInfo;

        @JsonProperty("seatAvailability")
        public Builder seatAvailability(boolean seatAvailability) {
            this.seatAvailability = seatAvailability;
            return this;
        }

        @JsonProperty("PricingInfo")
        public Builder pricingInfo(PricingInfo pricingInfo) {
            this.pricingInfo = pricingInfo;
            return this;
        }

        public AvailabilityResponse build() {
            return new AvailabilityResponse(this);
        }
    }
}