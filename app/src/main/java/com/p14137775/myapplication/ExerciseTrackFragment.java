package com.p14137775.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.ArrayList;

import classes.Exercise;
import classes.ExerciseRecord;
import wrappers.SQLDataWrapper;

public class ExerciseTrackFragment extends Fragment {
    private Exercise exercise;
    private OnComplete mCallback;
    private ArrayList<SetView> sets;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        exercise = ((MainActivity)getActivity()).getExercise();
        return inflater.inflate(R.layout.fragment_exercisetrack, parent, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        TextView name = (TextView) view.findViewById(R.id.textView);
        ViewGroup vg = (ViewGroup) name.getParent();
        SQLDataWrapper db = ((MainActivity)getActivity()).getDb();
        int setNum = 3;
        ExerciseRecord record = db.getLastRecord(exercise.getName());
        if (record != null) {
                JSONArray sets = record.getSetsJSON();
           setNum = sets.length();
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity= Gravity.CENTER;
        sets = new ArrayList<>();
        for (int i = 0; i < setNum; i++) {
            sets.add(new SetView(getContext()));
            vg.addView(sets.get(i), i + 1);
            sets.get(i).setLayoutParams(params);
        }


        Button button = (Button) view.findViewById(R.id.button);
        name.setText(exercise.getName());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onComplete();
            }
        });
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnComplete) {
            mCallback = (OnComplete) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement ExerciseAreasFragment.OnAreaSelected");
        }
    }

    interface OnComplete {
        void onComplete();
    }
}
