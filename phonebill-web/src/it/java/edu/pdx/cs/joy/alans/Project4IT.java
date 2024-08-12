package edu.pdx.cs.joy.alans;

import edu.pdx.cs.joy.web.HttpRequestHelper;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.io.ByteArrayOutputStream;
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
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() {
        mockClient = mock(PhoneBillRestClient.class);
        errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void tearDown() {
        System.setErr(originalErr);
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
    void testInvalidCommandLineSyntax() {
        Project4 project = new Project4();
        int result = project.processArgs("-host", "localhost");
        assertEquals(1, result);
        assertThat(errContent.toString(), containsString("Missing customer name"));
    }

}
