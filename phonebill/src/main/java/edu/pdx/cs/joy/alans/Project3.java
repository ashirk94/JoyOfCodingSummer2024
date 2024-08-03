package edu.pdx.cs.joy.alans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.google.common.annotations.VisibleForTesting;

import edu.pdx.cs.joy.ParserException;

/**
 * This class represents the main entry point for the phone bill application.
 * It processes command line arguments, handles file operations, and prints phone bill details.
 */
public class Project3 {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");

    /**
     * The main method that serves as the entry point for the application.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        for (String arg : args) {
            if (arg.equals("-README")) {
                printREADME();
                return;
            }
        }

        try {
            processArgs(args);
        } catch (RuntimeException | IOException | ParserException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Processes the command line arguments and performs the corresponding actions.
     *
     * @param args Command line arguments
     * @throws IOException If an I/O error occurs
     * @throws ParserException If a parsing error occurs
     */
    @VisibleForTesting
    static void processArgs(String[] args) throws IOException, ParserException {
        if (args.length == 0) {
            System.err.println("Missing command line arguments\n");
            printUsage();
            return;
        }

        boolean printCall = false;
        String textFile = null;
        String prettyFile = null;
        String customer = null;
        String callerNumber = null;
        String calleeNumber = null;
        String startDate = null;
        String startTime = null;
        String endDate = null;
        String endTime = null;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-print")) {
                printCall = true;
            } else if (arg.equals("-textFile")) {
                if (i + 1 < args.length) {
                    textFile = args[++i];
                } else {
                    System.err.println("Missing file name after -textFile");
                    return;
                }
            } else if (arg.equals("-pretty")) {
                if (i + 1 < args.length) {
                    prettyFile = args[++i];
                } else {
                    System.err.println("Missing file name after -pretty");
                    return;
                }
            } else if (arg.equals("-README")) {
                printREADME();
                return;
            } else if (arg.startsWith("-")) {
                System.err.println("Unknown command line option: " + arg);
                return;
            } else {
                if (customer == null) {
                    customer = arg;
                } else if (callerNumber == null) {
                    callerNumber = arg;
                } else if (calleeNumber == null) {
                    calleeNumber = arg;
                } else if (startDate == null) {
                    startDate = arg;
                } else if (startTime == null) {
                    startTime = arg;
                } else if (endDate == null) {
                    endDate = arg;
                } else if (endTime == null) {
                    endTime = arg;
                } else {
                    System.err.println("Extraneous command line argument: " + arg);
                    return;
                }
            }
        }

        if (customer == null || callerNumber == null || calleeNumber == null || startDate == null || startTime == null || endDate == null || endTime == null) {
            System.err.println("Missing required command line arguments");
            return;
        }

        if (!isValidPhoneNumber(callerNumber) || !isValidPhoneNumber(calleeNumber)) {
            System.err.println("Invalid phone number format");
            return;
        }

        if (!isValidDateTime(startDate, startTime) || !isValidDateTime(endDate, endTime)) {
            System.err.println("Invalid date/time format");
            return;
        }

        if (isEndTimeBeforeStartTime(startDate, startTime, endDate, endTime)) {
            System.err.println("End time cannot be before begin time");
            return;
        }

        PhoneBill bill = new PhoneBill(customer);

        if (textFile != null) {
            File file = new File(textFile);
            if (file.exists()) {
                try {
                    TextParser parser = new TextParser(new BufferedReader(new FileReader(file)));
                    bill = parser.parse();
                } catch (IOException | ParserException e) {
                    System.err.println("Could not read from text file: " + e.getMessage());
                    return;
                }

                if (!bill.getCustomer().equals(customer)) {
                    System.err.println("Customer name in file does not match specified customer. Expected: " + bill.getCustomer() + ", Provided: " + customer);
                    return;
                }
            } else {
                System.out.println("File not found. Creating a new file: " + textFile);
            }
        }

        LocalDateTime startDateTime = DateFormatter.parse(startDate + " " + startTime);
        LocalDateTime endDateTime = DateFormatter.parse(endDate + " " + endTime);

        PhoneCall call = new PhoneCall(callerNumber, calleeNumber, startDateTime, endDateTime);
        bill.addPhoneCall(call);

        if (printCall) {
            System.out.println(call);
        }

        if (textFile != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(textFile))) {
                TextDumper dumper = new TextDumper(writer);
                dumper.dump(bill);
            } catch (IOException e) {
                System.err.println("Could not write to text file: " + e.getMessage());
            }
        }

        if (prettyFile != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(prettyFile))) {
                PrettyPrinter printer = new PrettyPrinter(writer);
                printer.dump(bill);
            } catch (IOException e) {
                System.err.println("Could not write to pretty file: " + e.getMessage());
            }
        }
    }

    /**
     * Validates a phone number.
     *
     * @param phoneNumber The phone number to validate
     * @return true if the phone number is valid, false otherwise
     */
    @VisibleForTesting
    static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\d{3}-\\d{3}-\\d{4}");
    }

    /**
     * Validates a date and time string.
     *
     * @param date The date string to validate
     * @param time The time string to validate
     * @return true if the date and time are valid, false otherwise
     */
    @VisibleForTesting
    static boolean isValidDateTime(String date, String time) {
        return date.matches("\\d{1,2}/\\d{1,2}/\\d{4}") && time.matches("\\d{1,2}:\\d{2}\\s?[aApP][mM]");
    }

    /**
     * Checks if the end time is before the start time.
     *
     * @param startDate The start date string
     * @param startTime The start time string
     * @param endDate The end date string
     * @param endTime The end time string
     * @return true if the end time is before the start time, false otherwise
     */
    @VisibleForTesting
    static boolean isEndTimeBeforeStartTime(String startDate, String startTime, String endDate, String endTime) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate + " " + startTime, formatter);
            LocalDateTime end = LocalDateTime.parse(endDate + " " + endTime, formatter);
            return end.isBefore(start);
        } catch (DateTimeParseException e) {
            System.err.println("Error parsing date/time: " + e.getMessage());
            return false;
        }
    }

    /**
     * Prints the README file content to the console.
     */
    private static void printREADME() {
        try (InputStream readme = Project3.class.getResourceAsStream("/edu/pdx/cs/joy/alans/README.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(readme))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading README: " + e.getMessage());
        }
    }

    /**
     * Prints the usage instructions to the console.
     */
    private static void printUsage() {
        System.out.println("usage: java -jar target/phonebill-1.0.0.jar [options] <args>");
        System.out.println("    args are (in this order):");
        System.out.println("        customer        The person whose phone bill we’re modeling");
        System.out.println("        callerNumber    Phone number of the caller (format: nnn-nnn-nnnn)");
        System.out.println("        calleeNumber    Phone number of the person who was called (format: nnn-nnn-nnnn)");
        System.out.println("        begin           Date and time the call began (format: mm/dd/yyyy hh:mm am/pm)");
        System.out.println("        end             Date and time the call ended (format: mm/dd/yyyy hh:mm am/pm)");
        System.out.println("    options are (options may appear in any order):");
        System.out.println("        -print          Prints a description of the new phone call");
        System.out.println("        -textFile file  Where to read/write the phone bill");
        System.out.println("        -pretty file    Pretty print the phone bill to a text file or - for standard out");
        System.out.println("        -README         Prints a README for this project and exits");
    }
}
