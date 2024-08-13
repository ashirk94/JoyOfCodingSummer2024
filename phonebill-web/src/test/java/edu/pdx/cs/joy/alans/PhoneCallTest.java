package edu.pdx.cs.joy.alans;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class PhoneCallTest {

    @Test
    void testPhoneCallCreation() {
        String caller = "503-245-2345";
        String callee = "765-389-1273";
        LocalDateTime startTime = LocalDateTime.of(2024, 2, 27, 8, 56);
        LocalDateTime endTime = LocalDateTime.of(2024, 2, 27, 10, 27);

        PhoneCall phoneCall = new PhoneCall(caller, callee, startTime, endTime);

        assertEquals(caller, phoneCall.getCaller());
        assertEquals(callee, phoneCall.getCallee());
        assertEquals(startTime, phoneCall.getBeginTime());
        assertEquals(endTime, phoneCall.getEndTime());
    }

    @Test
    void testPhoneCallDuration() {
        LocalDateTime startTime = LocalDateTime.of(2024, 2, 27, 8, 56);
        LocalDateTime endTime = LocalDateTime.of(2024, 2, 27, 10, 27);

        PhoneCall phoneCall = new PhoneCall("503-245-2345", "765-389-1273", startTime, endTime);

        long expectedDuration = 91;
        assertEquals(expectedDuration, phoneCall.getDurationMinutes());
    }

    @Test
    void testPhoneCallComparison() {
        LocalDateTime startTime1 = LocalDateTime.of(2024, 2, 27, 8, 56);
        LocalDateTime endTime1 = LocalDateTime.of(2024, 2, 27, 10, 27);
        PhoneCall phoneCall1 = new PhoneCall("503-245-2345", "765-389-1273", startTime1, endTime1);

        LocalDateTime startTime2 = LocalDateTime.of(2024, 2, 27, 9, 0);
        LocalDateTime endTime2 = LocalDateTime.of(2024, 2, 27, 10, 30);
        PhoneCall phoneCall2 = new PhoneCall("503-245-2345", "765-389-1273", startTime2, endTime2);

        assertTrue(phoneCall1.compareTo(phoneCall2) < 0);
    }
}
