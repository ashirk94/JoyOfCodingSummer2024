package edu.pdx.cs.joy.alans;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This class provides utility methods for formatting and parsing date and time strings.
 */
public class DateFormatter {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");

    /**
     * Formats a LocalDateTime object into a string with the pattern "MM/dd/yyyy hh:mm a".
     *
     * @param dateTime The LocalDateTime object to format
     * @return The formatted date and time string
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime.format(formatter);
    }

    /**
     * Parses a date and time string into a LocalDateTime object using the pattern "MM/dd/yyyy hh:mm a".
     *
     * @param dateTimeString The date and time string to parse
     * @return The parsed LocalDateTime object
     */
    public static LocalDateTime parse(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, formatter);
    }
}
