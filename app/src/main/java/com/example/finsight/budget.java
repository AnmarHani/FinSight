package com.example.finsight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

public class budget extends AppCompatActivity {

    private Button newBudgetButton;

    private ImageButton newprofileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        fetchBudget();

        newBudgetButton = findViewById(R.id.newBudgetBtn);
        newprofileButton = findViewById(R.id.profileBtn);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_budget);
        bottomNavigationView.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();

            if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), home_page.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();

                return true;
            }

            else if (itemId == R.id.bottom_transaction) {
                startActivity(new Intent(getApplicationContext(), transactions.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();

                return true;
            } else if (itemId == R.id.bottom_budget) {

                return true;
            } else if (itemId == R.id.bottom_insight) {
                startActivity(new Intent(getApplicationContext(), insights.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });

        newBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the sign-up activity
                Intent intent = new Intent(budget.this, creat_budget.class);
                startActivity(intent);
            }
        });

        newprofileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the sign-up activity
                Intent intent = new Intent(budget.this, editProfile.class);
                startActivity(intent);
            }
        });

    }

    // Call budget API
    private void fetchBudget() {
        JSONObject response = null;
        try {
            System.out.println("EEEEEEEEEEEEEEEEE*WAU(*DUWA(*DU(*WU(*DWUA))))");
            response = APIMethods.get(APIMethods.CONNECTION_URL + "/users_budgets");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        // Display the budget
        if (response.has("result")) {
            // Display the budget
            System.out.println(response.toString() + "Budget");
        } else if (response.has("error")) {
            // Handle error
        }
    }
}