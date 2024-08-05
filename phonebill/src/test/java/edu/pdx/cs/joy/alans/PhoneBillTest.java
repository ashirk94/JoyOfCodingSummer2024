package edu.pdx.cs.joy.alans;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    }

    @Test
    void testGetCustomer() {
        assertEquals("Test Customer", bill.getCustomer());
    }

    @Test
    void testGetPhoneCalls() {
        TreeSet<PhoneCall> calls = (TreeSet<PhoneCall>) bill.getPhoneCalls();
        assertEquals(2, calls.size());
    }

    @Test
    void testAddPhoneCall() {
        PhoneCall call = new PhoneCall("123-456-7890", "234-567-8901",
                LocalDateTime.parse("07/17/2024 10:00 AM", formatter),
                LocalDateTime.parse("07/17/2024 11:00 AM", formatter));
        bill.addPhoneCall(call);
        assertEquals(3, bill.getPhoneCalls().size());
    }

    @Test
    void testPhoneBillWithNoCalls() {
        PhoneBill emptyBill = new PhoneBill("Empty Customer");
        assertTrue(emptyBill.getPhoneCalls().isEmpty());
    }

    @Test
    void testPhoneBillWithMultipleCalls() {
        PhoneBill bill = new PhoneBill("Customer");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
        PhoneCall call1 = new PhoneCall("111-111-1111", "222-222-2222", LocalDateTime.parse("07/15/2024 10:00 AM", formatter), LocalDateTime.parse("07/15/2024 11:00 AM", formatter));
        PhoneCall call2 = new PhoneCall("333-333-3333", "444-444-4444", LocalDateTime.parse("07/16/2024 12:00 PM", formatter), LocalDateTime.parse("07/16/2024 01:00 PM", formatter));
        bill.addPhoneCall(call1);
        bill.addPhoneCall(call2);

        assertEquals(2, bill.getPhoneCalls().size());
        assertTrue(bill.getPhoneCalls().contains(call1));
        assertTrue(bill.getPhoneCalls().contains(call2));
    }

}
