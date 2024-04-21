package com.example.finsight;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

public class insights extends AppCompatActivity {

    private ImageButton newprofileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insights);


        newprofileButton = findViewById(R.id.profileBtn);

        fetchAndDisplaySuggestions();
        fetchAndDisplayInsights();


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_insight);
        bottomNavigationView.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();

            if (itemId == R.id.bottom_home){
                startActivity(new Intent(getApplicationContext(), home_page.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();

                return true;
            }

            else if (itemId == R.id.bottom_transaction){
                startActivity(new Intent(getApplicationContext(), transactions.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            else if (itemId == R.id.bottom_budget){
                startActivity(new Intent(getApplicationContext(), budget.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();

                return true;
            }
            else if (itemId == R.id.bottom_insight){

                return true;
            }
            return false;
        });

        newprofileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the sign-up activity
                Intent intent = new Intent(insights.this, editProfile.class);
                startActivity(intent);
            }
        });



    }

    private void fetchAndDisplaySuggestions() {
        new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    return APIMethods.get(APIMethods.CONNECTION_URL + "/generate_gemini_content");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(JSONObject response) {
                if (response != null) {
                    try {
                        if (response.has("generated_text")) {
                            // Update successful, show success message
                            TextView suggestionText = (TextView) findViewById(R.id.suggestionText);
                            suggestionText.setText(response.getString("generated_text"));
                        } else if (response.has("error")) {
                            // Handle error
                            String error = response.getString("error");
                            Toast.makeText(insights.this, error, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }

    private void fetchAndDisplayInsights() {
        try {
            JSONObject response = APIMethods.get(APIMethods.CONNECTION_URL + "/transaction_analysis");
            if (response.has("message")) {
                // Update successful, show success message
                String message = response.getString("message");
                int transactionImageId = getResources().getIdentifier("transaction_analysis", "drawable", getPackageName());
                int budgetImageId = getResources().getIdentifier("budget_analysis", "drawable", getPackageName());

                ImageView transactionImageView = findViewById(R.id.transactionImageView);
                ImageView budgetImageView = findViewById(R.id.budgetImageView);

                transactionImageView.setImageResource(transactionImageId);
                budgetImageView.setImageResource(budgetImageId);

                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } else if (response.has("error")) {
                // Handle error
                String error = response.getString("error");
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}