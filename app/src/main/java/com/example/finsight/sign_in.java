package com.example.finsight;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
                // Perform login logic here
                String emailUser = emailUserEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Add your login code or API call here

                // Assuming login is successful, navigate to the next activity
                Intent intent = new Intent(sign_in.this, home_page.class);
                startActivity(intent);
                finish(); // Prevents going back to the sign-in activity
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