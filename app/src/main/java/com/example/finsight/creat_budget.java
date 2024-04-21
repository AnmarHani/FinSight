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

public class creat_budget extends AppCompatActivity {

    private ImageButton newprofileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_budget);

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

        String[] type = new String[] { "Daily", "Weekly", "Monthly", "Yearly" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.drop_down_item,
                type);

        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.typeBudget);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(creat_budget.this, autoCompleteTextView.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        Button continueButton = findViewById(R.id.continuetBtn);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // post to info to database
                JSONObject body = new JSONObject();
                EditText budgetName = findViewById(R.id.budget_name);
                String stringBudgetName = budgetName.getText().toString();
                AutoCompleteTextView budgetType = findViewById(R.id.typeBudget);
                String stringBudgetType = budgetType.getText().toString();
                EditText budgetAmount = findViewById(R.id.budgetAmount);
                String stringBudgetAmount = budgetAmount.getText().toString();
                try {
                    body.put("budget_name", stringBudgetName);
                    body.put("budget_type", stringBudgetType);
                    body.put("budget_amount", stringBudgetAmount);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONObject response = APIMethods.post(APIMethods.CONNECTION_URL + "/create_budget",
                            body.toString());
                    if (response.has("message")) {
                        // Budget created successfully
                        Toast.makeText(creat_budget.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(creat_budget.this, budget.class);
                        startActivity(intent);
                        finish();
                    } else if (response.has("error")) {
                        // Handle error
                        String error = response.getString("error");
                        Toast.makeText(creat_budget.this, error, Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(creat_budget.this, editProfile.class);
                startActivity(intent);
            }
        });
    }
}