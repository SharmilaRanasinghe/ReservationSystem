package org.reservation.system;

public class Constant {
    public static class method {
        public static final String GET = "GET";
        public static final String POST = "POST";
    }

    public static class api {
        public static final String RESERVE = "/reserve";
        public static final String CHECK_AVAILABILITY = "/check-availability";
    }

    public static class AttributeName {
        public static final String ORIGIN = "origin";
        public static final String DESTINATION = "destination";
        public static final String PASSENGER_COUNT = "passengerCount";
        public static final String TRAVEL_DATE = "travelDate";
    }

    public static class DefaultConfig {
        public static final int MIN_RESERVATION_DAYS = 1;
        public static final int MAX_RESERVATION_DAYS = 7;
    }
}
