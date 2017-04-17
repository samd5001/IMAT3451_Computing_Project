package com.p14137775.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import classes.Plan;
import wrappers.SQLWrapper;

public class PlanCurrentFragment extends Fragment {
    private OnCurrentSelect mCallback;
    private SQLWrapper db;
    private SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        prefs = ((MainActivity) getActivity()).getPrefs();
        db = ((MainActivity) getActivity()).getDb();
        return inflater.inflate(R.layout.fragment_plancurrent, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView description = (TextView) view.findViewById((R.id.description));
        ImageButton search = (ImageButton) view.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onBrowse();
            }
        });
        if (prefs.getString("currentPlan", "").equals("")) {
            title.setText(R.string.selectplan);
            description.setText(R.string.browse);
        } else {
            title.setText(prefs.getString("currentPlan", ""));
            description.setText(R.string.nextday);
            ((LinearLayout)description.getParent()).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onPlanContinue(db.getPlan(prefs.getString("currentPlan", "")));
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCurrentSelect) {
            mCallback = (OnCurrentSelect) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement PlanCurrentFragment.OnCurrentSelect");
        }
    }

    interface OnCurrentSelect {
        void onPlanContinue(Plan plan);

        void onBrowse();
    }
}
