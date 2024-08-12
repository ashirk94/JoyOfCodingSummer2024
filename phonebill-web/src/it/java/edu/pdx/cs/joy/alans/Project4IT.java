package edu.pdx.cs.joy.alans;

import edu.pdx.cs.joy.InvokeMainTestCase;
import edu.pdx.cs.joy.UncaughtExceptionInMain;
import edu.pdx.cs.joy.web.HttpRequestHelper;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import java.util.Locale;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Project4IT extends InvokeMainTestCase {
    private static final String HOSTNAME = "localhost";
    private static final String PORT = System.getProperty("http.port", "8080");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a", Locale.US);

    private PhoneBillRestClient mockClient;

    @BeforeEach
    void setUp() {
        mockClient = mock(PhoneBillRestClient.class);
    }

    @Test
    void test0RemoveAllPhoneBills() throws IOException {
        doNothing().when(mockClient).removeAllPhoneBills();
        mockClient.removeAllPhoneBills();
        verify(mockClient, times(1)).removeAllPhoneBills();
    }

    @Test
    void test1NoCommandLineArguments() {
        MainMethodResult result = invokeMain(Project4.class);
        assertThat(result.getTextWrittenToStandardError(), containsString(Project4.MISSING_ARGS));
    }

    @Test
    void test2MissingCustomerName() {
        MainMethodResult result = invokeMain(Project4.class, "-host", HOSTNAME, "-port", PORT);
        assertThat(result.getTextWrittenToStandardError(), containsString("Missing customer name"));
    }
}
