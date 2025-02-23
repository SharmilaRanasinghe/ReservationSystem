package org.reservation.system.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class Bus {
    private final int rows = 10;
    private final int seatsPerRow = 4;
    private static final String[] stops = {"A", "B", "C", "D"};

    private final char[] seatLabels = {'A', 'B', 'C', 'D'};
    private final List<Seat> seatList = new ArrayList<>();

    private static final Map<String, BigDecimal> ticketPrices = new HashMap<>();
    private static final List<Route> routes = new ArrayList<>();
    private static final List<Route> returnRoutes = new ArrayList<>();
    private static final Map<String, Integer> travelDurations = new HashMap<>(); // Travel time per route

    private static final int baseDepartureTime = 9; // hour 9:00 AM
    private static final int baseReturnDepartureTime = 14;

    static {
        initializeTicketPrices();
        initializeRoutes();
        initializeTravelDurations();
    }

    private static void initializeTicketPrices() {
        ticketPrices.put("A-B", BigDecimal.valueOf(50));
        ticketPrices.put("A-C", BigDecimal.valueOf(100));
        ticketPrices.put("A-D", BigDecimal.valueOf(150));
        ticketPrices.put("B-C", BigDecimal.valueOf(50));
        ticketPrices.put("B-D", BigDecimal.valueOf(100));
        ticketPrices.put("C-D", BigDecimal.valueOf(50));
    }

    private static void initializeTravelDurations() {
        travelDurations.put("A-B", 90);  // 1 hour 30 mins
        travelDurations.put("A-C", 180); // 3 hours
        travelDurations.put("A-D", 240); // 4 hours
        travelDurations.put("B-C", 120); // 2 hours
        travelDurations.put("B-D", 180); // 3 hours
        travelDurations.put("C-D", 90);  // 1 hour 30 mins
    }

    private static void initializeRoutes() {
        for (int i = 0; i <= stops.length-2; i++) {
            for (int j = i+1; j <= stops.length-1; j++) {
                Route route = new Route(stops[i] + stops[j], stops[i], stops[j], false);
                routes.add(route);
                Route returnRoute = new Route(stops[j] + stops[i], stops[j], stops[i], true);
                returnRoutes.add(returnRoute);
            }
        }
    }


    public Bus() {
        initializeSeats();
    }

    private void initializeSeats() {

        for (int i = 1; i <= rows; i++) {
            for (char seatLabel : seatLabels) {
                Seat seat = new Seat(i + String.valueOf(seatLabel));
                seatList.add(seat);
            }
        }
    }

    public static Map<String, BigDecimal> getTicketPriceList() {
        return ticketPrices;
    }

    public List<Seat> getSeats() {
        return seatList;
    }

    public Route getRoute(String origin, String destination) {

        return routes.stream().filter(route -> route.getOrigin().equals(origin)
                && route.getDestination().equals(destination)).findFirst().orElse(returnRoutes.stream().filter(route -> route.getOrigin().equals(origin)
                && route.getDestination().equals(destination)).findFirst().orElse(null));
    }

    public static Map<String, Integer> getTravelDurations() {
        return travelDurations;
    }

    public static String[] getBusStops() {
        return stops;
    }

    public static int getBaseDepartureTime() {
        return baseDepartureTime;
    }

    public static int getBaseReturnDepartureTime() {
        return baseReturnDepartureTime;
    }
}

