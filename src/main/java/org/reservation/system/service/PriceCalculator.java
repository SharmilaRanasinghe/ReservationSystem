package org.reservation.system.service;

import org.reservation.system.model.Route;

import java.math.BigDecimal;
import java.util.Map;

public class PriceCalculator {

    public static BigDecimal calculatePrice(Route route, Map<String, BigDecimal> prices, int passengers) {
        String key = route.getOrigin() + "-" + route.getDestination();
        if (!route.isReturnRoute()) {
            return prices.getOrDefault(key, BigDecimal.ZERO).multiply(BigDecimal.valueOf(passengers)) ;
        } else {
            key = route.getDestination() + "-" + route.getOrigin();
            return prices.getOrDefault(key, BigDecimal.ZERO).multiply(BigDecimal.valueOf(passengers)) ;
        }
    }
}