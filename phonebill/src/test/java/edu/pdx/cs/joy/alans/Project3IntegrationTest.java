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

import static org.junit.jupiter.api.Assertions.*;

public class Project3IntegrationTest {

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
        // Clean up after each test
        Files.walk(tempDir)
                .map(Path::toFile)
                .forEach(File::delete);
    }

    public void restoreStreams() {
        System.setErr(originalErr);
        System.setOut(originalOut);
    }

    @Disabled
    @Test
    public void testHelpMessage() {
        String[] args = {};
        try {
            Project3.main(args);
        } catch (Exception e) {
            // Ignore exception
        }
        assertTrue(errContent.toString().contains("Missing command line arguments"));
        restoreStreams();
    }

    @Test
    public void testReadme() {
        String[] args = {"-README"};
        setUpStreams();
        Project3.main(args);
        String output = outContent.toString(); // Capture the content printed to System.out
        assertTrue(output.contains("README for Project3"), "README should contain the project description");
        restoreStreams();
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
    public void testExtraneousArgument() {
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
