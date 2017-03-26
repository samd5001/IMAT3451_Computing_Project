package com.p14137775.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;
import static com.p14137775.myapplication.R.xml.preferences;

public class PreferencesFragment extends PreferenceFragment {
    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(preferences);
        final SharedPreferences sharedpreferences = getActivity().getSharedPreferences("preferences", MODE_PRIVATE);
        final Preference hiddenPreference;
        final PreferenceScreen screen = getPreferenceScreen();
        if (sharedpreferences.getBoolean("loggedIn", false))
        {
            hiddenPreference = findPreference("login");
            screen.removePreference(hiddenPreference);
            Preference logOut = findPreference("logout");
            logOut.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    sharedpreferences.edit().putBoolean("loggedIn", false).apply();
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Logged out", Toast.LENGTH_SHORT).show();
                    screen.addPreference(hiddenPreference);
                    screen.removePreference(findPreference("logout"));
                    return true;
                }
            });
        }
        else
        {
            hiddenPreference = findPreference("logout");
            screen.removePreference(hiddenPreference);
        }
    }
}
