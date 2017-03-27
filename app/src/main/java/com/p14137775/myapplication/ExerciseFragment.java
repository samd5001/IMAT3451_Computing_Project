package com.p14137775.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ExerciseFragment extends Fragment  {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercise, parent, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            return;
        }

        ExerciseAreasFragment areasFragment = new ExerciseAreasFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.placeholder, areasFragment, "registerBegin").addToBackStack(null).commit();
    }

    public void onBackPressed() {

    }
}
