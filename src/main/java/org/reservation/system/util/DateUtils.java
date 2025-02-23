package org.reservation.system.util;

import org.reservation.system.exception.RequestValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtils {
    
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE; // yyyy-MM-dd
    

    public static LocalDate toLocalDate(String dateStr) {
        return toLocalDate(dateStr, DEFAULT_FORMATTER);
    }


    public static LocalDate toLocalDate(String dateStr, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return toLocalDate(dateStr, formatter);
    }

    public static LocalDate toLocalDate(String dateStr, DateTimeFormatter formatter) {
        try {
            return LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            throw new RequestValidationException("Invalid date format. Please provide the date in YYYY-MM-DD format.");
        }
    }

    public static String formatDate(LocalDate date, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return date.format(formatter);
    }
}
