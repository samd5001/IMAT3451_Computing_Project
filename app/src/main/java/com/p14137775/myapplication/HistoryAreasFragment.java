package com.p14137775.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import wrappers.SQLWrapper;

public class HistoryAreasFragment extends ExerciseAreasFragment {

    private OnHistoryAreaSelected mCallback;


    public void onViewCreated(View view, Bundle savedInstanceState) {
        ArrayList<String> areaRecords = new ArrayList<>();
        String[] areas = {"Chest", "Arms", "Legs", "Core", "Back", "Shoulders"};
        SQLWrapper db = new SQLWrapper(getActivity().getApplicationContext());
        for (String area : areas) {
            if (db.checkAreaRecords(area)) {
                areaRecords.add(area);
            }
        }
        if (areaRecords.isEmpty()) {
            areaRecords.add("No records have been made yet, Get to it!");
        }
        final ListView listView = (ListView) view.findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.layout_exerciseitem, R.id.textView, areaRecords);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String area = (String) listView.getItemAtPosition(position);
                if (!area.equals("No records have been made yet, Get to it!")) {
                    mCallback.onHistoryAreaSelected(area);
                }
            }
        });
        ImageView add = (ImageView) view.findViewById(R.id.add);
        ((ViewGroup) add.getParent()).removeView(add);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHistoryAreaSelected) {
            mCallback = (OnHistoryAreaSelected) context;
        } else {
            throw new ClassCastException();
        }
    }

    interface OnHistoryAreaSelected {
        void onHistoryAreaSelected(String area);
    }
}
