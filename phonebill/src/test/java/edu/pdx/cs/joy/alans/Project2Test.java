package edu.pdx.cs.joy.alans;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import edu.pdx.cs.joy.ParserException;

/**
 * Integration tests for the <code>Project2</code> class.
 */
public class Project2Test {

    @TempDir
    Path tempDir;

    private static final PrintStream originalErr = System.err;
    private ByteArrayOutputStream errContent;
    private static final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    public void setUpStreams() {
        errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void testHelpMessageDisplayed() {
        String[] args = {};
        Project2.main(args);

        String output = errContent.toString().trim();
        assertEquals("Missing command line arguments", output.split("\n")[0], "Expected command line usage message");
    }

    @Test
    void testReadMeDisplayed() {
        String[] args = { "-README" };
        Project2.main(args);

        String output = outContent.toString().trim();
        assertTrue(output.contains("README"), "Expected README message");
    }

    @Test
    void testInvalidCallerPhoneNumber() {
        String[] args = { "-textFile", "alans/alans-x.txt", "Test3", "ABC-123-4567", "123-456-7890", "03/03/2024", "12:00", "03/03/2024", "16:00" };
        Project2.main(args);

        String output = errContent.toString().trim();
        assertEquals("Invalid phone number format", output, "Expected error message for invalid phone number");
    }

    @Test
    void testInvalidBeginTime() {
        String[] args = { "-textFile", "alans/alans-x.txt", "Test4", "123-456-7890", "234-567-8901", "03/03/2024", "12:XX", "03/03/2024", "16:00" };
        Project2.main(args);

        String output = errContent.toString().trim();
        assertEquals("Invalid date/time format", output, "Expected error message for invalid begin time");
    }

    @Test
    void testInvalidEndTime() {
        String[] args = { "-textFile", "alans/alans-x.txt", "Test5", "123-456-7890", "234-567-8901", "03/03/2024", "12:00", "01/04/20/1", "16:00" };
        Project2.main(args);

        String output = errContent.toString().trim();
        assertEquals("Invalid date/time format", output, "Expected error message for invalid end time");
    }

    @Test
    void testUnknownCommandLineArgument() {
        String[] args = { "-textFile", "alans/alans-x.txt", "Test6", "123-456-7890", "234-567-8901", "03/03/2024", "12:00", "04/04/2024", "16:00", "fred" };
        Project2.main(args);

        String output = errContent.toString().trim();
        assertEquals("Extraneous command line argument: fred", output, "Expected error message for unknown command line argument");
    }

    @Test
    void testStartingNewPhoneBillFile() throws IOException, ParserException {
        Path textFile = tempDir.resolve("alans.txt");
        String[] args = { "-textFile", textFile.toString(), "-print", "Project2", "123-456-7890", "234-567-9081", "01/07/2024", "07:00 AM", "01/17/2024", "05:00 PM" };
        Project2.main(args);

        String output = outContent.toString().trim();
        assertTrue(output.contains("Creating a new file: " + textFile.toString()), "Expected message for creating new file");
        assertTrue(output.contains("Phone call from 123-456-7890 to 234-567-9081"), "Expected phone call details");

        // Check if the file was created
        assertTrue(Files.exists(textFile), "Expected the new file to be created");

        // Parse the file and validate its contents
        try (BufferedReader reader = Files.newBufferedReader(textFile)) {
            TextParser parser = new TextParser(reader);
            PhoneBill bill = parser.parse();
            assertEquals("Project2", bill.getCustomer(), "Expected customer name to match");
            assertEquals(1, bill.getPhoneCalls().size(), "Expected one phone call in the bill");
            PhoneCall call = bill.getPhoneCalls().iterator().next();
            assertEquals("123-456-7890", call.getCaller(), "Expected caller number to match");
            assertEquals("234-567-9081", call.getCallee(), "Expected callee number to match");
            assertEquals("01/07/2024 7:00 AM", call.getBeginTimeString().replace("\u00A0", " ").replace("\u202F", " "), "Expected begin time to match");
            assertEquals("01/17/2024 5:00 PM", call.getEndTimeString().replace("\u00A0", " ").replace("\u202F", " "), "Expected end time to match");
        }
    }

    @Test
    void testUsingExistingPhoneBillFile() throws IOException, ParserException {
        Path textFile = tempDir.resolve("alans.txt");
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(textFile))) {
            writer.println("Project2");
            writer.println("123-456-7890 234-567-9081 01/07/2024 07:00 AM 01/17/2024 05:00 PM");
        }

        String[] args = { "-textFile", textFile.toString(), "Project2", "123-456-7890", "456-789-0123", "01/08/2024", "08:00 AM", "01/08/2024", "06:00 PM" };
        Project2.main(args);

        // Check if the file contains both phone calls
        try (BufferedReader reader = Files.newBufferedReader(textFile)) {
            TextParser parser = new TextParser(reader);
            PhoneBill bill = parser.parse();
            assertEquals(2, bill.getPhoneCalls().size(), "Expected two phone calls in the bill");
        }
    }

    @Test
    void testCustomerNameMismatch() throws IOException {
        Path textFile = tempDir.resolve("alans.txt");
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(textFile))) {
            writer.println("Project2");
            writer.println("123-456-7890 234-567-9081 01/07/2024 07:00 AM 01/17/2024 05:00 PM");
        }

        String[] args = { "-textFile", textFile.toString(), "DIFFERENT", "123-456-7890", "789-012-3456", "01/09/2024", "09:00 AM", "02/04/2024", "04:00 PM" };
        Project2.main(args);

        String output = errContent.toString().trim();
        assertEquals("Customer name in file does not match specified customer. Expected: Project2, Provided: DIFFERENT", output, "Expected error message for customer name mismatch");
    }
}
