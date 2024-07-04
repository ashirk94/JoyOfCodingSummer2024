package edu.pdx.cs.joy.alans;

import edu.pdx.cs.joy.AppointmentBookDumper;
import edu.pdx.cs.joy.PhoneBillDumper;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * A skeletal implementation of the <code>TextDumper</code> class for Project 2.
 */
public class TextDumper implements PhoneBillDumper<PhoneBill> {
  private final Writer writer;

  public TextDumper(Writer writer) {
    this.writer = writer;
  }

  @Override
  public void dump(PhoneBill bill) {
    try (
      PrintWriter pw = new PrintWriter(this.writer)
    ) {
      pw.println(bill.getCustomer());

      pw.flush();
    }
  }
}
