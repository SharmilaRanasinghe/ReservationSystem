package org.reservation.system.model;

import java.util.ArrayList;
import java.util.List;

public class Route {
    private final String routeId;
    private final String origin;
    private final String destination;
    private final boolean isReturnRoute;

    public Route(String routeId, String origin, String destination, boolean isReturnRoute) {
        this.routeId = routeId;
        this.origin = origin;
        this.destination = destination;
        this.isReturnRoute = isReturnRoute;
    }

    // Getters
    public String getRouteId() { return routeId; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public boolean isReturnRoute() { return isReturnRoute; }
   // public List<String> getIntermediateStops() { return new ArrayList<>(intermediateStops); }
}