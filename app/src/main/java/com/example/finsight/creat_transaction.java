package com.example.finsight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

public class creat_transaction extends AppCompatActivity {

    private ImageButton newprofileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_transaction);

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

        String[] type = new String[] { "Positive", "Negative" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.drop_down_item,
                type);

        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.typeTransaction);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(creat_transaction.this, autoCompleteTextView.getText().toString(), Toast.LENGTH_SHORT)
                        .show();
            }
        });

        Button continueButton = findViewById(R.id.continuetBtn);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // post to info to database
                JSONObject body = new JSONObject();
                EditText transactionAmount = findViewById(R.id.transactionAmount);
                String stringTransactionAmount = transactionAmount.getText().toString();
                AutoCompleteTextView transactionType = findViewById(R.id.typeTransaction);
                String stringTransactionType = transactionType.getText().toString();
                int transactionTypeInt = 0;
                if (stringTransactionType.equals("Positive")) {
                    transactionTypeInt = 1;
                } else if (stringTransactionType.equals("Negative")) {
                    transactionTypeInt = -1;
                }
                EditText transactionDescription = findViewById(R.id.transactionDescription);
                String stringTransactionDescription = transactionDescription.getText().toString();
                try {
                    body.put("amount", stringTransactionAmount);
                    body.put("description", stringTransactionDescription);
                    body.put("transaction_type", transactionTypeInt);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONObject response = APIMethods.post(APIMethods.CONNECTION_URL + "/add_transaction",
                            body.toString());
                    if (response.has("message")) {
                        // Budget created successfully
                        Toast.makeText(creat_transaction.this, response.getString("message"), Toast.LENGTH_SHORT)
                                .show();
                        Intent intent = new Intent(creat_transaction.this, transactions.class);
                        startActivity(intent);
                        finish();
                    } else if (response.has("error")) {
                        // Handle error
                        String error = response.getString("error");
                        Toast.makeText(creat_transaction.this, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        newprofileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the sign-up activity
                Intent intent = new Intent(creat_transaction.this, editProfile.class);
                startActivity(intent);
            }
        });
    }
}