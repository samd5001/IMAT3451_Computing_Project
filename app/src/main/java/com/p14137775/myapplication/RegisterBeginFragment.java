package com.p14137775.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import classes.PasswordValidator;
import wrappers.URLWrapper;
import wrappers.VolleyWrapper;

public class RegisterBeginFragment extends Fragment {

    private OnRegisterBegin mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registerbegin, parent, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        final EditText emailText = (EditText) view.findViewById(R.id.editText);
        final EditText email2Text = (EditText) view.findViewById(R.id.editText2);
        final EditText passwordText = (EditText) view.findViewById(R.id.editText3);
        final EditText password2Text = (EditText) view.findViewById(R.id.editText4);
        Button register = (Button) view.findViewById(R.id.button);

        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            String email = emailText.getText().toString().trim();
            String email2 = email2Text.getText().toString().trim();
            String password = passwordText.getText().toString().trim();
            String password2 = password2Text.getText().toString().trim();
            if (!email.isEmpty() && !password.isEmpty()) {
                if (email.equals(email2)) {
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        if (password.equals(password2)) {
                            if (new PasswordValidator().validate(password)) {
                                checkUser(email, password);
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Password must be greater than 8 characters, less than 15 characters and contain a number",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Passwords do not match", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Email is not valid", Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(),
                        "Email address does not match", Toast.LENGTH_SHORT)
                        .show();
                }
            } else {
                Toast.makeText(getActivity().getApplicationContext(),
                    "Please enter an email and password", Toast.LENGTH_LONG)
                    .show();
            }

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRegisterBegin) {
            mCallback = (OnRegisterBegin) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement RegisterBeginFragment.OnButtonClick");
        }
    }

    private void checkUser (final String email, final String password) {
        StringRequest request = new StringRequest(Method.POST,
                URLWrapper.checkUserURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Toast.makeText(getActivity().getApplicationContext(), "Email is already registered", Toast.LENGTH_LONG).show();
                    } else {
                        mCallback.onRegisterBegin(email, password);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(),
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
        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyWrapper.getInstance().addToRequestQueue(request, "checkUser");
    }

    interface OnRegisterBegin {
        void onRegisterBegin(String email, String password);
    }
}
