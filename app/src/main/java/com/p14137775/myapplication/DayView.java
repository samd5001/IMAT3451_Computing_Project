package com.p14137775.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import classes.Day;
import classes.Exercise;
import wrappers.SQLWrapper;

public class DayView extends LinearLayout {
    private TextView title;
    private LinearLayout exercises;

    public DayView(Context context , SQLWrapper db, Day day) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_dayview, this);
        init(db, day);
    }

    public DayView(Context context, AttributeSet attrs, SQLWrapper db, Day day) {
        super (context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_dayview, this);
        init(db, day);
    }

    private void init(SQLWrapper db, Day day) {
        title = (TextView) findViewById(R.id.title);
        exercises = (LinearLayout) findViewById(R.id.exerciseContainer);
        title.setText("Day " + day.getDayNumber());
        try {
            JSONArray jArr = new JSONArray(day.getExercises());
            for (int i = 0; i < jArr.length(); i++) {
                Exercise exercise = db.getExercise(jArr.getString(i));
                ExerciseView exerciseView = new ExerciseView(getContext());
                exerciseView.setName(exercise.getName());
                exercises.addView(exerciseView);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
