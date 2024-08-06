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
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Project3EdgeCasesTest {

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
    void testBasicCommandLineArguments() {
        String[] args = { "-print", "Customer", "123-456-7890", "234-567-8901", "07/15/2024", "10:00", "AM", "07/15/2024", "11:00", "AM" };
        Project3.main(args);

        String output = outContent.toString();
        System.out.println("Output: " + output); // Debug print
        assertThat(output, containsString("Phone call from 123-456-7890 to 234-567-8901 from 7/15/2024 10:00 AM to 7/15/2024 11:00 AM"));
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
    void testMalformedTextFile() throws IOException {
        File textFile = new File(tempDir.toFile(), "malformed.txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter(textFile))) {
            writer.println("Customer");
            writer.println("Malformed data line");
        }

        String[] args = { "-textFile", textFile.getPath(), "Customer", "123-456-7890", "234-567-8901", "03/03/2024", "12:00", "PM", "03/03/2024", "04:00", "PM" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Malformed phone call entry"));
        restoreStreams();
    }

    @Test
    void testUnknownCommandLineOption() {
        String[] args = { "-unknownOption", "Customer", "123-456-7890", "234-567-8901", "03/03/2024", "12:00", "PM", "03/03/2024", "04:00", "PM" };
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        Project3.main(args);

        String output = errContent.toString();
        assertThat(output, containsString("Unknown command line option: -unknownOption"));
        restoreStreams();
    }
}
