package com.p14137775.myapplication;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import classes.User;
import wrappers.SQLWrapper;
import wrappers.URLWrapper;
import wrappers.VolleyWrapper;

public class PreferencesDetailsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SQLWrapper db;
    private User user;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new SQLWrapper(getActivity().getApplicationContext());
        user = db.getUser();

        addPreferencesFromResource(R.xml.details_preferences);
        final SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();
        prefs.registerOnSharedPreferenceChangeListener(this);
        final EditTextPreference email = (EditTextPreference) findPreference("email");
        email.setText(user.getEmail());
        email.setSummary(user.getEmail());
        final EditTextPreference name = (EditTextPreference) findPreference("name");
        name.setText(user.getName());
        name.setSummary(user.getName());
        final ListPreference goal = (ListPreference) findPreference("goal");
        goal.setValueIndex(user.getGoal());
        goal.setSummary(goal.getEntry());
        final Preference submit = findPreference("submit");
        final Preference password = findPreference("reset");
        final Preference delete = findPreference("delete");
        final Preference reset = findPreference("resetAccount");

        password.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                new AlertDialog.Builder(getActivity())
                        .setMessage("An email will be sent to reset your password do you want to do this?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                StringRequest emailRequest = new StringRequest(Request.Method.POST, URLWrapper.resetPasswordURL, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Toast.makeText(getActivity(), "Email sent", Toast.LENGTH_SHORT).show();
                                    }
                                }, new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                    }
                                }) {

                                    @Override
                                    protected Map<String, String> getParams() {
                                        Map<String, String> params = new HashMap<>();
                                        params.put("email", user.getEmail());
                                        return params;
                                    }

                                };
                                emailRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                VolleyWrapper.getInstance().addToRequestQueue(emailRequest, "resetEmail");
                            }
                        })
                        .setNegativeButton("No", null).show();

                return false;
            }
        });

        submit.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (email.getText().equals(user.getEmail()) && goal.getValue().equals(String.valueOf(user.getGoal())) && name.getText().equals(user.getName())) {
                    Toast.makeText(getActivity(), "Values must be changed", Toast.LENGTH_SHORT).show();
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setMessage("Do you want to store these changes?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    final String oldEmail = user.getEmail();
                                    final String newEmail = email.getText().trim();
                                    if (!oldEmail.equals(newEmail)) {
                                        final EditText password = new EditText(getActivity());
                                        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                        new AlertDialog.Builder(getActivity())
                                                .setView(password)
                                                .setMessage("Please enter your password to update your email")
                                                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        if (password.getText().toString().equals(user.getPassword())) {
                                                            user.setEmail(newEmail);
                                                            user.setName(name.getText().trim());
                                                            user.setGoal(Integer.valueOf(goal.getValue()));
                                                            db.updateUser(user, oldEmail);
                                                            Toast.makeText(getActivity(), "Details updated", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(getActivity(), "Password is incorrect", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                })
                                                .setNegativeButton("Cancel", null).show();
                                    } else {
                                        user.setName(name.getText().trim());
                                        user.setGoal(Integer.valueOf(goal.getValue()));
                                        db.updateUser(user, oldEmail);
                                        Toast.makeText(getActivity(), "Details updated", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            })
                            .setNegativeButton("No", null).show();
                }
                return true;
            }
        });

        delete.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final EditText password = new EditText(getActivity());
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                new AlertDialog.Builder(getActivity())
                        .setView(password)
                        .setMessage("Are you sure you want to delete your account? This cannot be undone. Please enter your password for confirmation")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (password.getText().toString().equals(user.getPassword())) {
                                    db.logoutUser();
                                    getActivity().finish();
                                    Toast.makeText(getActivity(), "Account deleted", Toast.LENGTH_SHORT).show();
                                    db.deleteUser(user);
                                } else {
                                    Toast.makeText(getActivity(), "Password is incorrect", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("No", null).show();
                return true;
            }
        });

        reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final EditText password = new EditText(getActivity());
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                new AlertDialog.Builder(getActivity())
                        .setView(password)
                        .setMessage("This will reset all user data? This cannot be undone. Please enter your password for confirmation")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (password.getText().toString().equals(user.getPassword())) {
                                    db.resetUser();
                                } else {
                                    Toast.makeText(getActivity(), "Password is incorrect", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("No", null).show();
                return true;
            }
        });
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);

        if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            pref.setSummary(listPref.getEntry());
        }

        if (pref instanceof EditTextPreference) {
            EditTextPreference textPreference = (EditTextPreference) pref;
            textPreference.setSummary(textPreference.getText());
        }
    }
}
