package com.example.finsight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class budget extends AppCompatActivity {

    private Button newBudgetButton;

    private ImageButton newprofileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        // Create a list of items
        ArrayList<String> itemList = new ArrayList<String>();

        fetchAndDisplayBudgets(itemList);

        // Create an ArrayAdapter
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itemList);

        // Connect the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.budgetsList);
        listView.setAdapter(itemsAdapter);


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
    private void fetchAndDisplayBudgets(ArrayList<String> list) {
        try {
            JSONObject response = APIMethods.get(APIMethods.CONNECTION_URL + "/users_budgets");
            if (response.has("result")) {
                JSONArray budgets = response.getJSONArray("result"); // Get the array
                for (int i = budgets.length() - 1; i >= 0; i--) {
                    JSONObject budget = budgets.getJSONObject(i); // Get each transaction as a JSONObject

                    // Now you can access the fields of each transaction
                    String name = budget.getString("budget_name");
                    String type = budget.getString("budget_type");
                    double amount = budget.getDouble("budget_amount");

                    list.add(name + ": " + String.valueOf(amount) + " | " + type);

                }

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