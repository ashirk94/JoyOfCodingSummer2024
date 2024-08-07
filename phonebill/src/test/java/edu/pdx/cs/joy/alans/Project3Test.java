package edu.pdx.cs.joy.alans;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import edu.pdx.cs.joy.ParserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Project3Test {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Test
    public void testDifferentCustomerName() throws Exception {
        Files.createDirectories(Paths.get("alans"));
        File textFile = new File("alans/alans.txt");
        try (FileWriter writer = new FileWriter(textFile)) {
            writer.write("Project3\n");
            writer.write("123-456-7890 213-124-6311 01/05/2024 5:00 AM 01/05/2024 6:14 AM\n");
        }

        String[] args = {
                "-textFile", "alans/alans.txt",
                "DIFFERENT",
                "123-456-7890",
                "213-124-6311",
                "01/05/2024", "5:00", "am",
                "01/05/2024", "6:14", "am"
        };

        Project3.main(args);

        String errOutput = errContent.toString().trim();
        assertEquals("Customer name in file does not match specified customer. Expected: Project3, Provided: DIFFERENT", errOutput);

        System.setErr(originalErr);
        if (textFile.exists()) {
            textFile.delete();
        }
    }

    @Test
    public void testValidInput() throws Exception {
        Files.createDirectories(Paths.get("alans"));
        File textFile = new File("alans/alans.txt");
        try (FileWriter writer = new FileWriter(textFile)) {
            writer.write("Project3\n");
            writer.write("123-456-7890 213-124-6311 01/05/2024 5:00 AM 01/05/2024 6:14 AM\n");
        }

        String[] args = {
                "-textFile", "alans/alans.txt",
                "Project3",
                "123-456-7890",
                "345-134-6134",
                "01/04/2024", "10:00", "am",
                "01/04/2024", "11:30", "am"
        };

        Project3.main(args);

        String output = errContent.toString().trim();
        assertFalse(output.contains("Invalid"));

        System.setErr(originalErr);
        if (textFile.exists()) {
            textFile.delete();
        }
    }

    @Test
    public void testInvalidDateTimeFormat() throws Exception {
        Files.createDirectories(Paths.get("alans"));
        File textFile = new File("alans/alans.txt");
        try (FileWriter writer = new FileWriter(textFile)) {
            writer.write("Project3\n");
            writer.write("123-456-7890 213-124-6311 01/05/2024 5:00 AM 01/05/2024 6:14 AM\n");
        }

        String[] args = {
                "-textFile", "alans/alans.txt",
                "Project3",
                "123-456-7890",
                "153-234-2521",
                "01/07/2024", "7:00", "am",
                "01/ZZ/2024", "7:00", "pm"
        };

        Project3.main(args);

        String errOutput = errContent.toString().trim();
        assertTrue(errOutput.contains("Invalid end date/time format"));

        System.setErr(originalErr);
        if (textFile.exists()) {
            textFile.delete();
        }
    }

    @Test
    public void testStartingNewPhoneBillFile() throws Exception {
        Files.createDirectories(Paths.get("alans"));
        File textFile = new File("alans/alans.txt");
        if (textFile.exists()) {
            textFile.delete();
        }

        String[] args = {
                "-textFile", "alans/alans.txt",
                "Project3",
                "123-456-7890",
                "234-567-8901",
                "01/03/2024", "11:00", "am",
                "01/03/2024", "1:00", "pm"
        };

        Project3.main(args);

        assertTrue(Files.exists(textFile.toPath()));

        System.setOut(originalOut);
        if (textFile.exists()) {
            textFile.delete();
        }
    }

    @Test
    public void testUsingExistingPhoneBillFile() throws Exception {
        Files.createDirectories(Paths.get("alans"));
        File textFile = new File("alans/alans.txt");
        try (FileWriter writer = new FileWriter(textFile)) {
            writer.write("Project3\n");
            writer.write("123-456-7890 234-567-8901 01/03/2024 11:00 AM 01/03/2024 1:00 PM\n");
        }

        String[] args = {
                "-textFile", "alans/alans.txt",
                "Project3",
                "123-456-7890",
                "345-134-6134",
                "01/04/2024", "10:00", "am",
                "01/04/2024", "11:30", "am"
        };

        Project3.main(args);

        assertTrue(Files.exists(textFile.toPath()));

        System.setOut(originalOut);
        if (textFile.exists()) {
            textFile.delete();
        }
    }

    @Test
    public void testMalformattedEndDate() throws Exception {
        Files.createDirectories(Paths.get("alans"));
        File textFile = new File("alans/alans.txt");
        try (FileWriter writer = new FileWriter(textFile)) {
            writer.write("Project3\n");
            writer.write("123-456-7890 153-234-2521 01/07/2024 7:00 AM 01/07/2024 8:00 AM\n");
        }

        String[] args = {
                "-textFile", "alans/alans.txt",
                "Project3",
                "123-456-7890",
                "153-234-2521",
                "01/07/2024", "7:00", "am",
                "01/ZZ/2024", "7:00", "pm"
        };

        Project3.main(args);

        String errOutput = errContent.toString().trim();
        assertTrue(errOutput.contains("Invalid end date/time format"));

        System.setErr(originalErr);
        if (textFile.exists()) {
            textFile.delete();
        }
    }

    @Test
    public void testAddingAnotherPhoneCall() throws Exception {
        Files.createDirectories(Paths.get("alans"));
        File textFile = new File("alans/alans.txt");
        try (FileWriter writer = new FileWriter(textFile)) {
            writer.write("Project3\n");
            writer.write("123-456-7890 234-567-8901 01/03/2024 11:00 AM 01/03/2024 1:00 PM\n");
        }

        String[] args = {
                "-textFile", "alans/alans.txt",
                "-pretty", "-",
                "Project3",
                "123-456-7890",
                "124-351-4234",
                "12/08/2024", "8:00", "am",
                "12/08/2024", "8:15", "am"
        };

        Project3.main(args);

        String outOutput = outContent.toString().trim();
        assertTrue(outOutput.contains("Phone Calls:"));

        System.setOut(originalOut);
        if (textFile.exists()) {
            textFile.delete();
        }
    }

    @Test
    public void testPrettyPrintingPhoneCall() throws Exception {
        Files.createDirectories(Paths.get("alans"));
        File textFile = new File("alans/alans.txt");
        try (FileWriter writer = new FileWriter(textFile)) {
            writer.write("Project3\n");
            writer.write("123-456-7890 234-567-8901 01/03/2024 11:00 AM 01/03/2024 1:00 PM\n");
            writer.write("123-456-7890 345-134-6134 01/04/2024 10:00 AM 01/04/2024 11:30 AM\n");
            writer.write("123-456-7891 452-234-2125 01/03/2024 11:00 AM 01/03/2024 2:00 PM\n");
        }

        String[] args = {
                "-textFile", "alans/alans.txt",
                "-pretty", "-",
                "Project3",
                "123-456-7891",
                "452-234-2125",
                "01/03/2024", "11:00", "am",
                "01/03/2024", "2:00", "pm"
        };

        Project3.main(args);

        String outOutput = outContent.toString().trim();
        assertTrue(outOutput.contains("Phone Calls:"));

        System.setOut(originalOut);
        if (textFile.exists()) {
            textFile.delete();
        }
    }

    @Test
    public void testPrettyPrintingToFile() throws Exception {
        Files.createDirectories(Paths.get("alans"));
        File textFile = new File("alans/alans.txt");
        if (textFile.exists()) {
            textFile.delete();
        }
        File prettyFile = new File("alans/alans-pretty.txt");

        String[] args = {
                "-textFile", "alans/alans.txt",
                "-pretty", "alans/alans-pretty.txt",
                "Project3",
                "123-456-7890",
                "452-234-2125",
                "01/10/2024", "10:00", "am",
                "01/10/2024", "3:45", "pm"
        };

        Project3.main(args);

        assertTrue(Files.exists(prettyFile.toPath()));

        System.setOut(originalOut);
        if (textFile.exists()) {
            textFile.delete();
        }
        if (prettyFile.exists()) {
            prettyFile.delete();
        }
    }

    @Test
    public void testBeginTimeAfterEndTime() throws Exception {
        Files.createDirectories(Paths.get("alans"));
        File textFile = new File("alans/alans.txt");
        if (textFile.exists()) {
            textFile.delete();
        }

        String[] args = {
                "Test10",
                "123-456-7890",
                "452-234-2125",
                "01/10/2024", "10:00", "pm",
                "01/10/2024", "3:45", "pm"
        };

        Project3.main(args);

        String errOutput = errContent.toString().trim();
        assertTrue(errOutput.contains("End time cannot be before start time"));

        System.setErr(originalErr);
        if (textFile.exists()) {
            textFile.delete();
        }
    }

    @Test
    public void testAddingCallWithSameBeginTime() throws Exception {
        Files.createDirectories(Paths.get("alans"));
        File textFile = new File("alans/alans.txt");
        try (FileWriter writer = new FileWriter(textFile)) {
            writer.write("Project3\n");
            writer.write("123-456-7890 234-567-8901 01/03/2024 11:00 AM 01/03/2024 1:00 PM\n");
            writer.write("123-456-7890 345-134-6134 01/04/2024 10:00 AM 01/04/2024 11:30 AM\n");
        }

        String[] args = {
                "-textFile", "alans/alans.txt",
                "-pretty", "-",
                "Project3",
                "123-456-7891",
                "452-234-2125",
                "01/03/2024", "11:00", "am",
                "01/03/2024", "2:00", "pm"
        };

        Project3.main(args);

        String outOutput = outContent.toString().trim();
        assertTrue(outOutput.contains("Phone Calls:"));

        System.setOut(originalOut);
        if (textFile.exists()) {
            textFile.delete();
        }
    }

    @Test
    public void testInvalidPhoneNumber() throws ParserException, IOException {
        String[] args = {
                "-textFile", "alans/alans.txt",
                "Project3", "123-456-7890", "invalid-number",
                "01/04/2024", "10:00", "am",
                "01/04/2024", "11:30", "am"
        };
        Project3.processArgs(args);
        assertTrue(errContent.toString().contains("Invalid phone number format"));
    }

    @Test
    public void testMissingArguments() throws ParserException, IOException {
        String[] args = {
                "-textFile", "alans/alans.txt",
                "Project3", "123-456-7890", "345-134-6134",
                "01/04/2024", "10:00", "am"
        };
        Project3.processArgs(args);
        assertTrue(errContent.toString().contains("Missing required arguments"));
    }

    @Test
    public void testREADME() {
        String[] args = {"-README"};
        Project3.main(args);
        assertTrue(outContent.toString().contains("usage: java -jar target/phonebill-1.0.0.jar"));
    }

    @Test
    public void testPrintOption() throws Exception {
        Files.createDirectories(Paths.get("alans"));
        File textFile = new File("alans/alans.txt");
        try (FileWriter writer = new FileWriter(textFile)) {
            writer.write("Project3\n");
            writer.write("123-456-7890 213-124-6311 01/05/2024 5:00 AM 01/05/2024 6:14 AM\n");
        }

        String[] args = {
                "-textFile", "alans/alans.txt",
                "-print",
                "Project3",
                "123-456-7890",
                "345-134-6134",
                "01/04/2024", "10:00", "AM",
                "01/04/2024", "11:30", "AM"
        };

        Project3.main(args);

        String expectedOutput = "Phone call from 123-456-7890 to 345-134-6134 from 01/04/2024 10:00 AM to 01/04/2024 11:30 AM";
        String actualOutput = outContent.toString().trim();
        assertEquals(expectedOutput, actualOutput);

        System.setOut(originalOut);
        if (textFile.exists()) {
            textFile.delete();
        }
    }



    @Test
    public void testMissingArgumentsHandling() throws Exception {
        String[] args = {
                "-textFile", "alans/alans.txt",
                "Project3",
                "123-456-7890"
                // Missing callee number, start and end date/time
        };

        Project3.main(args);

        String errOutput = errContent.toString().trim();
        assertTrue(errOutput.contains("Missing required arguments"));

        System.setErr(originalErr);
    }

    @Test
    public void testEmptyTextFileHandling() throws Exception {
        Files.createDirectories(Paths.get("alans"));
        File textFile = new File("alans/alans.txt");
        if (textFile.exists()) {
            textFile.delete();
        }
        textFile.createNewFile();

        String[] args = {
                "-textFile", "alans/alans.txt",
                "Project3",
                "123-456-7890",
                "234-567-8901",
                "01/03/2024", "11:00", "am",
                "01/03/2024", "1:00", "pm"
        };

        Project3.main(args);

        assertTrue(Files.exists(textFile.toPath()));

        System.setOut(originalOut);
        if (textFile.exists()) {
            textFile.delete();
        }
    }

    @Test
    public void testNonExistentFileHandling() throws Exception {
        Files.createDirectories(Paths.get("alans"));
        File textFile = new File("alans/nonexistent.txt");
        if (textFile.exists()) {
            textFile.delete();
        }

        String[] args = {
                "-textFile", "alans/nonexistent.txt",
                "Project3",
                "123-456-7890",
                "234-567-8901",
                "01/03/2024", "11:00", "am",
                "01/03/2024", "1:00", "pm"
        };

        Project3.main(args);

        assertTrue(Files.exists(textFile.toPath()));

        System.setOut(originalOut);
        if (textFile.exists()) {
            textFile.delete();
        }
    }
}
