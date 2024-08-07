package edu.pdx.cs.joy.alans;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class PhoneCallTest {
    private PhoneCall call;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a", Locale.US);

    @BeforeEach
    void setUp() {
        call = new PhoneCall("123-456-7890", "234-567-8901",
                LocalDateTime.parse("07/16/2024 02:00 PM", formatter),
                LocalDateTime.parse("07/16/2024 03:00 PM", formatter));
    }

    @Test
    void testGetBeginTimeString() {
        String expected = "07/16/2024 2:00 PM";
        String actual = call.getBeginTimeString().replace("\u202F", " ");
        assertEquals(expected, actual);
    }

    @Test
    void testGetEndTimeString() {
        String expected = "07/16/2024 3:00 PM";
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

    @Test
    void testPhoneCallDuration() {
        PhoneCall call = new PhoneCall("111-111-1111", "222-222-2222",
                LocalDateTime.parse("07/15/2024 10:00 AM", formatter),
                LocalDateTime.parse("07/15/2024 11:00 AM", formatter));
        assertEquals(60, call.getDurationMinutes());
    }

    @Test
    void testPhoneCallComparisonWithSameTime() {
        PhoneCall call1 = new PhoneCall("111-111-1111", "222-222-2222",
                LocalDateTime.parse("07/15/2024 10:00 AM", formatter),
                LocalDateTime.parse("07/15/2024 11:00 AM", formatter));
        PhoneCall call2 = new PhoneCall("333-333-3333", "444-444-4444",
                LocalDateTime.parse("07/15/2024 10:00 AM", formatter),
                LocalDateTime.parse("07/15/2024 11:00 AM", formatter));

        assertTrue(call1.compareTo(call2) < 0);
    }
}
