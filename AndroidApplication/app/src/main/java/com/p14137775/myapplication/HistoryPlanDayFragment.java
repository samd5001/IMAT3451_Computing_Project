package com.p14137775.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import classes.Plan;
import wrappers.SQLWrapper;

public class HistoryPlanDayFragment extends Fragment {

    Plan plan;
    private OnPlanDaySelected mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        plan = new SQLWrapper(getActivity().getApplicationContext()).getPlan(getArguments().getString("name"));
        return inflater.inflate(R.layout.fragment_historycategory, parent, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        TextView text = (TextView) view.findViewById(R.id.textView);
        text.setText(plan.getName());
        ArrayList<String> days = new ArrayList<>();
        try {
            JSONArray jArr = new JSONArray(plan.getDays());
            for (int i = 0; i < jArr.length(); i++) {
                days.add(String.valueOf(jArr.getInt(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ListView listView = (ListView) view.findViewById(R.id.listView);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.layout_exerciseitem, R.id.textView, days);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String daynum = (String) listView.getItemAtPosition(position);
                if (!daynum.equals("No records have been made yet, Get to it!")) {
                    mCallback.onPlanDaySelected(plan.getName(), daynum);
                }
            }
        });
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPlanDaySelected) {
            mCallback = (OnPlanDaySelected) context;
        } else {
            throw new ClassCastException(context.toString());
        }
    }

    interface OnPlanDaySelected {
        void onPlanDaySelected(String planname, String daynum);
    }
}
