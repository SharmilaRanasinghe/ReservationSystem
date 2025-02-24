package org.reservation.system.service;

import org.reservation.system.model.Bus;
import org.reservation.system.model.Route;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Utility class for calculating estimated departure and arrival times
 * for bus routes based on predefined travel durations.
 */
public class TimeDurationCalculator {

    /**
     * Calculates the estimated departure time for a given route and travel date.
     *
     * @param route      The route for which departure time is being estimated.
     * @param travelDate The travel date.
     * @return The estimated departure time as a LocalDateTime.
     */
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
            key = route.getOrigin() + "-" + baseOrigin;
        }

        // If the origin is the base stop, return the base departure time
        if (baseOrigin.equals(route.getOrigin())) {
            return baseDepartureTime;
        }

        // Calculate departure time by adding predefined travel duration
        int duration = Bus.getTravelDurations().getOrDefault(key, 0);
        return baseDepartureTime.plusMinutes(duration);
    }

    /**
     * Calculates the estimated arrival time based on departure time and route.
     *
     * @param departureTime The departure time of the bus.
     * @param route         The route for which arrival time is being estimated.
     * @return The estimated arrival time as a LocalDateTime.
     */
    public static LocalDateTime getEstimatedArrivalTime(LocalDateTime departureTime, Route route) {
        String routeKey = route.getOrigin() + "-" + route.getDestination();
        if (route.isReturnRoute()) {
            routeKey = route.getDestination() + "-" + route.getOrigin();
        }

        // Fetch travel duration or use default value
        int duration = Bus.getTravelDurations().getOrDefault(routeKey, 120); // Default to 2 hours
        return departureTime.plusMinutes(duration);
    }

    /**
     * Retrieves the base return origin, which is the last stop in the bus route.
     *
     * @return The base return origin as a String.
     */
    public static String getBaseReturnOrigin() {
        String[] stops = Bus.getBusStops();
        return stops[stops.length - 1]; // Last stop in the array
    }
}
