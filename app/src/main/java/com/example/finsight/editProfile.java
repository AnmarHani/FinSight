package com.example.finsight;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

public class editProfile extends AppCompatActivity {
    private EditText currentBalanceEditText;
    private EditText incomeEditText;
    private Button currentBalanceBtn;
    private Button incomeBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        TextView username = (TextView) findViewById(R.id.username);
        username.setText(APIMethods.username);

        currentBalanceEditText = findViewById(R.id.currentBalance);
        incomeEditText = findViewById(R.id.income);
        currentBalanceBtn = findViewById(R.id.currentBalanceBtn);
        incomeBtn = findViewById(R.id.incomeBtn);

        currentBalanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInfo("current_balance", currentBalanceEditText.getText().toString());
            }
        });

        incomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInfo("monthly_income", incomeEditText.getText().toString());
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
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
                startActivity(new Intent(getApplicationContext(), insights.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });

    }

    private void updateUserInfo(String field, String value) {
        JSONObject body = new JSONObject();
        try {
            body.put(field, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject response = APIMethods.put(APIMethods.CONNECTION_URL + "/update_user", body.toString());
            if (response.has("message")) {
                // Update successful, show success message
                String message = response.getString("message");
                Toast.makeText(editProfile.this, message, Toast.LENGTH_SHORT).show();
            } else if (response.has("error")) {
                // Handle error
                String error = response.getString("error");
                Toast.makeText(editProfile.this, error, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}