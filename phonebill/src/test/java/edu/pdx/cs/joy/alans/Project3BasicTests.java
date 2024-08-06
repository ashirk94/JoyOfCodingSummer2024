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
import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;

public class Project3BasicTests {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a", Locale.ENGLISH);
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

    @Test
    void testPrintOption() {
        String[] args = { "-print", "Customer", "123-456-7890", "234-567-8901", "07/15/2024", "10:00", "AM", "07/15/2024", "11:00", "AM" };
        Project3.main(args);

        String output = outContent.toString();
        assertThat(output, containsString("Phone call from 123-456-7890 to 234-567-8901 from 7/15/2024 10:00 AM to 7/15/2024 11:00 AM"));
    }

    @Test
    void testTextFileCreation() throws IOException {
        File textFile = new File(tempDir.toFile(), "bill.txt");
        String[] args = { "-textFile", textFile.getPath(), "Customer", "123-456-7890", "234-567-8901", "07/15/2024", "10:00", "AM", "07/15/2024", "11:00", "AM" };
        Project3.main(args);

        assertTrue(textFile.exists());
        try (BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
            String content = reader.readLine();
            assertThat(content, containsString("Customer"));
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
        Project3.main(args);

        String output = outContent.toString();
        assertThat(output, containsString("Customer: Customer"));
    }

    @Test
    void testInvalidPhoneNumberFormat() {
        String[] args = { "Customer", "1234567890", "234-567-8901", "07/15/2024", "10:00", "AM", "07/15/2024", "11:00", "AM" };
        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Invalid phone number format"));
    }

    @Test
    void testInvalidDateFormat() {
        String[] args = { "Customer", "123-456-7890", "234-567-8901", "07/15/2024", "10:00", "XX", "07/15/2024", "11:00", "AM" };
        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Invalid start date/time format"));
    }

    @Test
    void testEndTimeBeforeStartTime() {
        String[] args = { "Customer", "123-456-7890", "234-567-8901", "07/15/2024", "11:00", "AM", "07/15/2024", "10:00", "AM" };
        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("End time cannot be before start time"));
    }

    @Test
    void testMissingArguments() {
        String[] args = { "Customer", "123-456-7890", "234-567-8901" };
        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Missing required arguments"));
    }

    @Test
    void testHelpMessage() {
        String[] args = {};
        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Missing required arguments"));
    }

    @Test
    void testReadmeMessage() {
        String[] args = { "-README" };
        Project3.main(args);

        String output = outContent.toString();
        assertThat(output, containsString("README for Project3"));
    }

    @Test
    void testUnknownCommandLineOption() {
        String[] args = { "-unknown" };
        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Unknown command line option: -unknown"));
    }

    @Test
    void testTextFileWithoutFilename() {
        String[] args = { "-textFile" };
        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Missing file name after -textFile"));
    }

    @Test
    void testPrettyPrintWithoutFilename() {
        String[] args = { "-pretty" };
        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Missing file name after -pretty"));
    }

    @Test
    void testCreatingAndReadingPhoneBill() throws IOException, ParserException {
        File textFile = new File(tempDir.toFile(), "testbill.txt");
        String[] createArgs = { "-textFile", textFile.getPath(), "Customer", "123-456-7890", "234-567-8901", "07/15/2024", "10:00", "AM", "07/15/2024", "11:00", "AM" };
        Project3.main(createArgs);

        assertTrue(textFile.exists());
        try (BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
            String content = reader.readLine();
            assertThat(content, containsString("Customer"));
        }

        String[] readArgs = { "-textFile", textFile.getPath(), "Customer", "345-678-9012", "456-789-0123", "07/15/2024", "11:30", "AM", "07/15/2024", "12:00", "PM" };
        Project3.main(readArgs);

        try (BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
            String content = reader.readLine();
            assertThat(content, containsString("Customer"));
        }
    }
}
