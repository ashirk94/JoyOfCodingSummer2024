package edu.pdx.cs.joy.alans;

/**
 * Class for formatting messages on the server side.  This is mainly to enable
 * test methods that validate that the server returned expected strings.
 */
public class Messages {
    public static String missingRequiredParameter(String parameterName) {
        return String.format("The required parameter \"%s\" is missing", parameterName);
    }

    public static String createdPhoneCall(String customer, String caller) {
        return String.format("Created a new phone call for %s with caller %s", customer, caller);
    }

    public static String allPhoneBillsDeleted() {
        return "All phone bills have been deleted";
    }
}
