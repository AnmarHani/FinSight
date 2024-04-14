package com.example.finsight;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
                // Perform account creation logic here
                String username = usernameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Add your account creation code or API call here

                // Assuming account creation is successful, navigate to the sign-in activity
                Intent intent = new Intent(sign_up.this, sign_in.class);
                startActivity(intent);
                finish(); // Prevents going back to the sign-up activity
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