package edu.pdx.cs.joy.alans;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DateFormatterTest {

    @Test
    void testFormatValidDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 7, 15, 10, 0);
        String formattedDate = DateFormatter.format(dateTime);
        assertEquals("07/15/2024 10:00 AM", formattedDate);
    }

    @Test
    void testParseValidDateTime() {
        String dateTimeString = "07/15/2024 10:00 AM";
        LocalDateTime dateTime = DateFormatter.parse(dateTimeString);
        assertEquals(LocalDateTime.of(2024, 7, 15, 10, 0), dateTime);
    }

    @Test
    void testParseInvalidDateTime() {
        String invalidDateTimeString = "invalid-date-time";
        assertThrows(DateTimeParseException.class, () -> DateFormatter.parse(invalidDateTimeString));
    }
}
