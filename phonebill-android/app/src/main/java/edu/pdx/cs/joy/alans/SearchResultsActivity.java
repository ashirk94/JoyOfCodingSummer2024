package edu.pdx.cs.joy.alans;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SearchResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        TextView searchResultsTextView = findViewById(R.id.searchResultsTextView);

        // Retrieves the data from the intent
        String searchResultsData = getIntent().getStringExtra("searchResultsData");

        // Displays the data
        if (searchResultsData != null) {
            searchResultsTextView.setText(searchResultsData);
        } else {
            searchResultsTextView.setText("No results to display");
        }
    }

    public void goBack(View view) {
        finish();
    }
}
