package edu.pdx.cs.joy.alans;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrettyPrinterTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a", Locale.US);

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void phoneBillIsPrettyPrinted() {
        PhoneBill bill = new PhoneBill("Test Customer");

        bill.addPhoneCall(new PhoneCall("111-111-1111", "222-222-2222",
                LocalDateTime.parse("07/15/2024 10:00 AM", formatter),
                LocalDateTime.parse("07/15/2024 11:00 AM", formatter)));

        bill.addPhoneCall(new PhoneCall("555-555-5555", "666-666-6666",
                LocalDateTime.parse("07/15/2024 10:00 AM", formatter),
                LocalDateTime.parse("07/15/2024 11:00 AM", formatter)));

        bill.addPhoneCall(new PhoneCall("777-777-7777", "888-888-8888",
                LocalDateTime.parse("07/16/2024 4:00 PM", formatter),
                LocalDateTime.parse("07/16/2024 5:00 PM", formatter)));

        PrettyPrinter prettyPrinter = new PrettyPrinter(new PrintWriter(System.out));
        prettyPrinter.dump(bill);

        String expectedOutput = "Customer: Test Customer\nPhone Calls:\n" +
                "111-111-1111 called 222-222-2222 from 07/15/2024 10:00 AM to 07/15/2024 11:00 AM Duration: 60 minutes\n" +
                "555-555-5555 called 666-666-6666 from 07/15/2024 10:00 AM to 07/15/2024 11:00 AM Duration: 60 minutes\n" +
                "777-777-7777 called 888-888-8888 from 07/16/2024 4:00 PM to 07/16/2024 5:00 PM Duration: 60 minutes\n";

        String actualOutput = outContent.toString().replace("\u202F", " ");
        // Normalize line separators
        expectedOutput = expectedOutput.replace(System.lineSeparator(), "\n");
        actualOutput = actualOutput.replace(System.lineSeparator(), "\n");

        assertEquals(expectedOutput, actualOutput);

        // Restore the original System.out
        System.setOut(originalOut);
    }
}
