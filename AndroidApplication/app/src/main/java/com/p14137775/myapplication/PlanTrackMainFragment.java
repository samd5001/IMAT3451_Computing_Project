package com.p14137775.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import classes.Day;

public class PlanTrackMainFragment extends Fragment implements PlanDetailsFragment.OnPlanBegin {

    private ArrayList exercises;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_placeholder, parent, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            return;
        }

        getChildFragmentManager().beginTransaction()
                .replace(R.id.placeholder, new PlanTrackFragment(), "planTrack").addToBackStack(null).commit();
    }

    @Override
    public void onPlanBegin(Day day) {

    }
}
