package com.p14137775.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import wrappers.DateTimeWrapper;
import wrappers.SQLiteUserWrapper;
import wrappers.URLWrapper;
import wrappers.VolleyWrapper;

import static android.content.Context.MODE_PRIVATE;

public class RegisterCompleteFragment extends Fragment {
    private String email;
    private String password;
    private int goal;
    private OnRegisterComplete mCallback;
    private SQLiteUserWrapper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        email = getArguments().getString("email");
        password = getArguments().getString("password");
        return inflater.inflate(R.layout.fragment_registercomplete, parent, false);
    }

    public void onViewCreated(View view, Bundle savedInstance) {
        db = new SQLiteUserWrapper(getActivity().getApplicationContext(), getActivity().getSharedPreferences("preferences", MODE_PRIVATE));
        final EditText nameText = (EditText) view.findViewById(R.id.editText);
        final EditText heightText = (EditText) view.findViewById(R.id.editText2);
        final EditText weightText = (EditText) view.findViewById(R.id.editText3);
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
        datePicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
        final RadioGroup genderGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        final Button register = (Button) view.findViewById(R.id.button);

        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final String name = nameText.getText().toString().trim();
                if (!name.isEmpty()) {
                    CharSequence goals[] = new CharSequence[] {"Improve Strength", "Improve Muscle Mass", "Lose Fat"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Finally chose your goal");
                    builder.setItems(goals, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int selectedGoal) {
                            goal = selectedGoal;

                            float height = 0, weight = 0;
                            if (!heightText.getText().toString().isEmpty()) {
                                height = Float.valueOf(heightText.getText().toString().trim());
                            }
                            if (!weightText.getText().toString().isEmpty()) {
                                weight = Float.valueOf(weightText.getText().toString().trim());
                            }
                            int genderId = genderGroup.getCheckedRadioButtonId();
                            View selectedGender = genderGroup.findViewById(genderId);
                            int gender = genderGroup.indexOfChild(selectedGender);
                            DateTimeWrapper date = new DateTimeWrapper(datePicker.getYear(), (datePicker.getMonth() + 1), datePicker.getDayOfMonth());
                            String dob = date.sqlReady();
                            registerUser(email, password, name, dob, gender, height, weight, goal);
                            mCallback.onRegisterComplete();
                            }
                        });
                    builder.show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Please enter your name", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRegisterComplete) {
            mCallback = (OnRegisterComplete) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement RegisterCompleteFragment.OnRegisterComplete");
        }
    }

    interface OnRegisterComplete {
        void onRegisterComplete();
    }

    private void registerUser(final String email, final String password, final String name,
                              final String dob, final int gender, final float height, final float weight, final int goal) {
        String requestTag = "req_register";

        StringRequest request = new StringRequest(Method.POST,
                URLWrapper.registerURL, new Response.Listener<String>() {

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
                        SharedPreferences prefs = getActivity().getSharedPreferences("preferences", MODE_PRIVATE);
                        prefs.edit().putBoolean("loggedIn", true).apply();
                        Toast.makeText(getActivity().getApplicationContext(), "Account created. You have been logged in", Toast.LENGTH_LONG).show();
                    } else {
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getActivity().getApplicationContext(),
                                errorMsg, Toast.LENGTH_SHORT).show();
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
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                params.put("name", name);
                params.put("dob", dob);
                params.put("gender", String.valueOf(gender));
                params.put("height", String.valueOf(height));
                params.put("weight", String.valueOf(weight));
                params.put("goal", String.valueOf(goal));

                return params;
            }

        };

        // Adding request to request queue
        VolleyWrapper.getInstance().addToRequestQueue(request, requestTag);
    }
}
