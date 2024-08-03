package edu.pdx.cs.joy.alans;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PhoneCallTest {
    private PhoneCall call;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");

    @BeforeEach
    void setUp() {
        call = new PhoneCall("123-456-7890", "234-567-8901",
                LocalDateTime.parse("07/16/2024 02:00 PM", formatter),
                LocalDateTime.parse("07/16/2024 03:00 PM", formatter));
    }

    @Test
    void testGetBeginTimeString() {
        String expected = "7/16/24, 2:00 PM";
        String actual = call.getBeginTimeString().replace("\u202F", " ");
        assertEquals(expected, actual);
    }

    @Test
    void testGetEndTimeString() {
        String expected = "7/16/24, 3:00 PM";
        String actual = call.getEndTimeString().replace("\u202F", " ");
        assertEquals(expected, actual);
    }

    @Test
    void testGetCaller() {
        assertEquals("123-456-7890", call.getCaller());
    }

    @Test
    void testGetCallee() {
        assertEquals("234-567-8901", call.getCallee());
    }

    @Test
    void testGetDurationMinutes() {
        assertEquals(60, call.getDurationMinutes());
    }

    @Test
    void testPhoneCallComparison() {
        PhoneCall earlierCall = new PhoneCall("123-456-7890", "234-567-8901",
                LocalDateTime.parse("07/15/2024 02:00 PM", formatter),
                LocalDateTime.parse("07/15/2024 03:00 PM", formatter));
        PhoneCall sameTimeCall = new PhoneCall("000-000-0000", "234-567-8901",
                LocalDateTime.parse("07/16/2024 02:00 PM", formatter),
                LocalDateTime.parse("07/16/2024 03:00 PM", formatter));

        assertTrue(call.compareTo(earlierCall) > 0);
        assertTrue(call.compareTo(sameTimeCall) > 0);
    }
}
