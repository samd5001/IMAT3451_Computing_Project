package com.p14137775.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import controllers.AppController;
import wrappers.SQLiteWrapper;
import wrappers.ServerURLS;
import wrappers.SessionManager;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private SessionManager session;
    private SQLiteWrapper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Button login = (Button) findViewById(R.id.button);
        final Button skip = (Button) findViewById(R.id.button2);
        final EditText emailText = (EditText) findViewById(R.id.editText);
        final EditText passwordText = (EditText) findViewById(R.id.editText2);
        final TextView register = (TextView) findViewById(R.id.textView4);

        // SQLite database handler
        db = new SQLiteWrapper(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

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


        skip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void remoteLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Method.POST,
                ServerURLS.loginURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response);

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        // Now store the user in SQLite

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("Name");
                        String email = user.getString("Email");

                        // Inserting row in users table
                        db.addUser(name, email);

                        // Launch main activity
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String message = jObj.getString("message");
                        Toast.makeText(getApplicationContext(),
                                message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
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
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
