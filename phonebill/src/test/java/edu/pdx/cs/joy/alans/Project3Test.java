package edu.pdx.cs.joy.alans;

import edu.pdx.cs.joy.ParserException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Project3Test {

    @TempDir
    File tempDir;

    private PhoneBill bill;

    @BeforeEach
    void setUp() {
        bill = new PhoneBill("Customer");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
        bill.addPhoneCall(new PhoneCall("123-456-7890", "234-567-8901",
                LocalDateTime.parse("03/03/2024 12:00 PM", formatter),
                LocalDateTime.parse("03/03/2024 04:00 PM", formatter)));
    }

    @AfterEach
    void tearDown() throws IOException {
        for (File file : tempDir.listFiles()) {
            file.delete();
        }
        Files.deleteIfExists(tempDir.toPath());
    }

    @Disabled
    @Test
    void testCreateNewFileWhenNotExist() throws IOException, ParserException {
        File textFile = new File(tempDir, "newbill.txt");
        String[] args = { "-textFile", textFile.getPath(), "New Customer", "234-567-8901", "109-876-5432", "08/01/2024", "3:00 PM", "08/01/2024", "3:30 PM" };
        Project3.main(args);

        assertTrue(textFile.exists());

        try (BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
            TextParser parser = new TextParser(reader);
            PhoneBill bill = parser.parse();
            assertEquals("New Customer", bill.getCustomer());
            assertEquals(1, bill.getPhoneCalls().size());
            PhoneCall call = bill.getPhoneCalls().iterator().next();
            assertEquals("234-567-8901", call.getCaller());
            assertEquals("109-876-5432", call.getCallee());
            assertEquals("8/1/24, 3:00 PM", call.getBeginTimeString().replace("\u00A0", " ").replace("\u202F", " "));
            assertEquals("8/1/24, 3:30 PM", call.getEndTimeString().replace("\u00A0", " ").replace("\u202F", " "));
        }
    }

    @Test
    void testMissingCommandLineArguments() {
        String[] args = { "Customer" };
        var errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Missing required command line arguments"));
    }

    @Test
    void testUnknownCommandLineArgument() {
        String[] args = { "Customer", "123-456-7890", "234-567-8901", "03/03/2024", "12:00", "03/03/2024", "16:00", "extraArgument" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Extraneous command line argument: extraArgument"));
    }

    @Test
    void testInvalidDateFormat() {
        String[] args = { "Customer", "123-456-7890", "234-567-8901", "invalid-date", "12:00", "03/03/2024", "16:00" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Invalid date/time format"));
    }

    @Test
    void testInvalidTimeFormat() {
        String[] args = { "Customer", "123-456-7890", "234-567-8901", "03/03/2024", "invalid-time", "03/03/2024", "16:00" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Invalid date/time format"));
    }

    @Disabled
    @Test
    void testMalformedTextFile() throws IOException {
        File textFile = new File(tempDir, "malformed.txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter(textFile))) {
            writer.println("Customer");
            writer.println("Malformed data line");
        }

        String[] args = { "-textFile", textFile.getPath(), "Customer", "123-456-7890", "234-567-8901", "03/03/2024", "12:00", "03/03/2024", "16:00" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Could not read from text file"));
    }

    @Disabled
    @Test
    void testCustomerNameMismatch() throws IOException {
        File textFile = new File(tempDir, "customer_mismatch.txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter(textFile))) {
            writer.println("Different Customer");
            writer.println("123-456-7890 234-567-8901 03/03/2024 12:00 03/03/2024 16:00");
        }

        String[] args = { "-textFile", textFile.getPath(), "Customer", "123-456-7890", "234-567-8901", "03/03/2024", "12:00", "03/03/2024", "16:00" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Customer name in file does not match specified customer"));
    }

    @Test
    void testUnknownCommandLineOption() {
        String[] args = { "-unknownOption", "Customer", "123-456-7890", "234-567-8901", "03/03/2024", "12:00", "03/03/2024", "16:00" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Unknown command line option: -unknownOption"));
    }
}
