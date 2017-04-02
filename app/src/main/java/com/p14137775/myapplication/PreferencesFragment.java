package com.p14137775.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import wrappers.SQLWrapper;

import static android.content.Context.MODE_PRIVATE;
import static com.p14137775.myapplication.R.xml.settings_preferences;

public class PreferencesFragment extends PreferenceFragment {

    private SharedPreferences prefs;
    Preference login;
    Preference logout;
    PreferenceScreen screen;
    boolean loggedin;

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(settings_preferences);
        prefs = getActivity().getSharedPreferences("preferences", MODE_PRIVATE);
        prefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("loggedIn") && !loggedin) {
                    screen.removePreference(login);
                    screen.addPreference(logout);
                    loggedin = true;
                }
            }
        });
        loggedin = prefs.getBoolean("loggedIn", false);
        login = findPreference("login");
        logout = findPreference("logout");
        screen = getPreferenceScreen();
        if (loggedin) {
            screen.removePreference(login);
        } else {
            screen.removePreference(logout);
        }


        logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Logged out", Toast.LENGTH_SHORT).show();
                screen.addPreference(login);
                screen.removePreference(logout);
                SQLWrapper db = new SQLWrapper(getActivity().getApplicationContext(), prefs);
                db.logoutUser();
                return true;
            }
        });
        final ListPreference units = (ListPreference) findPreference("units");
        if (prefs.getBoolean("loggedIn", false))
        if (prefs.getBoolean("kg", true)) {
            units.setValueIndex(0);
        } else {
            units.setValueIndex(1);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
         if (loggedin && (!prefs.getBoolean("loggedIn", true))) {
             screen.removePreference(logout);
             screen.addPreference(login);
             loggedin = false;
         } else if (!loggedin && prefs.getBoolean("loggedIn", false)) {
             screen.removePreference(login);
             screen.addPreference(logout);
             loggedin = true;
         }
    }
}
