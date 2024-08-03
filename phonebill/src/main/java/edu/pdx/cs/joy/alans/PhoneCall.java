package edu.pdx.cs.joy.alans;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import edu.pdx.cs.joy.AbstractPhoneCall;

/**
 * PhoneCall This class receives String arguments passed in from the main
 * project method and stores the data as Strings. The class contains getter
 * methods to access the data members.
 */
public class PhoneCall extends AbstractPhoneCall implements Comparable<PhoneCall> {

    private final String caller;
    private final String callee;
    private final LocalDateTime beginTime;
    private final LocalDateTime endTime;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy h:mm a", Locale.US);

    /**
     * Creates a new <code>PhoneCall</code> with the specified details.
     *
     * @param caller The phone number of the caller
     * @param callee The phone number of the callee
     * @param beginTime The start time of the call
     * @param endTime The end time of the call
     */
    public PhoneCall(String caller, String callee, LocalDateTime beginTime, LocalDateTime endTime) {
        this.caller = caller;
        this.callee = callee;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }

    /**
     * Compares two PhoneCall objects by comparing their beginTime and caller.
     */
    @Override
    public int compareTo(PhoneCall other) {
        int timeComparison = this.beginTime.compareTo(other.getBeginTime());
        if (timeComparison != 0) {
            return timeComparison;
        }
        return this.caller.compareTo(other.getCaller());
    }

    /**
     * Getter for the caller String.
     */
    @Override
    public String getCaller() {
        return this.caller;
    }

    /**
     * Getter for the callee String.
     */
    @Override
    public String getCallee() {
        return this.callee;
    }

    /**
     * Getter for the beginTime in date format.
     */
    @Override
    public LocalDateTime getBeginTime() {
        return this.beginTime;
    }

    /**
     * Getter for the endTime in date format.
     */
    @Override
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    /**
     * Getter for the beginTime String.
     */
    @Override
    public String getBeginTimeString() {
        return beginTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT));
    }

    /**
     * Getter for the endTime String.
     */
    @Override
    public String getEndTimeString() {
        return endTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT));
    }

    /**
     * Returns the duration of the phone call in minutes.
     *
     * @return The duration in minutes
     */
    public long getDurationMinutes() {
        return Duration.between(beginTime, endTime).toMinutes();
    }
}
