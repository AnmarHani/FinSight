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

public class sign_in extends AppCompatActivity {

    private EditText emailUserEditText, passwordEditText;
    private Button logInButton;
    private TextView signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        emailUserEditText = findViewById(R.id.emailUser);
        passwordEditText = findViewById(R.id.password);
        logInButton = findViewById(R.id.logInBtn);
        signUpButton = findViewById(R.id.signUpBtn);

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailUser = emailUserEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                JSONObject body = new JSONObject();
                try {
                    body.put("username", emailUser);
                    body.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    JSONObject response = APIMethods.post(APIMethods.CONNECTION_URL + "/login", body.toString());
                    if (response.has("token")) {
                        // Login successful, Save token, navigate to the next activity
                        APIMethods.AUTHORIZATION_KEY = response.getString("token");
                        Intent intent = new Intent(sign_in.this, home_page.class);
                        intent.putExtra("username", emailUser);
                        startActivity(intent);
                        finish(); // Prevents going back to the sign-in activity
                    } else if (response.has("error")) {
                        // Handle error
                        String error = response.getString("error");
                        Toast.makeText(sign_in.this, error, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the sign-up activity
                Intent intent = new Intent(sign_in.this, sign_up.class);
                startActivity(intent);
            }
        });


    }
}