package com.example.finsight;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void onClickMe(View view) throws JSONException {
        JSONObject res = APIMethods.get("https://jsonplaceholder.typicode.com/todos/1");

        TextView text = (TextView) findViewById(R.id.textView);
        text.setText(res.getString("title"));

    }
}

