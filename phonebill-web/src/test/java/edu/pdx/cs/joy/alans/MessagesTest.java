package edu.pdx.cs.joy.alans;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessagesTest {

    @Test
    public void testMissingRequiredParameter() {
        String result = Messages.missingRequiredParameter("customer");
        assertEquals("The required parameter \"customer\" is missing", result);
    }

    @Test
    public void testCreatedPhoneCall() {
        String result = Messages.createdPhoneCall("Dave", "503-245-2345");
        assertEquals("Created a new phone call for Dave with caller 503-245-2345", result);
    }

    @Test
    public void testAllPhoneBillsDeleted() {
        String result = Messages.allPhoneBillsDeleted();
        assertEquals("All phone bills have been deleted", result);
    }
}
