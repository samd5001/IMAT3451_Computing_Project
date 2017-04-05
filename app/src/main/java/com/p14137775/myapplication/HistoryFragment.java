package com.p14137775.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static android.content.Context.MODE_PRIVATE;

public class HistoryFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_placeholder, parent, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            return;
        }

        if (getActivity().getSharedPreferences("preferences", MODE_PRIVATE).getBoolean("firstRun", true)) {
            WelcomeFragment welcome = new WelcomeFragment();
            Bundle args = new Bundle();
            args.putString("title", "History");
            args.putBoolean("last", true);
            welcome.setArguments(args);
            getChildFragmentManager().beginTransaction().replace(R.id.placeholder, welcome, "welcome").commitAllowingStateLoss();
        } else
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.placeholder, new HistoryCategoryFragment(), "historyCategories").addToBackStack(null).commit();
    }
}
