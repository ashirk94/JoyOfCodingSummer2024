package edu.pdx.cs.joy.alans;

import java.util.*;

import edu.pdx.cs.joy.AbstractPhoneBill;

/**
 * PhoneBill
 * This class takes in a customer String and creates an ArrayList of PhoneCalls.
 * It can also add a new PhoneCall to the phoneCalls ArrayList when one is entered into the command line.
 */
public class PhoneBill extends AbstractPhoneBill<PhoneCall> {
    private String customer;
    private final List<PhoneCall> calls = new ArrayList<>();

    /**
     * Creates a new <code>PhoneBill</code> with the specified customer.
     *
     * @param customer The name of the customer
     */
    public PhoneBill(String customer) {
        this.customer = customer;
    }

    /**
     * Getter for the customer String.
     */
    @Override
    public String getCustomer() {
        return this.customer;
    }

    /**
     * Adds a PhoneCall to the phoneCalls ArrayList and sorts the list by the start time and caller number.
     */
    @Override
    public void addPhoneCall(PhoneCall call) {
        this.calls.add(call);
        // Sorts the calls list after adding a new call
        this.calls.sort(Comparator.comparing(PhoneCall::getBeginTime).thenComparing(PhoneCall::getCaller));
    }

    /**
     * Getter for the phoneCalls ArrayList.
     */
    @Override
    public Collection<PhoneCall> getPhoneCalls() {
        return this.calls;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PhoneBill) {
            PhoneBill other = (PhoneBill) o;
            return this.customer.equals(other.customer) && this.calls.equals(other.calls);
        }
        return false;
    }
}
