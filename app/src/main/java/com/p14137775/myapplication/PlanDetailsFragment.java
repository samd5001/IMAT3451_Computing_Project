package com.p14137775.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import classes.Day;
import classes.ExerciseRecord;
import classes.Plan;
import classes.QueryValidator;
import views.DayView;
import wrappers.SQLWrapper;

public class PlanDetailsFragment extends Fragment{

    private OnPlanBegin mCallback;
    private Plan plan;
    private SQLWrapper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        plan = ((MainActivity)getActivity()).getPlan();
        db = ((MainActivity)getActivity()).getDb();
        return inflater.inflate(R.layout.fragment_plandetails, parent, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(plan.getName());
        try {
            JSONArray days = new JSONArray(plan.getDays());
            ArrayList<Day> daysArray = new ArrayList<>();
            ArrayList<String> exerciseArray = new ArrayList<>();
            for (int i = 0; i < days.length(); i++) {
                daysArray.add(db.getDay(plan.getName(), days.getInt(i)));
                exerciseArray.add(new JSONArray(daysArray.get(i).getExercises()).getString(0));
            }
            ExerciseRecord lastRecord = db.getLastPlanRecord(exerciseArray.get(0), new QueryValidator(daysArray.get(0).getPlanName()).validateQuery());
            int lastDay = 0;
            if (lastRecord != null) {
                lastDay++;
                ExerciseRecord nextRecord = db.getLastPlanRecord(exerciseArray.get(1), daysArray.get(1).getPlanName());
                if ((nextRecord.getTime().compareTo(lastRecord.getTime())) > 0) {
                    lastDay++;
                    while(lastDay < days.length() && (nextRecord.getTime().compareTo(lastRecord.getTime())) > 0) {
                        lastRecord = nextRecord;
                        nextRecord = db.getLastPlanRecord(exerciseArray.get(lastDay), daysArray.get(lastDay).getPlanName());
                        lastDay++;
                    }
                    if (lastDay == days.length()) {
                        lastDay = 0;
                    }
                }
            }

            final Day day = db.getDay(plan.getName(), days.getInt(lastDay));
            DayView dayView = new DayView(getActivity().getApplicationContext(), db, day);
            dayView.setGravity(Gravity.CENTER_HORIZONTAL);
            ((ViewGroup)title.getParent().getParent()).addView(dayView);
            dayView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onPlanBegin(day);
                }
            });
            for (int i = lastDay + 1; i < days.length(); i++) {
                Day afterDay = db.getDay(plan.getName(), days.getInt(i));
                dayView = new DayView(getActivity().getApplicationContext(), db, afterDay);
                dayView.setGravity(Gravity.CENTER_HORIZONTAL);
                ((ViewGroup)title.getParent().getParent()).addView(dayView);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPlanBegin) {
            mCallback = (OnPlanBegin) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement PlanSearchFragment.OnPlanSelected");
        }
    }

    interface OnPlanBegin {
        void onPlanBegin(Day day);
    }
}
