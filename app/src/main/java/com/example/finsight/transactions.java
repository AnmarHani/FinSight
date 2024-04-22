package com.example.finsight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class transactions extends AppCompatActivity {

    private Button newTransactionButton;
    private ImageButton newprofileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        // Create a list of items
        ArrayList<String> itemList = new ArrayList<String>();

        fetchAndDisplayUserTransactions(itemList);

        // Create an ArrayAdapter
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                itemList);

        // Connect the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.transactionsList);
        listView.setAdapter(itemsAdapter);

        newTransactionButton = findViewById(R.id.newTransactionBtn);
        newprofileButton = findViewById(R.id.profileBtn);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_transaction);
        bottomNavigationView.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();

            if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), home_page.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();

                return true;
            }

            else if (itemId == R.id.bottom_transaction) {

                return true;
            } else if (itemId == R.id.bottom_budget) {
                startActivity(new Intent(getApplicationContext(), budget.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();

                return true;
            } else if (itemId == R.id.bottom_insight) {
                startActivity(new Intent(getApplicationContext(), insights.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });

        newTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the new transaction activity
                Intent intent = new Intent(transactions.this, creat_transaction.class);
                startActivity(intent);
            }
        });

        newprofileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the sign-up activity
                Intent intent = new Intent(transactions.this, editProfile.class);
                startActivity(intent);
            }
        });

    }

    private void fetchAndDisplayUserTransactions(ArrayList<String> list) {
        try {
            JSONObject response = APIMethods.get(APIMethods.CONNECTION_URL + "/user_transactions");
            if (response.has("result")) {
                JSONArray transactions = response.getJSONArray("result"); // Get the array
                for (int i = transactions.length() - 1; i >= 0; i--) {
                    JSONObject transaction = transactions.getJSONObject(i); // Get each transaction as a JSONObject
                    // Now you can access the fields of each transaction
                    double amount = transaction.getDouble("amount");
                    String description = transaction.getString("description");

                    if (transaction.getInt("transaction_type") == 1) {
                        list.add(description + ": " + String.valueOf(amount) + " SR");
                    } else {
                        list.add(description + ": -" + String.valueOf(amount) + " SR");
                    }
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