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

public class ExerciseAreasFragment extends Fragment{

    private OnAreaSelected mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercisearea, parent, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        String[] areas = {"Chest", "Arms", "Legs", "Core", "Back", "Shoulders"};
        final ListView listView = (ListView) view.findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.layout_exerciseitem, R.id.textView, areas);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String area = (String) listView.getItemAtPosition(position);
                mCallback.onAreaSelected(area);
            }
        });
        view.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onExerciseAdd();
            }
        });
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAreaSelected) {
            mCallback = (OnAreaSelected) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement ExerciseAreasFragment.OnAreaSelected");
        }
    }

    interface OnAreaSelected {
        void onAreaSelected(String area);
        void onExerciseAdd();
    }
}
