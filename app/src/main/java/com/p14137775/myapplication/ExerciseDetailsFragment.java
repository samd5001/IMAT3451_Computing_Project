package com.p14137775.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import classes.Exercise;
import wrappers.SQLExercisesWrapper;

public class ExerciseDetailsFragment extends android.support.v4.app.Fragment {
    private Exercise exercise;
    private OnBegin mCallback;
    SQLExercisesWrapper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        db = ((MainActivity)getActivity()).getDb();
        exercise = db.getExercise(getArguments().getString("name"));
        return inflater.inflate(R.layout.fragment_exercisedetails, parent, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        ImageView image = (ImageView) view.findViewById(R.id.imageView);
        TextView name = (TextView) view.findViewById(R.id.textView);
        TextView description = (TextView) view.findViewById(R.id.textView2);
        Button button = (Button) view.findViewById(R.id.button);
        name.setText(exercise.getName());
        description.setText(exercise.getDescription());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onBegin();
            }
        });

    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBegin) {
            mCallback = (OnBegin) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement ExerciseAreasFragment.OnAreaSelected");
        }
    }

    interface OnBegin {
        void onBegin();
    }
}
