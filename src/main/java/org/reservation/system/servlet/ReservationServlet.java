package org.reservation.system.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.reservation.system.exception.NotEnoughSeatException;
import org.reservation.system.exception.RequestValidationException;
import org.reservation.system.exception.RouteNotFoundException;
import org.reservation.system.model.Bus;
import org.reservation.system.model.request.AvailabilityRequest;
import org.reservation.system.model.request.ReservationRequest;
import org.reservation.system.model.response.AvailabilityResponse;
import org.reservation.system.model.response.ReservationResponse;
import org.reservation.system.service.BusReservationService;
import org.reservation.system.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;

import static org.reservation.system.Constant.AttributeName.*;
import static org.reservation.system.Constant.api.CHECK_AVAILABILITY;
import static org.reservation.system.Constant.api.RESERVE;

public class ReservationServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ReservationServlet.class);
    private final ReservationService reservationService = new BusReservationService(new Bus());
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.info("Get request received");
        String servletPath = req.getServletPath();

        if (CHECK_AVAILABILITY.equals(servletPath)) {
            handleAvailabilityRequest(req, resp);
        } else {
            sendJsonResponse(resp, HttpServletResponse.SC_NOT_FOUND, "{\"error\": \"Not Found\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.info("Post request received");
        String path = req.getServletPath();
        logger.info("Path: {}", path);
        if (RESERVE.equals(path)) {
            handleReservationRequest(req, resp);
        } else {
            sendJsonResponse(resp, HttpServletResponse.SC_NOT_FOUND, "{\"error\": \"Not Found\"}");
        }
    }

    private void handleAvailabilityRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            logger.info("Starting to proceed availability request");
            AvailabilityRequest availabilityRequest = new AvailabilityRequest.Builder()
                    .origin(req.getParameter(ORIGIN))
                    .destination(req.getParameter(DESTINATION))
                    .passengerCount(Integer.parseInt(req.getParameter(PASSENGER_COUNT)))
                    .travelDate(req.getParameter(TRAVEL_DATE))
                    .build();
            AvailabilityResponse response = reservationService.checkAvailability(availabilityRequest);
            String stringResponse = objectMapper.writeValueAsString(response);
            logger.info("Received availability response: {}", stringResponse);
            sendJsonResponse(resp, HttpServletResponse.SC_OK, stringResponse);
        } catch (RouteNotFoundException | RequestValidationException e) {
            logger.error("Error handling availability request: {}", e.getMessage());
            sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            logger.error("Unexpected error handling availability request", e);
            sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "{\"error\": \"Internal Server Error\"}");
        }
    }

    private void handleReservationRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            logger.info("Starting to proceed reservation request - {}", req);
            ReservationRequest request = objectMapper.readValue(readRequestBody(req), ReservationRequest.class);
            ReservationResponse response = reservationService.reserveTicket(request);
            String stringResponse = objectMapper.writeValueAsString(response);
            logger.info("Received reservation response: {}",stringResponse);
            sendJsonResponse(resp, HttpServletResponse.SC_OK, stringResponse);
        } catch (NotEnoughSeatException e) {
            sendJsonResponse(resp, HttpServletResponse.SC_OK, "{\"message\": \"" + e.getMessage() + "\"}");
        } catch (RouteNotFoundException | RequestValidationException e) {
            logger.error("Error handling reservation request: {}", e.getMessage());
            sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            logger.error("Unexpected error handling reservation request", e);
            sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "{\"error\": \"Internal Server Error\"}");
        }
    }

    private String readRequestBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    private void sendJsonResponse(HttpServletResponse resp, int statusCode, String json) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(statusCode);
        resp.getWriter().write(json);
    }
}
