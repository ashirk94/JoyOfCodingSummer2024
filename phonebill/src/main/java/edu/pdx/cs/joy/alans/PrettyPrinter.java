package edu.pdx.cs.joy.alans;

import java.io.PrintWriter;

/**
 * This class is responsible for pretty-printing a phone bill.
 */
public class PrettyPrinter {
    private final PrintWriter writer;

    /**
     * Creates a new <code>PrettyPrinter</code> that writes to the specified writer.
     *
     * @param writer The writer to write to
     */
    public PrettyPrinter(PrintWriter writer) {
        this.writer = writer;
    }

    /**
     * Pretty-prints the given phone bill.
     *
     * @param bill The phone bill to pretty-print
     */
    public void dump(PhoneBill bill) {
        writer.println("Customer: " + bill.getCustomer());
        writer.println("Phone Calls:");
        for (PhoneCall call : bill.getPhoneCalls()) {
            writer.println(String.format("%s called %s from %s to %s Duration: %d minutes",
                    call.getCaller(),
                    call.getCallee(),
                    DateFormatter.format(call.getBeginTime()),
                    DateFormatter.format(call.getEndTime()),
                    call.getDurationMinutes()));
        }
        writer.flush();
        writer.close();
    }
}
