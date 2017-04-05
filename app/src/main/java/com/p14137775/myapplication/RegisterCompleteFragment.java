package com.p14137775.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import classes.User;
import wrappers.DateTimeWrapper;
import wrappers.SQLWrapper;
import wrappers.URLWrapper;
import wrappers.VolleyWrapper;

import static android.content.Context.MODE_PRIVATE;

public class RegisterCompleteFragment extends Fragment {
    private String email;
    private String password;
    private OnRegisterComplete mCallback;
    private SQLWrapper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        email = getArguments().getString("email");
        password = getArguments().getString("password");
        return inflater.inflate(R.layout.fragment_registercomplete, parent, false);
    }

    public void onViewCreated(View view, Bundle savedInstance) {
        db = new SQLWrapper(getActivity().getApplicationContext(), getActivity().getSharedPreferences("preferences", MODE_PRIVATE));
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

                            double height = 0, weight = 0;
                            if (!heightText.getText().toString().isEmpty()) {
                                height = Double.valueOf(heightText.getText().toString().trim());
                            }
                            if (!weightText.getText().toString().isEmpty()) {
                                weight = Double.valueOf(weightText.getText().toString().trim());
                            }
                            int genderId = genderGroup.getCheckedRadioButtonId();
                            View selectedGender = genderGroup.findViewById(genderId);
                            int gender = genderGroup.indexOfChild(selectedGender);
                            DateTimeWrapper date = new DateTimeWrapper(datePicker.getYear(), (datePicker.getMonth() + 1), datePicker.getDayOfMonth());
                            String dob = date.sqlReady();
                            User user = new User(email, password, name, dob, gender, height, weight, selectedGoal, 1);
                            registerUser(user);
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

    private void registerUser(final User user) {
        StringRequest request = new StringRequest(Method.POST,
                URLWrapper.registerURL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Toast.makeText(getActivity().getApplicationContext(), "Account created. You have been logged in", Toast.LENGTH_LONG).show();
                        db.loginUser(user);
                        mCallback.onRegisterComplete();
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
                Map<String, String> params = new HashMap<>();
                params.put("email", user.getEmail());
                params.put("password", password);
                params.put("name", user.getName());
                params.put("dob", user.getDob());
                params.put("gender", String.valueOf(user.getGender()));
                params.put("height", String.valueOf(user.getHeight()));
                params.put("weight", String.valueOf(user.getWeight()));
                params.put("goal", String.valueOf(user.getGoal()));
                params.put("units", String.valueOf(user.getUnits()));
                return params;
            }

        };
        VolleyWrapper.getInstance().addToRequestQueue(request);
    }
}
