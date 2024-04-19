package com.example.finsight;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class sign_up extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, passwordEditText;
    private Button createAccountButton;
    private TextView signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        createAccountButton = findViewById(R.id.createAccountBtn);
        signInButton = findViewById(R.id.signInBtn);


        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                JSONObject body = new JSONObject();
                try {
                    body.put("username", username);
                    body.put("email", email);
                    body.put("password", password);
                    body.put("current_balance", 10000.0);
                    body.put("monthly_income", 20000.0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    JSONObject response = APIMethods.post(APIMethods.CONNECTION_URL + "/register", body.toString());
                    if (response.has("message")) {
                        // Account creation successful, navigate to the sign-in activity
                        Intent intent = new Intent(sign_up.this, sign_in.class);
                        startActivity(intent);
                        finish(); // Prevents going back to the sign-up activity
                    } else if (response.has("error")) {
                        // Handle error
                        String error = response.getString("error");
                        Toast.makeText(sign_up.this, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the sign-in activity
                Intent intent = new Intent(sign_up.this, sign_in.class);
                startActivity(intent);
            }
        });
    }
}