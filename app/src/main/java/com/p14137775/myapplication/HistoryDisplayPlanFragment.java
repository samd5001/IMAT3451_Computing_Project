package com.p14137775.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import classes.ExerciseRecord;
import views.NoRecordView;
import views.RecordView;
import wrappers.SQLWrapper;

public class HistoryDisplayPlanFragment extends Fragment {
    private ArrayList<ExerciseRecord> records;
    private String daynum;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        records = new SQLWrapper(getActivity().getApplicationContext()).getDayRecords(getArguments().getString("name"), Integer.valueOf(getArguments().getString("daynum")));
        daynum = getArguments().getString("daynum");
        return inflater.inflate(R.layout.fragment_historydisplay, parent, false);
    }

    @SuppressLint("SetTextI18n")
    public void onViewCreated(View view, Bundle savedInstanceState) {
        LinearLayout container = (LinearLayout) view.findViewById(R.id.container);
        TextView title = (TextView) view.findViewById(R.id.name);
        title.setText("Day " + daynum);
        if (records.isEmpty()) {
            container.addView(new NoRecordView(getContext()));
        } else {
            for (ExerciseRecord record : records) {
                container.addView(new RecordView(getContext(), record, true));
            }
        }
    }
}
