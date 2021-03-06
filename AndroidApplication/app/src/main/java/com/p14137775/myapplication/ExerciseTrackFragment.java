package com.p14137775.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import classes.Exercise;
import classes.ExerciseRecord;
import classes.Set;
import classes.User;
import views.SetView;
import wrappers.SQLWrapper;

public class ExerciseTrackFragment extends Fragment {
    private Exercise exercise;
    private OnComplete mCallback;
    private ArrayList<SetView> sets;
    private User user;
    private boolean submitted;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        exercise = ((MainActivity) getActivity()).getExercise();
        user = ((MainActivity) getActivity()).getDb().getUser();
        submitted = false;
        return inflater.inflate(R.layout.fragment_exercisetrack, parent, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        TextView name = (TextView) view.findViewById(R.id.textView);
        final ViewGroup vg = (ViewGroup) name.getParent();
        ImageView add = (ImageView) view.findViewById(R.id.imageView);
        ImageView remove = (ImageView) view.findViewById(R.id.imageView2);
        Button complete = (Button) view.findViewById(R.id.button);
        NetworkImageView image = (NetworkImageView) view.findViewById(R.id.networkImageView);
        ((ViewGroup) image.getParent()).removeView(image);
        final SQLWrapper db = ((MainActivity) getActivity()).getDb();
        name.setText(exercise.getName());
        int setNum = 1;
        final ExerciseRecord record = db.getLastRecord(exercise.getName());
        if (record != null) {
            JSONArray sets = record.getSetsJSON();
            setNum = sets.length();
        }
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        sets = new ArrayList<>();
        for (int i = 0; i < setNum; i++) {
            SetView set = new SetView(getContext());
            if (record != null) {
                try {
                    JSONObject lastSet = record.getSetsJSON().getJSONObject(i);
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
                try {
                    if (checkSets(exercise, sets) || submitted) {
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
                                        Toast.makeText(getContext(), "Record saved", Toast.LENGTH_SHORT).show();
                                        mCallback.onComplete();
                                    }
                                })
                                .setNegativeButton("No", null).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    boolean checkSets(Exercise exercise, ArrayList<SetView> sets) throws JSONException {
        ArrayList<ExerciseRecord> previousRecords = new SQLWrapper(getContext()).getLastRecords(exercise.getName());
        boolean setsMatch = false;
        if (previousRecords.size() == 3) {
            setsMatch = true;
            for (int i = 0; i < 4; i++) {
                JSONArray previousSets = previousRecords.get(i).getSetsJSON();
                JSONArray nextSets = previousRecords.get(i + 1).getSetsJSON();
                if (previousSets.length() <= nextSets.length()) {
                    for (int d = 0; d < previousSets.length(); d++) {
                        Set previousSet = new Set(previousSets.getJSONObject(d));
                        Set nextSet = new Set(nextSets.getJSONObject(d));
                        if (previousSet.getWeight() != nextSet.getWeight()) {
                            setsMatch = false;
                        }
                    }
                }
            }
        }
        if (setsMatch) {
            Toast.makeText(getActivity().getApplicationContext(),
                    "You've been doing the same weight for a while now, consider upping them", Toast.LENGTH_LONG).show();
            submitted = true;
            return false;
        }
        JSONArray lastSets = null;
        if (!previousRecords.isEmpty()) {
            lastSets = previousRecords.get(previousRecords.size() - 1).getSetsJSON();
        }
        int i = 0;
        for (SetView set : sets) {

            if (set.getReps() == 0) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Please enter the number of reps on " + set.getSets(), Toast.LENGTH_LONG).show();
                return false;
            }
            if (set.getWeight() == 0) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Please enter the weight on " + set.getSets(), Toast.LENGTH_LONG).show();
                return false;
            }
            if (set.getReps() > 30) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Consider doing a heavier weight on " + set.getSets(), Toast.LENGTH_LONG).show();
                submitted = true;
            }
            if (set.getReps() > 40) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Too many reps, please enter a valid ammount" + set.getSets(), Toast.LENGTH_LONG).show();
                return false;
            }

            if (lastSets != null) {
                Set setObj = new Set(lastSets.getJSONObject(i));
                if (set.getWeight() < setObj.getWeight() && set.getReps() < setObj.getReps()) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Consider doing a higher weight or more reps. Your last record was better on " + set.getSets(), Toast.LENGTH_LONG).show();
                    submitted = true;
                }
            } else if (user != null) {
                switch (user.getGoal()) {
                    case 0:
                        if (set.getReps() > 5) {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Consider doing a heavier weight on " + set.getSets(), Toast.LENGTH_LONG).show();
                            submitted = true;
                            return false;
                        }
                        break;
                    case 1:
                        if (set.getReps() < 8) {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Consider doing a lighter weight on " + set.getSets(), Toast.LENGTH_LONG).show();
                            submitted = true;
                            return false;
                        }
                        if (set.getReps() > 12) {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Consider doing a heavier weight on " + set.getSets(), Toast.LENGTH_LONG).show();
                            submitted = true;
                            return false;
                        }
                        break;
                    case 2:
                        if (set.getReps() < 10) {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Consider doing a lighter weight on " + set.getSets(), Toast.LENGTH_LONG).show();
                            submitted = true;
                            return false;
                        }
                        if (set.getReps() > 20) {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Consider doing a heavier weight on " + set.getSets(), Toast.LENGTH_LONG).show();
                            submitted = true;
                            return false;
                        }
                    default:
                        break;
                }
            }
            if (set.getWeight() > exercise.getMaxThreshold()) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "You're He-Man or you're lying. Enter a valid weight on " + set.getSets(), Toast.LENGTH_LONG).show();
                submitted = true;
                return false;

            }

            if (set.getWeight() < exercise.getMinThreshold()) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "That won't get you anywhere. Do a higher weight on " + set.getSets(), Toast.LENGTH_LONG).show();
                submitted = true;
                return false;
            }
        }
        return true;
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
