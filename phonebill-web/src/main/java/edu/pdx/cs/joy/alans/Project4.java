package edu.pdx.cs.joy.alans;

import edu.pdx.cs.joy.ParserException;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * The main class that parses the command line and communicates with the
 * Phone Bill server using REST.
 */
public class Project4 {

    public static final String MISSING_ARGS = "Missing command line arguments";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a", Locale.US);

    /**
     * The main method that drives the program. It parses command line arguments
     * and communicates with the Phone Bill server.
     *
     * @param args The command line arguments
     */
    public static void main(String... args) {
        Project4 project = new Project4();
        int result = project.processArgs(args);
    }

    /**
     * Processes command line arguments and executes the appropriate actions.
     * This method handles error checking and avoids throwing exceptions from main.
     *
     * @param args The command line arguments
     * @return 0 if the process completes successfully, 1 if an error occurs
     */
    public int processArgs(String... args) {
        String hostName = null;
        String portString = null;
        String customer = null;
        String caller = null;
        String callee = null;
        String beginDate = null;
        String beginTime = null;
        String beginAmPm = null;
        String endDate = null;
        String endTime = null;
        String endAmPm = null;
        boolean search = false;
        boolean print = false;

        if (args.length == 0) {
            usage(MISSING_ARGS);
            return 1;
        }

        for (String arg : args) {
            if (arg.equals("-README")) {
                printREADME();
                return 0;
            }
        }

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "-host":
                    hostName = args[++i];
                    break;
                case "-port":
                    portString = args[++i];
                    break;
                case "-search":
                    search = true;
                    break;
                case "-print":
                    print = true;
                    break;
                default:
                    if (customer == null) {
                        customer = arg;
                    } else if (caller == null) {
                        caller = arg;
                    } else if (callee == null) {
                        callee = arg;
                    } else if (beginDate == null) {
                        beginDate = arg;
                    } else if (beginTime == null) {
                        beginTime = arg;
                    } else if (beginAmPm == null) {
                        beginAmPm = arg.toUpperCase();
                    } else if (endDate == null) {
                        endDate = arg;
                    } else if (endTime == null) {
                        endTime = arg;
                    } else if (endAmPm == null) {
                        endAmPm = arg.toUpperCase();
                    } else {
                        error("Unexpected argument: " + arg);
                        return 1;
                    }
                    break;
            }
        }

        if (customer == null) {
            error("** Missing customer name");
            return 1;
        }

        if (hostName == null) {
            error("** Missing host name");
            return 1;
        }

        if (portString == null) {
            error("** Missing port");
            return 1;
        }

        int port;
        try {
            port = Integer.parseInt(portString);
        } catch (NumberFormatException ex) {
            error("** Port \"" + portString + "\" must be an integer");
            return 1;
        }

        try {
            PhoneBillRestClient client = createPhoneBillRestClient(hostName, port);
            PrettyPrinter printer = new PrettyPrinter(new PrintWriter(System.out));

            if (search) {
                if (beginDate == null || beginTime == null || beginAmPm == null || endDate == null || endTime == null || endAmPm == null) {
                    // No dates provided, pretty print the entire phone bill
                    PhoneBill bill = client.getPhoneBillForCustomer(customer);
                    printer.printPhoneBill(bill);
                } else {
                    String begin = beginDate + " " + beginTime + " " + beginAmPm;
                    String end = endDate + " " + endTime + " " + endAmPm;

                    LocalDateTime start = LocalDateTime.parse(begin, formatter);
                    LocalDateTime endDateTime = LocalDateTime.parse(end, formatter);

                    PhoneBill bill = client.getPhoneBillForCustomer(customer);
                    printer.printPhoneCallsBetween(bill, start, endDateTime);
                }
            } else {
                if (caller != null && callee != null && beginDate != null && endDate != null) {
                    LocalDateTime start = LocalDateTime.parse(beginDate + " " + beginTime + " " + beginAmPm, formatter);
                    LocalDateTime end = LocalDateTime.parse(endDate + " " + endTime + " " + endAmPm, formatter);
                    client.addPhoneCallToBill(customer, new PhoneCall(caller, callee, start, end));
                    if (print) {
                        System.out.println("Added new phone call:");
                        printer.printPhoneCall(new PhoneCall(caller, callee, start, end));
                    }
                } else {
                    PhoneBill bill = client.getPhoneBillForCustomer(customer);
                    printer.printPhoneBill(bill);
                }
            }
        } catch (DateTimeParseException e) {
            error("Invalid date/time format: " + e.getMessage());
            return 1;
        } catch (IOException | ParserException e) {
            error("While contacting server: " + e.getMessage());
            return 1;
        } catch (RuntimeException e) {
            error("Runtime error: " + e.getMessage());
            return 1;
        }

        return 0;
    }

    /**
     * Creates a new instance of PhoneBillRestClient. This method can be overridden in tests to return a mock.
     *
     * @param hostName The name of the host
     * @param port The port number
     * @return A new instance of PhoneBillRestClient
     */
    protected static PhoneBillRestClient createPhoneBillRestClient(String hostName, int port) {
        return new PhoneBillRestClient(hostName, port);
    }

    /**
     * Prints an error message to the standard error stream.
     *
     * @param message The error message to print
     */
    private static void error(String message) {
        PrintStream err = System.err;
        err.println("** " + message);
    }

    /**
     * Prints the README text from the README file.
     */
    private static void printREADME() {
        try (InputStream readme = Project4.class.getResourceAsStream("/edu/pdx/cs/joy/alans/README.txt");
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
     * Prints usage information for this program and exits.
     *
     * @param message An error message to print
     */
    private static void usage(String message) {
        PrintStream err = System.err;
        err.println("** " + message);
        err.println();
        err.println("usage: java Project4 -host hostname -port port [options] <args>");
        err.println("  host         Host of web server");
        err.println("  port         Port of web server");
        err.println("  -search      Search for phone calls within a time range");
        err.println("  -print       Prints a description of the new phone call");
        err.println("  customer     Customer name");
        err.println("  callerNumber Phone number of caller");
        err.println("  calleeNumber Phone number of person who was called");
        err.println("  begin        Date and time call began (MM/dd/yyyy h:mm a)");
        err.println("  end          Date and time call ended (MM/dd/yyyy h:mm a)");
        err.println();
    }
}
