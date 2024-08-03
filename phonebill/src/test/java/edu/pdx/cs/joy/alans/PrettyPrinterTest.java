package edu.pdx.cs.joy.alans;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrettyPrinterTest {
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
    void phoneBillIsPrettyPrinted() {
        StringWriter sw = new StringWriter();
        PrettyPrinter printer = new PrettyPrinter(new PrintWriter(sw));
        printer.dump(bill);

        String expectedOutput = "Customer: Test Customer\n" +
                "Phone Calls:\n" +
                "111-111-1111 called 222-222-2222 from 07/15/2024 10:00 AM to 07/15/2024 11:00 AM Duration: 60 minutes\n" +
                "555-555-5555 called 666-666-6666 from 07/15/2024 10:00 AM to 07/15/2024 11:00 AM Duration: 60 minutes\n" +
                "777-777-7777 called 888-888-8888 from 07/16/2024 04:00 PM to 07/16/2024 05:00 PM Duration: 60 minutes\n";

        String actualOutput = sw.toString();

        // Remove any non-breaking spaces and other potential problematic whitespace characters
        String cleanedExpectedOutput = expectedOutput.replaceAll("\\s+", " ").trim();
        String cleanedActualOutput = actualOutput.replaceAll("\\s+", " ").trim();

        assertEquals(cleanedExpectedOutput, cleanedActualOutput);
    }
}
