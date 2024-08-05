package edu.pdx.cs.joy.alans;

import edu.pdx.cs.joy.ParserException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertThat(output, containsString("Invalid date/time format"));
        restoreStreams();
    }

    @Test
    void testInvalidTimeFormat() {
        String[] args = { "Customer", "123-456-7890", "234-567-8901", "03/03/2024", "invalid-time", "PM", "03/03/2024", "04:00", "PM" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Invalid date/time format"));
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
    void testValidCall() throws IOException, ParserException {
        File textFile = new File(tempDir.toFile(), "testfile.txt");
        String[] args = { "-textFile", textFile.getPath(), "Customer", "123-456-7890", "234-567-8901", "07/15/2024", "10:00", "AM", "07/15/2024", "11:00", "AM" };

        Project3.main(args);

        assertTrue(textFile.exists(), "The file should exist");
        try (BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
            TextParser parser = new TextParser(reader);
            PhoneBill bill = parser.parse();
            assertTrue(bill != null);
            assertTrue(bill.getPhoneCalls().size() > 0);
        }
    }

    @Test
    void testExtraneousArgument() {
        String[] args = {"-textFile", "testfile.txt", "Customer", "123-456-7890", "234-567-8901", "01/03/2024", "11:00", "am", "01/03/2024", "1:00", "pm", "extra"};
        try {
            Project3.main(args);
        } catch (Exception e) {
            // Ignore exception
        }
        assertTrue(errContent.toString().contains("Extraneous command line argument"));
        restoreStreams();
    }
}
