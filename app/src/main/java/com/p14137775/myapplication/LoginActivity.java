package com.p14137775.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import wrappers.SQLiteUserWrapper;
import wrappers.URLWrapper;
import wrappers.VolleyWrapper;


public class LoginActivity extends AppCompatActivity {
    private SQLiteUserWrapper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Button login = (Button) findViewById(R.id.button);
        final Button skip = (Button) findViewById(R.id.button2);
        final EditText emailText = (EditText) findViewById(R.id.editText);
        final EditText passwordText = (EditText) findViewById(R.id.editText2);
        final TextView register = (TextView) findViewById(R.id.textView4);
        final TextView forgot = (TextView) findViewById(R.id.textView3);

        // SQLite database handler
        db = new SQLiteUserWrapper(getApplicationContext(), getSharedPreferences("preferences", MODE_PRIVATE));

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = emailText.getText().toString().trim();
                String password = passwordText.getText().toString().trim();

                if (!email.isEmpty() && !password.isEmpty()) {
                    remoteLogin(email, password);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Enter a valid email and password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailText.getText().toString().trim();
                Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                i.putExtra("email", email);
                startActivity(i);
            }
        });




        skip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }



    private void remoteLogin(final String email, final String password) {
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                URLWrapper.loginURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        JSONObject user = jObj.getJSONObject("user");
                        String email = user.getString("email");
                        String name = user.getString("name");
                        String dob = user.getString("dob");
                        String gender = user.getString("gender");
                        String height = user.getString("height");
                        String weight = user.getString("weight");
                        String goal = user.getString("goal");
                        db.storeUser(name, email, dob, gender, height, weight, goal);

                    } else {
                        String message = jObj.getString("message");
                        Toast.makeText(getApplicationContext(),
                                message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        VolleyWrapper.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
