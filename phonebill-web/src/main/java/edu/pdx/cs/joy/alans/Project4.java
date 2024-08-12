package edu.pdx.cs.joy.alans;

import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.web.HttpRequestHelper;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * The main class that parses the command line and communicates with the
 * Phone Bill server using REST.
 */
public class Project4 {

    public static final String MISSING_ARGS = "Missing command line arguments";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a", Locale.US);

    public static void main(String... args) {
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
            usage("Missing command line arguments");
            return;
        }

        if (args.length > 20) {
            usage("Too many command line arguments");
            return;
        }

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-host")) {
                hostName = args[++i];
            } else if (arg.equals("-port")) {
                portString = args[++i];
            } else if (arg.equals("-search")) {
                search = true;
            } else if (arg.equals("-print")) {
                print = true;
            } else if (customer == null) {
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
                System.err.println("Unexpected argument: " + arg);
                return;
            }
        }

        String begin = beginDate + " " + beginTime + " " + beginAmPm;
        String end = endDate + " " + endTime + " " + endAmPm;

        if (customer == null) {
            System.err.println("** Missing customer name");
            return;
        }

        if (hostName == null) {
            System.err.println("** Missing host name");
            return;
        }

        if (portString == null) {
            System.err.println("** Missing port");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portString);
        } catch (NumberFormatException ex) {
            System.err.println("** Port \"" + portString + "\" must be an integer");
            return;
        }

        PhoneBillRestClient client = createPhoneBillRestClient(hostName, port);

        PrettyPrinter printer = new PrettyPrinter(new PrintWriter(System.out));

        try {
            if (search) {
                if (begin == null || end == null) {
                    usage("Both begin and end dates are required for search");
                }
                LocalDateTime start = LocalDateTime.parse(begin, formatter);
                LocalDateTime endDateTime = LocalDateTime.parse(end, formatter);
                PhoneBill bill = client.getPhoneBillForCustomer(customer);
                printer.printPhoneCallsBetween(bill, start, endDateTime);
            } else {
                if (caller != null && callee != null && begin != null && end != null) {
                    LocalDateTime start = LocalDateTime.parse(begin, formatter);
                    LocalDateTime endDateTime = LocalDateTime.parse(end, formatter);

                    try {
                        client.addPhoneCallToBill(customer, new PhoneCall(caller, callee, start, endDateTime));
                    } catch (HttpRequestHelper.RestException e) {
                        System.err.println("Error: " + e.getMessage());
                        return;
                    }
                    if (print) {
                        System.out.println("Added new phone call:");
                        printer.printPhoneCall(new PhoneCall(caller, callee, start, endDateTime));
                    }
                } else {
                    PhoneBill bill = client.getPhoneBillForCustomer(customer);
                    printer.printPhoneBill(bill);
                }
            }
        } catch (IOException | ParserException ex) {
            error("While contacting server: " + ex.getMessage());
        }
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

    private static void error(String message) {
        PrintStream err = System.err;
        err.println("** " + message);
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
        err.println("This program interacts with a phone bill server.");
        err.println();
    }
}
