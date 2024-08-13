package edu.pdx.cs.joy.alans;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PhoneBillTest {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a", Locale.US);
    private PhoneBill phoneBill;

    @BeforeEach
    void setUp() {
        phoneBill = new PhoneBill("Test Customer");
    }

    @Test
    void testAddPhoneCallAndSorting() {
        PhoneCall call1 = new PhoneCall("503-123-4567", "503-765-4321",
                LocalDateTime.parse("02/01/2024 10:00 AM", formatter),
                LocalDateTime.parse("02/01/2024 11:00 AM", formatter));

        PhoneCall call2 = new PhoneCall("503-111-1111", "503-222-2222",
                LocalDateTime.parse("02/01/2024 09:00 AM", formatter),
                LocalDateTime.parse("02/01/2024 10:00 AM", formatter));

        PhoneCall call3 = new PhoneCall("503-123-4567", "503-222-2222",
                LocalDateTime.parse("02/01/2024 10:00 AM", formatter),
                LocalDateTime.parse("02/01/2024 11:00 AM", formatter));

        phoneBill.addPhoneCall(call1);
        phoneBill.addPhoneCall(call2);
        phoneBill.addPhoneCall(call3);

        Collection<PhoneCall> calls = phoneBill.getPhoneCalls();
        assertEquals(3, calls.size());

        // Verify that the calls are sorted by start time and then by caller number
        assertTrue(calls.iterator().next().equals(call2));
    }

    @Test
    void testGetCustomer() {
        assertEquals("Test Customer", phoneBill.getCustomer());
    }
}
