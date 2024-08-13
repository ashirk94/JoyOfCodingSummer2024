package edu.pdx.cs.joy.alans;

import edu.pdx.cs.joy.ParserException;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mockStatic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
class Project4IT {
    private static final String HOSTNAME = "localhost";
    private static final String PORT = System.getProperty("http.port", "8080");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a", Locale.US);

    private PhoneBillRestClient mockClient;
    private ByteArrayOutputStream errContent;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        mockClient = mock(PhoneBillRestClient.class);
        errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setErr(originalErr);
        System.setOut(originalOut);
    }

    @Test
    void testUsageDisplaysWhenNoArguments() {
        Project4 project = new Project4();
        int result = project.processArgs();

        assertEquals(1, result);
        assertThat(errContent.toString(), containsString("Missing command line arguments"));
        assertThat(errContent.toString(), containsString("usage: java Project4"));
    }

    @Test
    void testReadmeOptionDisplaysReadme() {
        Project4 project = new Project4();
        int result = project.processArgs("-README");

        assertEquals(0, result);
        assertThat(outContent.toString(), containsString("README for Project4"));
    }

    @Test
    void test0RemoveAllPhoneBills() throws Exception {
        doNothing().when(mockClient).removeAllPhoneBills();
        mockClient.removeAllPhoneBills();
        verify(mockClient, times(1)).removeAllPhoneBills();
    }

    @Test
    void test1NoCommandLineArguments() {
        Project4 project = new Project4();
        int result = project.processArgs();
        assertEquals(1, result);
        assertThat(errContent.toString(), containsString("Missing command line arguments"));
    }

    @Test
    void test2MissingCustomerName() {
        Project4 project = new Project4();
        int result = project.processArgs("-host", HOSTNAME, "-port", PORT);
        assertEquals(1, result);
        assertThat(errContent.toString(), containsString("Missing customer name"));
    }

    @Test
    void test3InvalidCommandLineSyntax() {
        Project4 project = new Project4();
        int result = project.processArgs("-host", "localhost");
        assertEquals(1, result);
        assertThat(errContent.toString(), containsString("Missing customer name"));
    }

    @Test
    void test4InvalidPortNumber() {
        Project4 project = new Project4();
        int result = project.processArgs("-host", HOSTNAME, "-port", "invalidPort", "Customer");
        assertEquals(1, result);
        assertThat(errContent.toString(), containsString("Port \"invalidPort\" must be an integer"));
    }

    @Test
    void testSearchWithoutDateArguments() throws ParserException, IOException {
        PhoneBill mockBill = new PhoneBill("Dave");
        mockBill.addPhoneCall(new PhoneCall("503-245-2345", "765-389-1273",
                LocalDateTime.parse("02/27/2024 8:56 AM", formatter),
                LocalDateTime.parse("02/27/2024 10:28 AM", formatter)));

        when(mockClient.getPhoneBillForCustomer(anyString())).thenReturn(mockBill);

        try (MockedStatic<Project4> mocked = mockStatic(Project4.class)) {
            mocked.when(() -> Project4.createPhoneBillRestClient(anyString(), anyInt()))
                    .thenReturn(mockClient);

            Project4 project = new Project4();
            int result = project.processArgs("-host", HOSTNAME, "-port", PORT, "-search", "Dave");

            assertEquals(0, result);
            assertThat(outContent.toString(), containsString("Customer: Dave"));
            assertThat(outContent.toString(), containsString("Caller: 503-245-2345, Callee: 765-389-1273"));
        }
    }

    @Test
    void testSearchWithDateArguments() throws ParserException, IOException {
        PhoneBill mockBill = new PhoneBill("Dave");
        mockBill.addPhoneCall(new PhoneCall("503-245-2345", "765-389-1273",
                LocalDateTime.parse("02/27/2024 8:56 AM", formatter),
                LocalDateTime.parse("02/27/2024 10:28 AM", formatter)));

        when(mockClient.getPhoneBillForCustomer(anyString())).thenReturn(mockBill);

        try (MockedStatic<Project4> mocked = mockStatic(Project4.class)) {
            mocked.when(() -> Project4.createPhoneBillRestClient(anyString(), anyInt()))
                    .thenReturn(mockClient);

            Project4 project = new Project4();
            int result = project.processArgs("-host", HOSTNAME, "-port", PORT, "-search", "Dave",
                    "02/01/2024", "12:00", "AM", "03/01/2024", "11:59", "PM");

            assertEquals(0, result);
            assertThat(outContent.toString(), containsString("Customer: Dave"));
            assertThat(outContent.toString(), containsString("Caller: 503-245-2345, Callee: 765-389-1273"));
        }
    }



    @Test
    void testInvalidDateFormat() {
        Project4 project = new Project4();
        int result = project.processArgs("-host", HOSTNAME, "-port", PORT, "Customer",
                "503-245-2345", "765-389-1273", "invalidDate", "8:56", "AM", "02/27/2024", "10:27", "AM");
        assertEquals(1, result);
        assertThat(errContent.toString(), containsString("Invalid date/time format"));
    }

    @Test
    void testPrintPhoneCall() throws Exception {
        PhoneBill mockBill = new PhoneBill("Customer");
        mockBill.addPhoneCall(new PhoneCall("503-245-2345", "765-389-1273",
                LocalDateTime.parse("02/27/2024 8:56 AM", formatter),
                LocalDateTime.parse("02/27/2024 10:27 AM", formatter)));

        when(mockClient.getPhoneBillForCustomer(anyString())).thenReturn(mockBill);

        try (MockedStatic<Project4> mocked = mockStatic(Project4.class)) {
            mocked.when(() -> Project4.createPhoneBillRestClient(anyString(), anyInt()))
                    .thenReturn(mockClient);

            Project4 project = new Project4();
            int result = project.processArgs("-host", HOSTNAME, "-port", PORT, "Customer",
                    "503-245-2345", "765-389-1273", "02/27/2024", "8:56", "AM", "02/27/2024", "10:27", "AM", "-print");

            assertEquals(0, result);

            assertThat(outContent.toString(), containsString("Caller: 503-245-2345, Callee: 765-389-1273"));
            assertThat(outContent.toString(), containsString("Start Time: 02/27/2024 8:56 AM"));
            assertThat(outContent.toString(), containsString("End Time: 02/27/2024 10:27 AM"));
            assertThat(outContent.toString(), containsString("Duration: 91 minutes"));
        }
    }
}
