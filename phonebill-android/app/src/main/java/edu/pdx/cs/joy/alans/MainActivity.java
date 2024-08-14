package edu.pdx.cs.joy.alans;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a", Locale.US);
    private Map<String, PhoneBill> phoneBills = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onEnterPhoneCallClicked(View view) {
        promptForPhoneCallDetails();
    }

    public void onPrettyPrintClicked(View view) {
        promptForPrettyPrintCustomerName();
    }

    public void onSearchCallsClicked(View view) {
        promptForSearchDetails();
    }

    private void promptForPhoneCallDetails() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_enter_phone_call, null);
        final EditText customerNameInput = dialogView.findViewById(R.id.customerName);
        final EditText callerInput = dialogView.findViewById(R.id.callerNumber);
        final EditText calleeInput = dialogView.findViewById(R.id.calleeNumber);
        final EditText startTimeInput = dialogView.findViewById(R.id.startTime);
        final EditText endTimeInput = dialogView.findViewById(R.id.endTime);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Phone Call Details")
                .setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String customerName = customerNameInput.getText().toString().trim();
                        String caller = callerInput.getText().toString().trim();
                        String callee = calleeInput.getText().toString().trim();
                        String startTime = startTimeInput.getText().toString().trim().toUpperCase();
                        String endTime = endTimeInput.getText().toString().trim().toUpperCase();

                        if (customerName.isEmpty() || caller.isEmpty() || callee.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                            showErrorDialog("All fields must be filled.");
                            return;
                        }

                        if (!isValidPhoneNumber(caller) || !isValidPhoneNumber(callee)) {
                            showErrorDialog("Invalid phone number format.");
                            return;
                        }

                        if (!isValidDateTime(startTime) || !isValidDateTime(endTime)) {
                            showErrorDialog("Invalid date/time format.");
                            return;
                        }

                        LocalDateTime start = parseDateTime(startTime);
                        LocalDateTime end = parseDateTime(endTime);

                        if (end.isBefore(start)) {
                            showErrorDialog("End time cannot be before start time.");
                            return;
                        }

                        PhoneCall call = new PhoneCall(caller, callee, start, end);
                        addPhoneCallToBill(customerName, call);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addPhoneCallToBill(String customerName, PhoneCall call) {
        PhoneBill phoneBill = phoneBills.get(customerName);
        if (phoneBill == null) {
            phoneBill = new PhoneBill(customerName);
            phoneBills.put(customerName, phoneBill);
            Toast.makeText(this, "Created new phone bill for " + customerName, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Added phone call to existing phone bill for " + customerName, Toast.LENGTH_SHORT).show();
        }
        phoneBill.addPhoneCall(call);
        showCallAddedDialog(call);
    }

    private void showCallAddedDialog(PhoneCall call) {
        String message = "Caller: " + call.getCaller() + "\n"
                + "Callee: " + call.getCallee() + "\n"
                + "Start Time: " + call.getBeginTimeString() + "\n"
                + "End Time: " + call.getEndTimeString() + "\n";

        new AlertDialog.Builder(this)
                .setTitle("Phone Call Added")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void promptForPrettyPrintCustomerName() {
        final EditText input = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Customer Name to Pretty Print")
                .setView(input)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String customerName = input.getText().toString().trim();
                        if (customerName.isEmpty()) {
                            showErrorDialog("Customer name cannot be empty.");
                        } else {
                            displayPrettyPrint(customerName);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void displayPrettyPrint(String customerName) {
        PhoneBill phoneBill = phoneBills.get(customerName);
        if (phoneBill == null) {
            showErrorDialog("No phone bill found for customer: " + customerName);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Customer: ").append(phoneBill.getCustomer()).append("\n");

        Collection<PhoneCall> calls = phoneBill.getPhoneCalls();
        if (calls.isEmpty()) {
            sb.append("No phone calls in this phone bill.");
        } else {
            sb.append("Phone calls:\n");
            for (PhoneCall call : calls) {
                sb.append("Caller: ").append(call.getCaller()).append("\n")
                        .append("Callee: ").append(call.getCallee()).append("\n")
                        .append("Start Time: ").append(call.getBeginTimeString()).append("\n")
                        .append("End Time: ").append(call.getEndTimeString()).append("\n")
                        .append("\n");
            }
        }

        // Starts PrettyPrintActivity and passes the pretty print data
        Intent intent = new Intent(this, PrettyPrintActivity.class);
        intent.putExtra("prettyPrintData", sb.toString());
        startActivity(intent);
    }


    private void promptForSearchDetails() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_search_phone_calls, null);
        final EditText customerNameInput = dialogView.findViewById(R.id.customerName);
        final EditText startInput = dialogView.findViewById(R.id.startDateTime);
        final EditText endInput = dialogView.findViewById(R.id.endDateTime);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Search Phone Calls")
                .setView(dialogView)
                .setPositiveButton("OK", (dialog, which) -> {
                    String customerName = customerNameInput.getText().toString().trim();
                    String startDateTime = startInput.getText().toString().trim().toUpperCase();
                    String endDateTime = endInput.getText().toString().trim().toUpperCase();

                    if (customerName.isEmpty() || startDateTime.isEmpty() || endDateTime.isEmpty()) {
                        showErrorDialog("All fields must be filled.");
                        return;
                    }

                    if (!isValidDateTime(startDateTime) || !isValidDateTime(endDateTime)) {
                        showErrorDialog("Invalid date/time format.");
                        return;
                    }

                    LocalDateTime start = parseDateTime(startDateTime);
                    LocalDateTime end = parseDateTime(endDateTime);

                    if (end.isBefore(start)) {
                        showErrorDialog("End time cannot be before start time.");
                        return;
                    }

                    searchPhoneCalls(customerName, start, end);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void searchPhoneCalls(String customerName, LocalDateTime start, LocalDateTime end) {
        PhoneBill phoneBill = phoneBills.get(customerName);
        if (phoneBill == null) {
            showErrorDialog("No phone bill found for customer: " + customerName);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Customer: ").append(phoneBill.getCustomer()).append("\n");

        Collection<PhoneCall> calls = phoneBill.getPhoneCalls();
        boolean found = false;

        for (PhoneCall call : calls) {
            if (!call.getBeginTime().isBefore(start) && !call.getBeginTime().isAfter(end)) {
                sb.append("Caller: ").append(call.getCaller()).append("\n")
                        .append("Callee: ").append(call.getCallee()).append("\n")
                        .append("Start Time: ").append(call.getBeginTimeString()).append("\n")
                        .append("End Time: ").append(call.getEndTimeString()).append("\n")
                        .append("\n");
                found = true;
            }
        }

        if (!found) {
            sb.append("No phone calls found between ").append(start.format(formatter)).append(" and ").append(end.format(formatter));
        }

        // Starts SearchResultsActivity and passes the search results
        Intent intent = new Intent(this, SearchResultsActivity.class);
        intent.putExtra("searchResultsData", sb.toString());
        startActivity(intent);
    }


    private void showResultDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\d{3}-\\d{3}-\\d{4}");
    }

    private boolean isValidDateTime(String dateTimeString) {
        try {
            LocalDateTime.parse(dateTimeString, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private LocalDateTime parseDateTime(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, formatter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_readme) {
            showReadmeDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showReadmeDialog() {
        String readmeContent = "README for Project5\nAlan Shirk\n\n" +
                "In this Android app you can add phone calls to a phone bill, search for calls by customer name and date, " +
                "and pretty print the details of a customer's phone bill.";

        new AlertDialog.Builder(this)
                .setTitle("README")
                .setMessage(readmeContent)
                .setPositiveButton("OK", null)
                .show();
    }

}
