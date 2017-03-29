package com.p14137775.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import wrappers.URLWrapper;
import wrappers.VolleyWrapper;

public class ForgotPasswordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        final Button send = (Button) findViewById(R.id.button);
        final EditText emailText = (EditText) findViewById(R.id.editText);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            emailText.setText(extras.getString("email"));
        }

        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = emailText.getText().toString().trim();

                if (!email.isEmpty()) {
                    checkUser(email);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Enter a valid email", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void checkUser (final String email) {
        StringRequest request = new StringRequest(Method.POST,
                URLWrapper.checkUserURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Toast.makeText(getApplicationContext(), "Sending recovery email", Toast.LENGTH_LONG).show();
                        StringRequest emailRequest = new StringRequest(Method.POST, URLWrapper.resetPasswordURL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response.equals("email sent.")) {
                                    Toast.makeText(getApplicationContext(), "Email sent", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Unknown error", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } , new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(),
                                        error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }) {

                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<>();
                                params.put("email", email);
                                return params;
                            }

                        };
                        VolleyWrapper.getInstance().addToRequestQueue(emailRequest, "reqemail");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }

        };
        VolleyWrapper.getInstance().addToRequestQueue(request, "reqcheck");
    }
}
