package com.example.finsight;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class insights extends AppCompatActivity {

    private ImageButton newprofileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insights);


        newprofileButton = findViewById(R.id.profileBtn);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_insight);
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

                return true;
            }
            return false;
        });

        newprofileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the sign-up activity
                Intent intent = new Intent(insights.this, editProfile.class);
                startActivity(intent);
            }
        });

    }
}