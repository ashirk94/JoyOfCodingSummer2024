package edu.pdx.cs.joy.alans;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import edu.pdx.cs.joy.PhoneBillDumper;

/**
 * A class that dumps the contents of a PhoneBill to a text file.
 */
public class TextDumper implements PhoneBillDumper<PhoneBill> {
  private final Writer writer;

  /**
   * Constructs a new TextDumper that writes to the given Writer.
   * 
   * @param writer The writer to which the PhoneBill will be dumped
   */
  public TextDumper(Writer writer) {
    this.writer = writer;
  }

  /**
   * Dumps the contents of a PhoneBill to the writer.
   * 
   * @param bill The PhoneBill to be dumped
   * @throws IOException If an I/O error occurs while writing to the file
   */
  @Override
  public void dump(PhoneBill bill) throws IOException {
    try (PrintWriter pw = new PrintWriter(this.writer)) {
      pw.println(bill.getCustomer());
      for (PhoneCall call : bill.getPhoneCalls()) {
        pw.println(call.getCaller() + "," + call.getCallee() + "," + call.getBeginTimeString() + "," + call.getEndTimeString());
      }
      pw.flush();
    }
  }
}
