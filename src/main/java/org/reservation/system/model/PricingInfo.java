package org.reservation.system.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Represents pricing information for a bus route.
 */
public record PricingInfo(@JsonProperty("pricePerPassenger") BigDecimal pricePerPassenger,
                          @JsonProperty("totalPrice") BigDecimal totalPrice,
                          @JsonProperty("currency") Currency currency) {

}
