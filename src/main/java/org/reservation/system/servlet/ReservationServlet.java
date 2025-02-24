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
import org.reservation.system.model.response.ApiResponse;
import org.reservation.system.model.response.AvailabilityResponse;
import org.reservation.system.model.response.ReservationResponse;
import org.reservation.system.service.BusReservationService;
import org.reservation.system.service.ReservationService;
import org.reservation.system.validator.RequestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;

import static org.reservation.system.Constant.AttributeName.*;
import static org.reservation.system.Constant.api.CHECK_AVAILABILITY;
import static org.reservation.system.Constant.api.RESERVE;

/**
 * Servlet handling reservation requests for bus ticket booking.
 */
public class ReservationServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ReservationServlet.class);
    private static final Bus bus;
    private static final ReservationService reservationService;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        bus = new Bus();
        reservationService = new BusReservationService(bus);
    }


    /**
     * Handles HTTP GET requests.
     * This method processes availability check requests based on the servlet path.
     *
     * @param req  The HTTP request object.
     * @param resp The HTTP response object.
     * @throws IOException If an input/output error occurs.
     */
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

    /**
     * Handles HTTP POST requests.
     * This method processes reservation requests based on the servlet path.
     *
     * @param req  The HTTP request object.
     * @param resp The HTTP response object.
     * @throws IOException If an input/output error occurs.
     */
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

    /**
     * Handles availability check requests.
     * Extracts request parameters, validates them, and checks bus availability.
     *
     * @param req  The HTTP request object.
     * @param resp The HTTP response object.
     * @throws IOException If an input/output error occurs.
     */
    private void handleAvailabilityRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            logger.info("Starting to proceed availability request");
            RequestValidator.validatePassengerCount(req);

            AvailabilityRequest availabilityRequest = new AvailabilityRequest.Builder()
                    .origin(req.getParameter(ORIGIN))
                    .destination(req.getParameter(DESTINATION))
                    .passengerCount(Integer.parseInt(req.getParameter(PASSENGER_COUNT)))
                    .travelDate(req.getParameter(TRAVEL_DATE))
                    .build();

            RequestValidator.validateAvailabilityRequest(availabilityRequest);
            AvailabilityResponse availabilityResponse = reservationService.checkAvailability(availabilityRequest);
            ApiResponse<AvailabilityResponse> response = ApiResponse.success(availabilityResponse);
            String stringResponse = objectMapper.writeValueAsString(response);
            logger.info("Received availability response: {}", stringResponse);
            sendJsonResponse(resp, HttpServletResponse.SC_OK, stringResponse);
        } catch (RouteNotFoundException | RequestValidationException e) {
            logger.error("Error handling availability request: {}", e.getMessage());
            ApiResponse<AvailabilityResponse> response = ApiResponse.error(e.getMessage(), null);
            String errorResponse = objectMapper.writeValueAsString(response);
            logger.info("Error response: {}", errorResponse);
            sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error handling availability request", e);
            ApiResponse<AvailabilityResponse> response = ApiResponse.error("Internal Server Error", null);
            String errorResponse = objectMapper.writeValueAsString(response);
            sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorResponse);
        }
    }

    /**
     * Handles ticket reservation requests.
     * Reads the request body, processes the reservation, and sends a response.
     *
     * @param req  The HTTP request object.
     * @param resp The HTTP response object.
     * @throws IOException If an input/output error occurs.
     */
    private void handleReservationRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            logger.info("Starting to proceed reservation request - {}", req);
            ReservationRequest request = objectMapper.readValue(readRequestBody(req), ReservationRequest.class);

            RequestValidator.validateReservationRequest(request);
            ReservationResponse resResponse = reservationService.reserveTicket(request);
            ApiResponse<ReservationResponse> response = ApiResponse.success(resResponse);
            String stringResponse = objectMapper.writeValueAsString(response);
            logger.info("Received reservation response: {}", stringResponse);
            sendJsonResponse(resp, HttpServletResponse.SC_OK, stringResponse);
        } catch (NotEnoughSeatException e) {
            ApiResponse<AvailabilityResponse> response = ApiResponse.error(null, e.getMessage());
            String stringResponse = objectMapper.writeValueAsString(response);
            logger.info(" Response : {}", stringResponse);
            sendJsonResponse(resp, HttpServletResponse.SC_OK, stringResponse);
        } catch (RouteNotFoundException | RequestValidationException e) {
            logger.error("Error handling reservation request: {}", e.getMessage());
            ApiResponse<AvailabilityResponse> response = ApiResponse.error(e.getMessage(), null);
            String errorResponse = objectMapper.writeValueAsString(response);
            sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, errorResponse);
        } catch (Exception e) {
            logger.error("Unexpected error handling reservation request", e);
            ApiResponse<AvailabilityResponse> response = ApiResponse.error("Internal Server Error", null);
            String errorResponse = objectMapper.writeValueAsString(response);
            sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorResponse);
        }
    }

    /**
     * Reads the request body and returns it as a string.
     *
     * @param req The HTTP request object.
     * @return The request body as a string.
     * @throws IOException If an input/output error occurs.
     */
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

    /**
     * Sends a JSON response to the client.
     *
     * @param resp       The HTTP response object.
     * @param statusCode The HTTP status code.
     * @param json       The JSON response string.
     * @throws IOException If an input/output error occurs.
     */
    private void sendJsonResponse(HttpServletResponse resp, int statusCode, String json) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(statusCode);
        resp.getWriter().write(json);
    }
}
