package com.p14137775.myapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import classes.Day;
import classes.Exercise;
import classes.ExerciseRecord;
import classes.User;
import wrappers.SQLWrapper;

public class PlanTrackFragment extends ExerciseTrackFragment {
    private Exercise exercise;
    private Day day;
    private OnComplete mCallback;
    private ArrayList<SetView> sets;
    private User user;
    private SQLWrapper db;
    private int exerciseNum;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        db = ((MainActivity)getActivity()).getDb();
        user = ((MainActivity)getActivity()).getUser();
        day = (((MainActivity)getActivity()).getDay());
        exercise = db.getExercise(getArguments().getString("exercise"));
        exerciseNum = getArguments().getInt("exerciseNum");
        return inflater.inflate(R.layout.fragment_exercisetrack, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        TextView name = (TextView) view.findViewById(R.id.textView);
        final ViewGroup vg = (ViewGroup) name.getParent();
        ImageView add = (ImageView) view.findViewById(R.id.imageView);
        ImageView remove = (ImageView) view.findViewById(R.id.imageView2);
        Button complete = (Button) view.findViewById(R.id.button);
        name.setText(exercise.getName());
     //   JSONArray sets = new JSONArray(pla.)
        int setNum = day.getDayNumber();
        final ExerciseRecord record = db.getLastRecord(exercise.getName());
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity= Gravity.CENTER;
    //    sets = new ArrayList<>();
        for (int i = 0; i < setNum; i++) {
            SetView set = new SetView(getContext());
            if (record != null) {
                try {
                    JSONObject lastSet =  record.getSetsJSON().getJSONObject(i);
                    set.setWeightReps(lastSet.getDouble("weight"), lastSet.getInt("reps"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                if (user != null) {
                    switch (user.getGoal()) {
                        case 0:
                            set.setReps(5);
                            break;
                        case 1:
                            set.setReps(8);
                            break;
                        case 2:
                            set.setReps(12);
                            break;
                        default:
                            break;
                    }
                }
            }
            set.setLayoutParams(params);
            set.setSetNum(i + 1);
            sets.add(set);
            vg.addView(set, i + 1);
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sets.size() > 5) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Too many sets, go easy", Toast.LENGTH_SHORT).show();
                } else {
                    int newIndex = sets.size();
                    SetView set = new SetView(getContext());
                    set.setSetNum(newIndex + 1);
                    set.setLayoutParams(params);
                    sets.add(set);
                    vg.addView(set, newIndex + 1);
                }
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sets.size() < 2) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Can't remove the only set", Toast.LENGTH_SHORT).show();
                } else {
                    vg.removeView(sets.get(sets.size() - 1));
                    sets.remove(sets.size() - 1);
                }
            }
        });





        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSets()) {
                    new AlertDialog.Builder(getContext())
                            .setMessage("Do you want to record these values?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    JSONArray setsJSON = new JSONArray();
                                    for (SetView set : sets) {
                                        JSONObject setJSON = set.getJSON();
                                        setsJSON.put(setJSON);
                                    }
                                    ExerciseRecord record = new ExerciseRecord(exercise.getName(), setsJSON.toString());
                                    db.storeRecord(record, true);
                                    mCallback.onComplete();
                                }})
                            .setNegativeButton("No", null).show();
                }
            }
        });
    }

    interface OnComplete {
        void onComplete();
    }
}
