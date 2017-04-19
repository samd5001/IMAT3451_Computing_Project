package com.p14137775.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import classes.Day;
import classes.Exercise;
import classes.ExerciseRecord;
import classes.Plan;
import views.DayView;
import wrappers.SQLWrapper;

public class PlanDetailsFragment extends Fragment {

    private OnPlanBegin mCallback;
    private Plan plan;
    private SQLWrapper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        plan = ((MainActivity) getActivity()).getPlan();
        db = ((MainActivity) getActivity()).getDb();
        return inflater.inflate(R.layout.fragment_plandetails, parent, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(plan.getName());
        try {
            JSONArray days = new JSONArray(plan.getDays());
            ArrayList<Day> daysArray = new ArrayList<>();
            ArrayList<Exercise> exerciseArray = new ArrayList<>();
            for (int i = 0; i < days.length(); i++) {
                daysArray.add(db.getDay(plan.getName(), days.getInt(i)));
                ArrayList<Exercise> lastExercise = daysArray.get(i).getExercises(db);
                exerciseArray.add(lastExercise.get(lastExercise.size() - 1));
            }
            ExerciseRecord lastRecord = db.getLastPlanRecord(exerciseArray.get(0).getName(), daysArray.get(0).getPlanName(), daysArray.get(0).getDayNumber());
            int lastDay = 0;
            if (lastRecord != null) {
                lastDay++;
                ExerciseRecord nextRecord = db.getLastPlanRecord(exerciseArray.get(1).getName(), daysArray.get(1).getPlanName(), daysArray.get(1).getDayNumber());
                while (nextRecord != null && lastDay < days.length() && (nextRecord.getTime().compareTo(lastRecord.getTime())) > 0) {
                    lastDay++;
                    if (lastDay < days.length()) {
                        lastRecord = nextRecord;
                        nextRecord = db.getLastPlanRecord(exerciseArray.get(lastDay).getName(), daysArray.get(lastDay).getPlanName(), daysArray.get(lastDay).getDayNumber());
                    }
                }
                if (lastDay == days.length()) {
                    lastDay = 0;
                }
            }

            final Day day = db.getDay(plan.getName(), days.getInt(lastDay));
            DayView dayView = new DayView(getActivity().getApplicationContext(), db, day);
            dayView.setGravity(Gravity.CENTER_HORIZONTAL);
            ((ViewGroup) title.getParent().getParent()).addView(dayView);
            dayView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onPlanBegin(day);
                }
            });
            int dayDifference = 0;
            if (lastDay + 1 != days.length()) {
                dayDifference = db.getDay(plan.getName(), days.getInt(lastDay + 1)).getDayNumber() - day.getDayNumber();
            }
            if (dayDifference > 1) {
                for (int d = 0; d < dayDifference - 1; d++) {
                    dayView = new DayView(getActivity().getApplicationContext());
                    dayView.setGravity(Gravity.CENTER_HORIZONTAL);
                    dayView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Rest day!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    ((ViewGroup) title.getParent().getParent()).addView(dayView);
                }
            }
            for (int i = lastDay + 1; i < days.length(); i++) {
                Day afterDay = db.getDay(plan.getName(), days.getInt(i));
                if (i < days.length() - 1) {
                    dayView = new DayView(getActivity().getApplicationContext(), db, afterDay);
                    dayView.setGravity(Gravity.CENTER_HORIZONTAL);
                    dayView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Complete the other days first!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    ((ViewGroup) title.getParent().getParent()).addView(dayView);
                    dayDifference = db.getDay(plan.getName(), days.getInt(i + 1)).getDayNumber() - afterDay.getDayNumber();
                    if (dayDifference > 1) {
                        for (int d = 0; d < dayDifference - 1; d++) {
                            dayView = new DayView(getActivity().getApplicationContext());
                            dayView.setGravity(Gravity.CENTER_HORIZONTAL);
                            dayView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            "Rest day!", Toast.LENGTH_SHORT).show();
                                }
                            });
                            ((ViewGroup) title.getParent().getParent()).addView(dayView);
                        }
                    }
                } else {
                    if (afterDay.getDayNumber() < 7) {
                        dayView = new DayView(getActivity().getApplicationContext(), db, afterDay);
                        dayView.setGravity(Gravity.CENTER_HORIZONTAL);
                        dayView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Complete the other days first!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        ((ViewGroup) title.getParent().getParent()).addView(dayView);
                        for (int d = afterDay.getDayNumber(); d < 7; d++) {
                            dayView = new DayView(getActivity().getApplicationContext());
                            dayView.setGravity(Gravity.CENTER_HORIZONTAL);
                            dayView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            "Rest day!", Toast.LENGTH_SHORT).show();
                                }
                            });
                            ((ViewGroup) title.getParent().getParent()).addView(dayView);
                        }
                    }
                }
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
