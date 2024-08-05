package edu.pdx.cs.joy.alans;

import edu.pdx.cs.joy.ParserException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;

public class Project3Test {

    private static final PrintStream originalErr = System.err;
    private ByteArrayOutputStream errContent;
    private static final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUpStreams() {
        errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() throws IOException {
        // Restore original streams
        restoreStreams();
        // Clean up after each test
        Files.walk(tempDir)
                .map(Path::toFile)
                .forEach(File::delete);
    }

    public void restoreStreams() {
        System.setErr(originalErr);
        System.setOut(originalOut);
    }

    private PhoneBill bill;

    @BeforeEach
    void setUp() {
        bill = new PhoneBill("Customer");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
        bill.addPhoneCall(new PhoneCall("123-456-7890", "234-567-8901",
                LocalDateTime.parse("03/03/2024 12:00 PM", formatter),
                LocalDateTime.parse("03/03/2024 04:00 PM", formatter)));
    }

    @Test
    void testEndTimeBeforeStartTime() {
        String[] args = { "Customer", "123-456-7890", "234-567-8901", "07/15/2024", "10:00", "AM", "07/15/2024", "09:00", "AM" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("End time cannot be before start time"));
        restoreStreams();
    }

    @Test
    void testFileCreationWithValidInput() throws IOException, ParserException {
        File textFile = new File(tempDir.toFile(), "newbill.txt");
        String[] args = { "-textFile", textFile.getPath(), "Customer", "123-456-7890", "234-567-8901", "07/15/2024", "10:00", "AM", "07/15/2024", "11:00", "AM" };

        Project3.main(args);

        assertTrue(textFile.exists());
        try (BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
            TextParser parser = new TextParser(reader);
            PhoneBill bill = parser.parse();
            assertEquals("Customer", bill.getCustomer());
            assertEquals(1, bill.getPhoneCalls().size());
            PhoneCall call = bill.getPhoneCalls().iterator().next();
            assertEquals("123-456-7890", call.getCaller());
            assertEquals("234-567-8901", call.getCallee());
            assertEquals("07/15/2024 10:00 AM", DateFormatter.format(call.getBeginTime()));
            assertEquals("07/15/2024 11:00 AM", DateFormatter.format(call.getEndTime()));
        }
    }

    @Test
    void testPrettyPrintToFile() throws IOException {
        File prettyFile = new File(tempDir.toFile(), "pretty.txt");
        String[] args = { "-pretty", prettyFile.getPath(), "Customer", "123-456-7890", "234-567-8901", "07/15/2024", "10:00", "AM", "07/15/2024", "11:00", "AM" };

        Project3.main(args);

        assertTrue(prettyFile.exists());
        try (BufferedReader reader = new BufferedReader(new FileReader(prettyFile))) {
            String content = reader.readLine();
            assertThat(content, containsString("Customer: Customer"));
        }
    }

    @Test
    void testPrettyPrintToStandardOut() {
        String[] args = { "-pretty", "-", "Customer", "123-456-7890", "234-567-8901", "07/15/2024", "10:00", "AM", "07/15/2024", "11:00", "AM" };

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Project3.main(args);

        String output = outContent.toString();
        assertThat(output, containsString("Customer: Customer"));
        restoreStreams();
    }

    @Test
    void testMissingCommandLineArguments() {
        String[] args = { "Customer" };
        var errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Missing required arguments"));
        restoreStreams();
    }

    @Test
    void testUnknownCommandLineArgument() {
        String[] args = { "Customer", "123-456-7890", "234-567-8901", "03/03/2024", "12:00", "PM", "03/03/2024", "04:00", "PM", "extraArgument" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Extraneous command line argument: extraArgument"));
        restoreStreams();
    }

    @Test
    void testInvalidDateFormat() {
        String[] args = { "Customer", "123-456-7890", "234-567-8901", "invalid-date", "12:00", "PM", "03/03/2024", "04:00", "PM" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Invalid start date/time format"));
        restoreStreams();
    }

    @Test
    void testInvalidTimeFormat() {
        String[] args = { "Customer", "123-456-7890", "234-567-8901", "03/03/2024", "invalid-time", "PM", "03/03/2024", "04:00", "PM" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Invalid start date/time format"));
        restoreStreams();
    }

    @Test
    void testHelpMessage() {
        String[] args = {};
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Missing required arguments"));
        restoreStreams();
    }

    @Test
    void testReadmeMessage() {
        String[] args = { "-README" };
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Project3.main(args);

        String output = outContent.toString();
        assertThat(output, containsString("README for Project3"));
        restoreStreams();
    }

    @Test
    void testExistingPhoneBillFile() throws IOException, ParserException {
        File textFile = new File(tempDir.toFile(), "existingbill.txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter(textFile))) {
            writer.println("Customer");
            writer.println("123-456-7890 234-567-8901 03/03/2024 12:00 PM 03/03/2024 04:00 PM");
        }
        String[] args = { "-textFile", textFile.getPath(), "Customer", "123-456-7890", "234-567-8901", "07/15/2024", "10:00", "AM", "07/15/2024", "11:00", "AM" };

        Project3.main(args);

        assertTrue(textFile.exists());
        try (BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
            TextParser parser = new TextParser(reader);
            PhoneBill bill = parser.parse();
            assertEquals("Customer", bill.getCustomer());
            assertEquals(2, bill.getPhoneCalls().size());
        }
    }

    @Test
    void testDifferentCustomerName() throws IOException, ParserException {
        File textFile = new File(tempDir.toFile(), "existingbill.txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter(textFile))) {
            writer.println("Customer");
            writer.println("123-456-7890 234-567-8901 03/03/2024 12:00 PM 03/03/2024 04:00 PM");
        }
        String[] args = { "-textFile", textFile.getPath(), "DifferentCustomer", "123-456-7890", "234-567-8901", "07/15/2024", "10:00", "AM", "07/15/2024", "11:00", "AM" };

        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Customer name in file does not match specified customer"));
        restoreStreams();
    }

    @Test
    void testAddingAnotherPhoneCall() throws IOException, ParserException {
        File textFile = new File(tempDir.toFile(), "bill.txt");
        String[] args1 = { "-textFile", textFile.getPath(), "Customer", "123-456-7890", "234-567-8901", "07/15/2024", "10:00", "AM", "07/15/2024", "11:00", "AM" };
        Project3.main(args1);

        String[] args2 = { "-textFile", textFile.getPath(), "Customer", "123-456-7890", "234-567-8901", "07/15/2024", "11:30", "AM", "07/15/2024", "12:00", "PM" };
        Project3.main(args2);

        assertTrue(textFile.exists());
        try (BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
            TextParser parser = new TextParser(reader);
            PhoneBill bill = parser.parse();
            assertEquals("Customer", bill.getCustomer());
            assertEquals(2, bill.getPhoneCalls().size());
        }
    }

    @Test
    void testTextFileAndPrettyPrint() throws IOException, ParserException {
        File textFile = new File(tempDir.toFile(), "bill.txt");
        File prettyFile = new File(tempDir.toFile(), "pretty.txt");
        String[] args = { "-textFile", textFile.getPath(), "-pretty", prettyFile.getPath(), "Customer", "123-456-7890", "234-567-8901", "07/15/2024", "10:00", "AM", "07/15/2024", "11:00", "AM" };

        Project3.main(args);

        assertTrue(textFile.exists());
        assertTrue(prettyFile.exists());

        try (BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
            TextParser parser = new TextParser(reader);
            PhoneBill bill = parser.parse();
            assertEquals("Customer", bill.getCustomer());
            assertEquals(1, bill.getPhoneCalls().size());
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(prettyFile))) {
            String content = reader.readLine();
            assertThat(content, containsString("Customer: Customer"));
        }
    }

    @Test
    void testNoArguments() {
        String[] args = {};
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Missing required arguments"));
        restoreStreams();
    }

    @Test
    void testPrintOption() {
        String[] args = { "-print", "Customer", "123-456-7890", "234-567-8901", "03/03/2024", "12:00", "PM", "03/03/2024", "04:00", "PM" };
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        Project3.main(args);

        String output = outContent.toString();
        assertThat(output, containsString("Phone call from 123-456-7890 to 234-567-8901 from 3/3/2024 12:00 PM to 3/3/2024 4:00 PM"));
        restoreStreams();
    }

    @Test
    void testTextFileOptionWithoutFilename() {
        String[] args = { "-textFile" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Missing file name after -textFile"));
        restoreStreams();
    }

    @Test
    void testPrettyPrintOptionWithoutFilename() {
        String[] args = { "-pretty" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Missing file name after -pretty"));
        restoreStreams();
    }

    @Test
    void testInvalidPhoneNumberFormat() {
        String[] args = { "Customer", "1234567890", "234-567-8901", "03/03/2024", "12:00", "PM", "03/03/2024", "04:00", "PM" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Invalid phone number format"));
        restoreStreams();
    }

    @Test
    void testInvalidPhoneNumberFormat2() {
        String[] args = { "Customer", "123-456-7890", "2345678901", "03/03/2024", "12:00", "PM", "03/03/2024", "04:00", "PM" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Invalid phone number format"));
        restoreStreams();
    }

    @Test
    void testIncompleteDate() {
        String[] args = { "Customer", "123-456-7890", "234-567-8901", "03/03", "12:00", "PM", "03/03/2024", "04:00", "PM" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Invalid start date/time format"));
        restoreStreams();
    }

    @Test
    void testInvalidDateAndTime() {
        String[] args = { "Customer", "123-456-7890", "234-567-8901", "invalid-date", "invalid-time", "PM", "03/03/2024", "04:00", "PM" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Invalid start date/time format"));
        restoreStreams();
    }

    @Test
    void testFileCreationWithMultipleCalls() throws IOException, ParserException {
        File textFile = new File(tempDir.toFile(), "multipleCalls.txt");
        String[] args1 = { "-textFile", textFile.getPath(), "Customer", "123-456-7890", "234-567-8901", "07/15/2024", "10:00", "AM", "07/15/2024", "11:00", "AM" };
        String[] args2 = { "-textFile", textFile.getPath(), "Customer", "345-678-9012", "456-789-0123", "07/15/2024", "11:30", "AM", "07/15/2024", "12:00", "PM" };

        Project3.main(args1);
        Project3.main(args2);

        assertTrue(textFile.exists());
        try (BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
            TextParser parser = new TextParser(reader);
            PhoneBill bill = parser.parse();
            assertEquals("Customer", bill.getCustomer());
            assertEquals(2, bill.getPhoneCalls().size());
        }
    }

    @Test
    void testValidDateAndTimeFormats() {
        String[] validArgs1 = {"Customer", "123-456-7890", "234-567-8901", "01/01/2024", "01:00", "PM", "01/01/2024", "02:00", "PM"};
        String[] validArgs2 = {"Customer", "123-456-7890", "234-567-8901", "12/31/2024", "11:59", "AM", "12/31/2024", "12:59", "PM"};

        Project3.main(validArgs1);
        Project3.main(validArgs2);

        String output = errContent.toString();
        assertFalse(output.contains("Invalid date/time format"));
        restoreStreams();
    }

    @Test
    void testInvalidDateAndTimeFormats() {
        String[] invalidArgs1 = {"Customer", "123-456-7890", "234-567-8901", "13/01/2024", "01:00", "PM", "01/01/2024", "02:00", "PM"};
        String[] invalidArgs2 = {"Customer", "123-456-7890", "234-567-8901", "01/01/2024", "13:00", "PM", "01/01/2024", "02:00", "PM"};
        String[] invalidArgs3 = {"Customer", "123-456-7890", "234-567-8901", "01/01/2024", "01:00", "XX", "01/01/2024", "02:00", "PM"};

        Project3.main(invalidArgs1);
        Project3.main(invalidArgs2);
        Project3.main(invalidArgs3);

        String output = errContent.toString();
        assertTrue(output.contains("Invalid start date/time format"));
        restoreStreams();
    }

    @Test
    void testValidInputWithRequiredArguments() {
        String[] args = {"Customer", "123-456-7890", "234-567-8901", "07/15/2024", "10:00", "AM", "07/15/2024", "11:00", "AM"};
        Project3.main(args);
        String output = outContent.toString();
        assertFalse(output.contains("Invalid"));
        restoreStreams();
    }

    @Test
    void testInvalidPhoneNumberFormats() {
        String[] args1 = {"Customer", "1234567890", "234-567-8901", "07/15/2024", "10:00", "AM", "07/15/2024", "11:00", "AM"};
        String[] args2 = {"Customer", "123-456-7890", "2345678901", "07/15/2024", "10:00", "AM", "07/15/2024", "11:00", "AM"};

        Project3.main(args1);
        Project3.main(args2);

        String output = errContent.toString();
        assertTrue(output.contains("Invalid phone number format"));
        restoreStreams();
    }

    @Test
    void testInvalidDateFormats() {
        String[] args1 = {"Customer", "123-456-7890", "234-567-8901", "15/07/2024", "10:00", "AM", "07/15/2024", "11:00", "AM"};
        String[] args2 = {"Customer", "123-456-7890", "234-567-8901", "07/15/2024", "10:00", "XX", "07/15/2024", "11:00", "AM"};

        Project3.main(args1);
        Project3.main(args2);

        String output = errContent.toString();
        assertTrue(output.contains("Invalid start date/time format"));
        restoreStreams();
    }

    @Test
    void testMissingArguments() {
        String[] args = {"Customer", "123-456-7890", "234-567-8901", "07/15/2024", "10:00", "AM"};

        Project3.main(args);

        String output = errContent.toString();
        assertTrue(output.contains("Missing required arguments"));
        restoreStreams();
    }
    @Test
    void testInvalidEndDateFormat() {
        String[] args = { "Customer", "123-456-7890", "234-567-8901", "07/15/2024", "10:00", "AM", "invalid-date", "11:00", "AM" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Invalid end date/time format"));
        restoreStreams();
    }

    @Test
    void testInvalidEndTimeFormat() {
        String[] args = { "Customer", "123-456-7890", "234-567-8901", "07/15/2024", "10:00", "AM", "07/15/2024", "invalid-time", "AM" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Invalid end date/time format"));
        restoreStreams();
    }

    @Test
    void testInvalidEndPeriodFormat() {
        String[] args = { "Customer", "123-456-7890", "234-567-8901", "07/15/2024", "10:00", "AM", "07/15/2024", "11:00", "XX" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Invalid end date/time format"));
        restoreStreams();
    }

    @Test
    void testIncompleteEndDate() {
        String[] args = { "Customer", "123-456-7890", "234-567-8901", "07/15/2024", "10:00", "AM", "07/15", "11:00", "AM" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Invalid end date/time format"));
        restoreStreams();
    }

    @Test
    void testInvalidEndDateAndTime() {
        String[] args = { "Customer", "123-456-7890", "234-567-8901", "07/15/2024", "10:00", "AM", "invalid-date", "invalid-time", "PM" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Invalid end date/time format"));
        restoreStreams();
    }
}
