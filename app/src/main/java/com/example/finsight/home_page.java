package com.example.finsight;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

public class home_page extends AppCompatActivity {

    private ImageButton newprofileButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        
        // Setup Views Here

        fetchAndDisplayCurrentBalance();
        fetchAndDisplayUserBudgets();
        fetchAndDisplayUserTransactions();

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");

        TextView usernameText = (TextView) findViewById(R.id.hiUserName);
        usernameText.setText("Hi, " + username);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();

            if (itemId == R.id.bottom_home){


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
                startActivity(new Intent(getApplicationContext(), insights.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });

        newprofileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the sign-up activity
                Intent intent = new Intent(home_page.this, editProfile.class);
                startActivity(intent);
            }
        });



    }

    private void fetchAndDisplayUserTransactions() {
        try {
            JSONObject response = APIMethods.get(APIMethods.CONNECTION_URL + "/user_transactions");
            if (response.has("result")) {
                String transactions = response.getString("result"); // I am not sure it is a stirng, it is an array. Please check out how to deal with JSON Arrays? or Print it?
                // Get View and Set Transactions Here as A List View or whatever but limit them to 3

            } else if (response.has("error")) {
                // Handle error
                String error = response.getString("error");
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchAndDisplayUserBudgets() {
        try {
            JSONObject response = APIMethods.get(APIMethods.CONNECTION_URL + "/users_budgets");
            if (response.has("result")) {
                String budgets = response.getString("result");// I am not sure it is a stirng, it is an array. Please check out how to deal with JSON Arrays? or Print it?
                // Get View and Set Budgets Here as A List View or whatever but limit them to 3
                // The same this method, use it when clicked on


            } else if (response.has("error")) {
                // Handle error
                String error = response.getString("error");
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchAndDisplayCurrentBalance() {
        try {
            JSONObject response = APIMethods.get(APIMethods.CONNECTION_URL + "/current_balance");
            if (response.has("current_balance")) {
                String currentBalance = response.getString("current_balance"); // Not sure if its String, maybe double or Integer?
                // Get View and Set Current Balance Here

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