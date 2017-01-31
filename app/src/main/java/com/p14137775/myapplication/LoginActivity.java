package com.p14137775.myapplication;

/**
 * Created by Samuel on 05/12/2016.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {
    EditText text1;
    EditText text2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button button1 = (Button) findViewById(R.id.button);
        Button button2 = (Button) findViewById(R.id.button2);
        text1 = (EditText) findViewById(R.id.editText);
        text2 = (EditText) findViewById(R.id.editText2);

        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (text1.getText().toString().equals("dummy") &&
                        text2.getText().toString().equals("dummy"))
                {
                    getSharedPreferences("Preferences", MODE_PRIVATE).edit().putBoolean("loggedIn", true).commit();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
}
