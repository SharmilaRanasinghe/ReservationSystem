package org.reservation.system.service;

import org.reservation.system.model.Bus;
import org.reservation.system.model.Route;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TimeDurationCalculator {

    public static LocalDateTime getEstimatedDepartureTime(Route route, LocalDate travelDate) {
        LocalDateTime baseDepartureTime;
        String key;
        String baseOrigin;
        if (!route.isReturnRoute()) {
            baseDepartureTime = travelDate.atTime(Bus.getBaseDepartureTime(), 0);
            baseOrigin = Bus.getBusStops()[0];
            key = baseOrigin + "-" + route.getOrigin();

        } else {
            baseDepartureTime = travelDate.atTime(Bus.getBaseReturnDepartureTime(), 0);
            baseOrigin = getBaseReturnOrigin();
            key = route.getOrigin() + "-" +  baseOrigin;
        }
        if (baseOrigin.equals(route.getOrigin())) {
            return baseDepartureTime;
        }
        int duration = Bus.getTravelDurations().getOrDefault(key, 0);
        return baseDepartureTime.plusMinutes(duration) ;
    }

    public static LocalDateTime getEstimatedArrivalTime(LocalDateTime departureTime, Route route) {
        String routeKey = route.getOrigin() + "-" + route.getDestination();
        if (route.isReturnRoute()) {
            routeKey = route.getDestination() + "-" + route.getOrigin();
        }
        int duration = Bus.getTravelDurations().getOrDefault(routeKey, 120); // Default to 2 hours
        return departureTime.plusMinutes(duration);
    }


    public static String getBaseReturnOrigin() {
        String[] stops = Bus.getBusStops();
        return stops[stops.length - 1]; // Last stop in the array
    }
}