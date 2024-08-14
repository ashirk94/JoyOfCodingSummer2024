package edu.pdx.cs.joy.alans;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PrettyPrintActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pretty_print);

        TextView prettyPrintTextView = findViewById(R.id.prettyPrintTextView);

        // Retrieves the data from the intent
        String prettyPrintData = getIntent().getStringExtra("prettyPrintData");

        // Displays the data
        prettyPrintTextView.setText(prettyPrintData);
    }

    public void goBack(View view) {
        finish();
    }
}
