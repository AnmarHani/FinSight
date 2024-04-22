package com.example.finsight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class home_page extends AppCompatActivity {

    private ImageButton transactionButton;
    private ImageButton budgetButton;
    private ImageButton newprofileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Setup Views Here
        fetchAndDisplayCurrentBalance();
        fetchAndDisplayUserBudgets();
        fetchAndDisplayUserTransactions();

        TextView usernameText = (TextView) findViewById(R.id.hiUserName);
        usernameText.setText("Hi, " + APIMethods.username);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();

            if (itemId == R.id.bottom_home) {

                return true;
            }

            else if (itemId == R.id.bottom_transaction) {
                startActivity(new Intent(getApplicationContext(), transactions.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
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

        newprofileButton = (ImageButton) findViewById(R.id.profileBtn);

        newprofileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the sign-up activity
                Intent intent = new Intent(home_page.this, editProfile.class);
                startActivity(intent);
            }
        });

        transactionButton = (ImageButton) findViewById(R.id.transactionButton);
        transactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the sign-up activity
                Intent intent = new Intent(home_page.this, transactions.class);
                startActivity(intent);
            }
        });

        budgetButton = (ImageButton) findViewById(R.id.budgetButton);
        budgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the sign-up activity
                Intent intent = new Intent(home_page.this, budget.class);
                startActivity(intent);
            }
        });

    }

    private void fetchAndDisplayUserTransactions() {
        try {
            JSONObject response = APIMethods.get(APIMethods.CONNECTION_URL + "/user_transactions");
            if (response.has("result")) {
                JSONArray transactions = response.getJSONArray("result"); // Get the array
                int transactionsTextFieldCount = 1;
                for (int i = transactions.length() - 1; i >= 0; i--) {
                    JSONObject transaction = transactions.getJSONObject(i); // Get each transaction as a JSONObject
                    // Now you can access the fields of each transaction
                    int transactionId = transaction.getInt("transaction_id");
                    double amount = transaction.getDouble("amount");
                    String transactionDate = transaction.getString("transaction_date");
                    String description = transaction.getString("description");
                    int transactionType = transaction.getInt("transaction_type");
                    System.out.println(transactionId + " " + amount + " " + transactionDate + " " + description + " "
                            + transactionType);
                    if (transactionsTextFieldCount <= 2) {
                        int textViewId = getResources().getIdentifier("transaction_name" + transactionsTextFieldCount,
                                "id", getPackageName());
                        TextView transaction_name = findViewById(textViewId);
                        if (description.length() > 18) {
                            description = description.substring(0, 17) + "...";
                        }
                        transaction_name.setText(description);
                        int transactionAmountId = getResources().getIdentifier(
                                "transaction_amount" + transactionsTextFieldCount, "id", getPackageName());
                        TextView transaction_amount = findViewById(transactionAmountId);
                        if (transaction.getInt("transaction_type") == 1) {
                            transaction_amount.setTextColor(getResources().getColor(R.color.green));
                        } else {
                            transaction_amount.setTextColor(getResources().getColor(R.color.red));
                            amount = -amount;
                        }
                        transaction_amount.setText(amount + " SR");
                        transactionsTextFieldCount++;
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

    private void fetchAndDisplayUserBudgets() {
        try {
            JSONObject response = APIMethods.get(APIMethods.CONNECTION_URL + "/users_budgets");
            if (response.has("result")) {
                JSONArray budgets = response.getJSONArray("result"); // Get the array
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                System.out.println(budgets);
                JSONObject budget = budgets.getJSONObject(budgets.length() - 1);

                String jsonBudgetName = budget.getString("budget_name");
                TextView budgetName = findViewById(R.id.budgetName);
                budgetName.setText(jsonBudgetName);
                String jsonBudgetAmount = budget.getString("budget_amount");
                TextView budgetAmount = findViewById(R.id.budgetAmount);
                budgetAmount.setText(jsonBudgetAmount + " SR");
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
                String currentBalance = response.getString("current_balance");

                // Get View and Set Current Balance Here
                TextView currentBalanceTextView = findViewById(R.id.balance);
                currentBalanceTextView.setText(currentBalance + " SR");

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