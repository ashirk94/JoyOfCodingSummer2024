package edu.pdx.cs.joy.alans;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import com.google.common.annotations.VisibleForTesting;

import edu.pdx.cs.joy.ParserException;

/**
 * The main class for Project3, which processes command line arguments, manages phone bills and phone calls, and handles file operations.
 */
public class Project3 {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a", Locale.US);

    /**
     * The main method for Project3.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Checks for null arguments array
        if (args == null) {
            System.err.println("Error: Arguments cannot be null");
            printUsage();
            return;
        }

        // Checking for README flag first
        for (String arg : args) {
            if (arg.equals("-README")) {
                printREADME();
                return;
            }
        }

        // Processes other arguments
        try {
            processArgs(args);
        } catch (RuntimeException | IOException | ParserException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Processes the command line arguments and contains the primary logic for the program.
     *
     * @param args Command line arguments
     * @throws IOException If an I/O error occurs during file operations
     * @throws ParserException If an error occurs while parsing the phone bill data
     */
    @VisibleForTesting
    static void processArgs(String[] args) throws IOException, ParserException {
        boolean printCall = false;
        String textFile = null;
        String prettyFile = null;
        String customer = null;
        String callerNumber = null;
        String calleeNumber = null;
        String startDateTime = null;
        String endDateTime = null;

        int i = 0;

        // Ignore the first argument (command name) if it is present
        if (args.length > 0 && args[0].equals("Project3")) {
            i++;
        }

        // Process options first
        while (i < args.length && args[i].startsWith("-")) {
            switch (args[i]) {
                case "-print":
                    printCall = true;
                    break;
                case "-textFile":
                    if (i + 1 < args.length) {
                        textFile = args[++i];
                    } else {
                        System.err.println("Missing file name after -textFile");
                        printUsage();
                        return;
                    }
                    break;
                case "-pretty":
                    if (i + 1 < args.length) {
                        prettyFile = args[++i];
                    } else {
                        System.err.println("Missing file name after -pretty");
                        printUsage();
                        return;
                    }
                    break;
                case "-README":
                    printREADME();
                    return;
                default:
                    System.err.println("Unknown command line option: " + args[i]);
                    printUsage();
                    return;
            }
            i++;
        }

        // Check if there are enough remaining arguments for positional parameters
        if (args.length - i < 8) {
            System.err.println("Missing required arguments\n");
            printUsage();
            return;
        }

        // Process positional arguments
        customer = args[i++];
        callerNumber = args[i++];
        calleeNumber = args[i++];
        String startDate = args[i++];
        String startTime = args[i++];
        String startPeriod = args[i++].toUpperCase(Locale.ENGLISH); // Convert to uppercase
        String endDate = args[i++];
        String endTime = args[i++];
        String endPeriod = args[i++].toUpperCase(Locale.ENGLISH); // Convert to uppercase

        startDateTime = startDate + " " + startTime + " " + startPeriod;
        endDateTime = endDate + " " + endTime + " " + endPeriod;

        if (!isValidPhoneNumber(callerNumber) || !isValidPhoneNumber(calleeNumber)) {
            System.err.println("Invalid phone number format");
            return;
        }

        if (!isValidDateTime(startDateTime)) {
            System.err.println("Invalid start date/time format");
            return;
        }

        if (!isValidDateTime(endDateTime)) {
            System.err.println("Invalid end date/time format");
            return;
        }

        LocalDateTime start = parseDateTime(startDateTime);
        LocalDateTime end = parseDateTime(endDateTime);

        if (isEndTimeBeforeStartTime(start, end)) {
            System.err.println("End time cannot be before start time");
            return;
        }

        PhoneBill bill = new PhoneBill(customer);

        if (textFile != null) {
            File file = new File(textFile);
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    TextParser parser = new TextParser(reader);
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

        PhoneCall call = new PhoneCall(callerNumber, calleeNumber, start, end);
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
            try (PrintWriter writer = "-".equals(prettyFile) ? new PrintWriter(System.out) : new PrintWriter(new FileWriter(prettyFile))) {
                PrettyPrinter prettyPrinter = new PrettyPrinter(writer);
                prettyPrinter.dump(bill);
            } catch (IOException e) {
                System.err.println("Could not write to pretty print file: " + e.getMessage());
            }
        }
    }

    /**
     * Validates the format of a phone number.
     *
     * @param phoneNumber The phone number to validate
     * @return true if the phone number is valid, false otherwise
     */
    @VisibleForTesting
    static boolean isValidPhoneNumber(String phoneNumber) {
        if (!phoneNumber.matches("\\d{3}-\\d{3}-\\d{4}")) {
            System.err.println("Invalid phone number: " + phoneNumber);
            return false;
        }
        return true;
    }

    /**
     * Validates the format of a date and time string.
     *
     * @param dateTimeString The date and time string to validate
     * @return true if the date and time are valid, false otherwise
     */
    @VisibleForTesting
    static boolean isValidDateTime(String dateTimeString) {
        try {
            LocalDateTime.parse(dateTimeString, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Parses a date and time string into a LocalDateTime object.
     *
     * @param dateTimeString The date and time string to parse
     * @return The parsed LocalDateTime object
     */
    @VisibleForTesting
    static LocalDateTime parseDateTime(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, formatter);
    }

    /**
     * Checks if the end time is before the start time.
     *
     * @param startDateTime The start date-time string
     * @param endDateTime The end date-time string
     * @return true if the end time is before the start time, false otherwise
     */
    @VisibleForTesting
    static boolean isEndTimeBeforeStartTime(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return endDateTime.isBefore(startDateTime);
    }

    /**
     * Prints the README text from the README file.
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
     * Prints the usage information for the program.
     */
    private static void printUsage() {
        System.out.println("Usage: java -jar phonebill-1.0.0.jar Project3 [options] <customer> <caller> <callee> <startDate> <startTime> <startPeriod> <endDate> <endTime> <endPeriod>");
        System.out.println("Options:");
        System.out.println("  -print              Print the phone call details");
        System.out.println("  -textFile <file>    Specify a text file for phone bill data");
        System.out.println("  -pretty <file>      Specify a file for pretty printing output");
        System.out.println("  -README             Print README and exit");
    }
}
