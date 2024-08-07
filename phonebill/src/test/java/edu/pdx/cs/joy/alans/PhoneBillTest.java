package edu.pdx.cs.joy.alans;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class PhoneBillTest {
    private PhoneBill bill;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a", Locale.US);

    @BeforeEach
    public void setUp() {
        bill = new PhoneBill("Test Customer");
    }

    @Test
    public void testAddPhoneCall() {
        PhoneCall call = new PhoneCall("123-456-7890", "234-567-8901",
                LocalDateTime.parse("01/01/2024 10:00 AM", formatter),
                LocalDateTime.parse("01/01/2024 11:00 AM", formatter));
        bill.addPhoneCall(call);
        Collection<PhoneCall> calls = bill.getPhoneCalls();
        assertEquals(1, calls.size());
        assertTrue(calls.contains(call));
    }

    @Test
    public void testGetCustomer() {
        assertEquals("Test Customer", bill.getCustomer());
    }

    @Test
    public void testGetPhoneCalls() {
        assertNotNull(bill.getPhoneCalls());
    }
}
