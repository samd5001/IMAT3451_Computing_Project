package com.p14137775.myapplication;

import android.content.Context;
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

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import classes.Day;
import classes.Exercise;
import classes.ExerciseRecord;
import views.SetView;
import wrappers.SQLWrapper;
import wrappers.VolleyWrapper;

public class PlanTrackFragment extends ExerciseTrackFragment {
    private Exercise exercise;
    private Day day;
    private OnCompletePlan mCallback;
    private ArrayList<SetView> sets;
    private SQLWrapper db;
    private int exerciseNum;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        db = ((MainActivity)getActivity()).getDb();
        day = (((MainActivity)getActivity()).getDay());
        exercise = ((MainActivity)getActivity()).getPlanExercise();
        exerciseNum = ((MainActivity)getActivity()).getExerciseNum();
        return inflater.inflate(R.layout.fragment_exercisetrack, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        TextView name = (TextView) view.findViewById(R.id.textView);
        NetworkImageView image = (NetworkImageView) view.findViewById(R.id.networkImageView);
        ImageLoader mImageLoader = VolleyWrapper.getInstance().getImageLoader();
        image.setImageUrl(exercise.getImageURL(), mImageLoader);
        final ViewGroup vg = (ViewGroup) image.getParent();
        ImageView add = (ImageView) view.findViewById(R.id.imageView);
        ((ViewGroup)add.getParent()).removeView(add);
        ImageView remove = (ImageView) view.findViewById(R.id.imageView2);
        ((ViewGroup)remove.getParent()).removeView(remove);
        Button complete = (Button) view.findViewById(R.id.button);
        name.setText(exercise.getName());
        int setNum = 1;
        JSONArray reps = null;
        try {
            JSONArray sets = new JSONArray(day.getSets());
            setNum = sets.getInt(exerciseNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            reps = new JSONArray(day.getReps()).getJSONArray(exerciseNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity= Gravity.CENTER;
        sets = new ArrayList<>();
        for (int i = 0; i < setNum; i++) {
            SetView set = new SetView(getContext());
            set.setLayoutParams(params);
            set.setSetNum(i + 1);
            try {
                if (reps != null) {
                    set.setReps(reps.getInt(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            sets.add(set);
            vg.addView(set, i + 1);
        }





        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (checkSets(exercise, sets)) {
                        new AlertDialog.Builder(getContext())
                                .setMessage("Do you want to record these values?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        JSONArray setsJSON = new JSONArray();
                                        for (SetView set : sets) {
                                            JSONObject setJSON = set.getJSON();
                                            setsJSON.put(setJSON);
                                        }
                                        ExerciseRecord record = new ExerciseRecord(exercise.getName(), day.getPlanName(), day.getDayNumber(), setsJSON.toString());
                                        db.storeRecord(record, true);
                                        mCallback.onCompletePlan();
                                    }})
                                .setNegativeButton("No", null).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCompletePlan) {
            mCallback = (OnCompletePlan) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement PlanSearchFragment.OnPlanSelected");
        }
    }

    interface OnCompletePlan {
        void onCompletePlan();
    }
}
