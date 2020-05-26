package com.p14137775.myapplication;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import wrappers.SQLWrapper;

import static android.content.Context.MODE_PRIVATE;

public class PreferencesFragment extends PreferenceFragment {

    Preference login;
    Preference logout;
    Preference register;
    Preference data;
    Preference details;
    PreferenceScreen screen;
    boolean loggedin;
    private SharedPreferences prefs;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_preferences);
        prefs = getActivity().getSharedPreferences("preferences", MODE_PRIVATE);
        prefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("loggedIn") && !loggedin) {
                    screen.removePreference(login);
                    screen.removePreference(register);
                    screen.addPreference(logout);
                    screen.addPreference(details);
                    loggedin = true;
                }
            }
        });
        loggedin = prefs.getBoolean("loggedIn", false);
        login = findPreference("login");
        logout = findPreference("logout");
        register = findPreference("register");
        data = findPreference("data");
        details = findPreference("details");

        screen = getPreferenceScreen();
        if (loggedin) {
            screen.removePreference(login);
            screen.removePreference(register);
        } else {
            screen.removePreference(logout);
            screen.removePreference(details);
        }

        logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Logged out", Toast.LENGTH_SHORT).show();
                screen.addPreference(login);
                screen.addPreference(register);
                screen.removePreference(logout);
                screen.removePreference(details);
                SQLWrapper db = new SQLWrapper(getActivity().getApplicationContext());
                db.logoutUser();
                return true;
            }
        });

        details.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getActivity().getFragmentManager().beginTransaction().replace(R.id.content, new PreferencesDetailsFragment()).addToBackStack(null).commit();
                return true;
            }
        });

        data.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new AlertDialog.Builder(getActivity())
                        .setMessage("Do you want to re-download the application data? (This will delete all data if you are not logged in)")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                new SQLWrapper(getActivity()).resetData();
                            }
                        })
                        .setNegativeButton("No", null).show();
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (prefs.getBoolean("loggedIn", false) && !loggedin) {
            screen.removePreference(login);
            screen.removePreference(register);
            screen.addPreference(logout);
            screen.addPreference(details);
            loggedin = true;
        }
    }
}
