package edu.pdx.cs.joy.alans;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class Project3HelperMethodsTest {

    @Test
    void testIsValidPhoneNumber() {
        assertTrue(Project3.isValidPhoneNumber("123-456-7890"));
        assertFalse(Project3.isValidPhoneNumber("1234567890"));
        assertFalse(Project3.isValidPhoneNumber("123-45-67890"));
        assertFalse(Project3.isValidPhoneNumber("12-3456-7890"));
    }

    @Test
    void testIsValidDateTime() {
        assertTrue(Project3.isValidDateTime("07/15/2024", "10:00", "AM"));
        assertFalse(Project3.isValidDateTime("15/07/2024", "10:00", "AM"));
        assertFalse(Project3.isValidDateTime("07/15/2024", "25:00", "AM"));
        assertFalse(Project3.isValidDateTime("07/15/2024", "10:00", "XX"));
    }

    @Test
    void testIsEndTimeBeforeStartTime() {
        LocalDateTime start = LocalDateTime.of(2024, 7, 15, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 7, 15, 11, 0);
        assertFalse(Project3.isEndTimeBeforeStartTime(start, end));
        assertTrue(Project3.isEndTimeBeforeStartTime(end, start));
    }
}
