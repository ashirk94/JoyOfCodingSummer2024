package edu.pdx.cs.joy.alans;

import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PrettyPrinterTest {

    @Test
    public void testPrintPhoneBill() {
        PhoneBill bill = new PhoneBill("Dave");
        bill.addPhoneCall(new PhoneCall("503-245-2345", "765-389-1273", LocalDateTime.now(), LocalDateTime.now().plusMinutes(10)));

        StringWriter writer = new StringWriter();
        PrettyPrinter printer = new PrettyPrinter(new PrintWriter(writer));

        printer.printPhoneBill(bill);
        String result = writer.toString();

        assertTrue(result.contains("Customer: Dave"));
        assertTrue(result.contains("Phone calls:"));
    }
}
