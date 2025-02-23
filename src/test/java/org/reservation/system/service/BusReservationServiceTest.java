package org.reservation.system.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reservation.system.exception.RouteNotFoundException;
import org.reservation.system.exception.NotEnoughSeatException;
import org.reservation.system.model.Bus;
import org.reservation.system.model.Route;
import org.reservation.system.model.Seat;
import org.reservation.system.model.request.AvailabilityRequest;
import org.reservation.system.model.request.ReservationRequest;
import org.reservation.system.model.response.AvailabilityResponse;
import org.reservation.system.model.response.ReservationResponse;
import org.reservation.system.util.DateUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BusReservationServiceTest {

    private BusReservationService busReservationService;
    private Bus bus;
    private Route route;
    private String travelDate;

    @BeforeEach
    void setUp() {
        bus = mock(Bus.class);
        route = mock(Route.class);
        when(bus.getRoute("A", "B")).thenReturn(route);
        busReservationService = new BusReservationService(bus);
        travelDate = DateUtils.formatDate(LocalDate.now().plusDays(1), "yyyy-MM-dd") ;
    }

    @Test
    void testCheckAvailability_whenSeatsAvailable() {


        AvailabilityRequest request = new AvailabilityRequest.Builder()
                .origin("A")
                .destination("B")
                .travelDate(travelDate)
                .passengerCount(2)
                .build();

        when(bus.getSeats()).thenReturn(List.of(new Seat("1A"), new Seat("1B")));
        when(route.getOrigin()).thenReturn("A");
        when(route.getDestination()).thenReturn("B");

        AvailabilityResponse response = busReservationService.checkAvailability(request);

        assertTrue(response.isAvailable());
        assertEquals(new BigDecimal("100"), response.getTotalPrice());
    }

    @Test
    void testCheckAvailability_whenNotEnoughSeats() {

        AvailabilityRequest request = new AvailabilityRequest.Builder()
                .origin("A")
                .destination("B")
                .travelDate(travelDate)
                .passengerCount(5)
                .build();
        when(bus.getSeats()).thenReturn(List.of(new Seat("1A"), new Seat("1B"), new Seat("1C"), new Seat("1D")));
        when(route.getOrigin()).thenReturn("A");
        when(route.getDestination()).thenReturn("B");

        AvailabilityResponse response = busReservationService.checkAvailability(request);

        assertFalse(response.isAvailable());
    }

    @Test
    void testReserveTicket_whenSeatsAvailable() {
        ReservationRequest request = new ReservationRequest.Builder()
                .origin("A")
                .destination("B")
                .travelDate(travelDate)
                .passengerCount(2)
                .paymentAmount(new BigDecimal("100"))
                .build();
        when(bus.getSeats()).thenReturn(List.of(new Seat("1A"), new Seat("1B")));
        when(route.getOrigin()).thenReturn("A");
        when(route.getDestination()).thenReturn("B");

        ReservationResponse response = busReservationService.reserveTicket(request);

        assertNotNull(response.getReservationId());
        assertEquals(2, response.getAllocatedSeatNumbers().size());
        assertEquals(new BigDecimal("100"), response.getTotalPrice());
    }

    @Test
    void testReserveTicket_whenNotEnoughSeats() {
        ReservationRequest request = new ReservationRequest.Builder()
                .origin("A")
                .destination("B")
                .travelDate(travelDate)
                .passengerCount(5)
                .paymentAmount(new BigDecimal("250"))
                .build();
        when(bus.getSeats()).thenReturn(List.of(new Seat("1A"), new Seat("1B"), new Seat("1C"), new Seat("1D")));
        when(route.getOrigin()).thenReturn("A");
        when(route.getDestination()).thenReturn("B");

        NotEnoughSeatException thrown = assertThrows(NotEnoughSeatException.class, () -> {
            busReservationService.reserveTicket(request);
        });

        assertEquals("Not enough seats available for " + travelDate, thrown.getMessage());
    }

    @Test
    void testValidateRoute_whenRouteNotFound() {
        when(bus.getRoute("A", "X")).thenReturn(null);
        ReservationRequest request = new ReservationRequest.Builder()
                .origin("A")
                .destination("X")
                .travelDate(travelDate)
                .passengerCount(2)
                .paymentAmount(new BigDecimal("100"))
                .build();

        RouteNotFoundException thrown = assertThrows(RouteNotFoundException.class, () -> {
            busReservationService.reserveTicket(request);
        });

        assertEquals("Incorrect origin or destination", thrown.getMessage());
    }

    @Test
    void reserveTicket_MultipleTimes() {
        when(bus.getSeats()).thenReturn(List.of(new Seat("1A"), new Seat("1B"), new Seat("1C"), new Seat("1D")));

        ReservationRequest request1 = new ReservationRequest.Builder()
                .origin("A")
                .destination("B")
                .travelDate(travelDate)
                .passengerCount(2)
                .paymentAmount(new BigDecimal("100"))
                .build();
        ReservationRequest request2 = new ReservationRequest.Builder()
                .origin("A")
                .destination("B")
                .travelDate(travelDate)
                .passengerCount(2)
                .paymentAmount(new BigDecimal("100"))
                .build();
        ReservationResponse response1 = busReservationService.reserveTicket(request1);
        ReservationResponse response2 = busReservationService.reserveTicket(request2);

        assertNotEquals(response1.getReservationId(), response2.getReservationId());
        assertNotEquals(response1.getAllocatedSeatNumbers(), response2.getAllocatedSeatNumbers());
    }

    @Test
    void reserveTicket_SameSeatsNotBookedTwice() {
        when(bus.getSeats()).thenReturn(List.of(new Seat("1A"), new Seat("1B"), new Seat("1C"), new Seat("1D")));
        ReservationRequest request1 = new ReservationRequest.Builder()
                .origin("A")
                .destination("B")
                .travelDate(travelDate)
                .passengerCount(4)
                .paymentAmount(new BigDecimal("100"))
                .build();
        ReservationRequest request2 = new ReservationRequest.Builder()
                .origin("A")
                .destination("B")
                .travelDate(travelDate)
                .passengerCount(1)
                .paymentAmount(new BigDecimal("100"))
                .build();

        busReservationService.reserveTicket(request1);
        assertThrows(NotEnoughSeatException.class, () -> busReservationService.reserveTicket(request2));
    }
}
