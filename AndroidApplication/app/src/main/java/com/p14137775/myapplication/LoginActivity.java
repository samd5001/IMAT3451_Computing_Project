package com.p14137775.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import classes.User;
import wrappers.SQLWrapper;
import wrappers.URLWrapper;
import wrappers.VolleyWrapper;


public class LoginActivity extends AppCompatActivity {
    private SQLWrapper db;

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

        db = new SQLWrapper(getApplicationContext());

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = emailText.getText().toString().trim();
                String password = passwordText.getText().toString().trim();

                if (!email.isEmpty() && !password.isEmpty()) {
                    remoteLogin(email, password);
                } else {
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

    @Override
    public void onResume() {
        super.onResume();
        if (getSharedPreferences("preferences", MODE_PRIVATE).getBoolean("loggedIn", false)) {
            finish();
        }
    }


    private void remoteLogin(final String email, final String password) {
        StringRequest request = new StringRequest(Request.Method.POST,
                URLWrapper.loginURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        jObj = jObj.getJSONObject("user");
                        User user = new User(email, password, jObj.getString("name"), jObj.getString("dob"), jObj.getInt("gender"), jObj.getDouble("height"), jObj.getDouble("weight"), jObj.getInt("goal"));
                        db.loginUser(user);
                        finish();

                    } else {
                        String message = jObj.getString("message");
                        Toast.makeText(getApplicationContext(),
                                message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "No Network Connection", Toast.LENGTH_SHORT).show();
                }
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyWrapper.getInstance().addToRequestQueue(request, "login");
    }
}
