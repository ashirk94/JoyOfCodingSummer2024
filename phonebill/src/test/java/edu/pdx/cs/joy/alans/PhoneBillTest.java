package edu.pdx.cs.joy.alans;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PhoneBillTest {
    private PhoneBill bill;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");

    @BeforeEach
    void setUp() {
        bill = new PhoneBill("Test Customer");
        bill.addPhoneCall(new PhoneCall("555-555-5555", "666-666-6666",
                LocalDateTime.parse("07/15/2024 10:00 AM", formatter),
                LocalDateTime.parse("07/15/2024 11:00 AM", formatter)));
        bill.addPhoneCall(new PhoneCall("777-777-7777", "888-888-8888",
                LocalDateTime.parse("07/16/2024 04:00 PM", formatter),
                LocalDateTime.parse("07/16/2024 05:00 PM", formatter)));
        bill.addPhoneCall(new PhoneCall("111-111-1111", "222-222-2222",
                LocalDateTime.parse("07/15/2024 10:00 AM", formatter),
                LocalDateTime.parse("07/15/2024 11:00 AM", formatter))); // Same begin time as the first call
    }

    @Test
    void testGetCustomer() {
        assertEquals("Test Customer", bill.getCustomer());
    }

    @Test
    void testGetPhoneCalls() {
        assertEquals(3, bill.getPhoneCalls().size());
    }

    @Test
    void testPhoneCallsSortedByBeginTimeAndCaller() {
        var calls = bill.getPhoneCalls().toArray(new PhoneCall[0]);
        assertEquals("111-111-1111", calls[0].getCaller());
        assertEquals("555-555-5555", calls[1].getCaller());
        assertEquals("777-777-7777", calls[2].getCaller());
    }
}
