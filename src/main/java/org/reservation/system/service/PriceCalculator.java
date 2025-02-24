package org.reservation.system.service;

import org.reservation.system.model.PricingInfo;
import org.reservation.system.model.Route;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;

public class PriceCalculator {

    private static final Currency DEFAULT_CURRENCY = Currency.getInstance("LKR");
    /**
     * Calculates the total ticket price for a given route and number of passengers.
     *
     * @param route      The bus route for which the price is being calculated.
     * @param prices     A map containing ticket prices for different routes.
     * @param passengers The number of passengers.
     * @return The total price for all passengers.
     */
    public static PricingInfo calculatePrice(Route route, Map<String, BigDecimal> prices, int passengers) {
        String key = route.getOrigin() + "-" + route.getDestination();
        if (route.isReturnRoute()) {
            key = route.getDestination() + "-" + route.getOrigin();
        }

        BigDecimal pricePerPassenger = prices.getOrDefault(key, BigDecimal.ZERO);

        // Calculate total price
        BigDecimal totalPrice = pricePerPassenger.multiply(BigDecimal.valueOf(passengers));

        return new PricingInfo(pricePerPassenger, totalPrice, DEFAULT_CURRENCY);

    }
}