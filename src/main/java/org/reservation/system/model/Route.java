package org.reservation.system.model;

/**
 * Represents a bus route between an origin and a destination.
 */
public class Route {
    private final String routeId;
    private final String origin;
    private final String destination;
    private final boolean isReturnRoute;

    /**
     * Constructs a new route.
     *
     * @param routeId       The unique identifier for the route.
     * @param origin        The starting location of the route.
     * @param destination   The ending location of the route.
     * @param isReturnRoute Indicates whether this route is a return trip.
     */
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
}
