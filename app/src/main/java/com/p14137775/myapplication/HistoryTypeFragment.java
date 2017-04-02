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

public class HistoryTypeFragment extends Fragment{

    private OnAreaHistorySelected mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercisearea, parent, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        String[] areas = {"Exercises", "Plans"};
        final ListView listView = (ListView) view.findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.layout_exerciseitem, R.id.textView, areas);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String type = (String) listView.getItemAtPosition(position);
                mCallback.onAreaHistorySelected(type);
            }
        });
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAreaHistorySelected) {
            mCallback = (OnAreaHistorySelected) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement ExerciseAreasFragment.OnAreaSelected");
        }
    }

    interface OnAreaHistorySelected {
        void onAreaHistorySelected(String area);
    }
}
