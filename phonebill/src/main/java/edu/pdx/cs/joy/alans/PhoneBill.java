package edu.pdx.cs.joy.alans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import edu.pdx.cs.joy.AbstractPhoneBill;

/**
 * PhoneBill
 * This class takes in a customer String and creates an ArrayList of PhoneCalls.
 * It can also add a new PhoneCall to the phoneCalls ArrayList when one is entered into the command line.
 */
public class PhoneBill extends AbstractPhoneBill<PhoneCall> {
  private final String customer;
  private final Collection<PhoneCall> phoneCalls;

  /**
   * Creates a new <code>PhoneBill</code> with the specified customer.
   *
   * @param customer The name of the customer
   */
  public PhoneBill(String customer) {
    this.customer = customer;
    this.phoneCalls = new TreeSet<>((call1, call2) -> {
      int comparison = call1.getBeginTime().compareTo(call2.getBeginTime());
      if (comparison == 0) {
        return call1.getCaller().compareTo(call2.getCaller());
      }
      return comparison;
    });
  }
  /**
   * Getter for the customer String.
   */
  @Override
  public String getCustomer() {
    return this.customer;
  }

  /**
   * Adds a PhoneCall to the phoneCalls ArrayList.
   */
  @Override
  public void addPhoneCall(PhoneCall call) {
    this.phoneCalls.add(call);
  }

  /**
   * Getter for the phoneCalls ArrayList.
   */
  @Override
  public Collection<PhoneCall> getPhoneCalls() {
    return this.phoneCalls;
  }
}
