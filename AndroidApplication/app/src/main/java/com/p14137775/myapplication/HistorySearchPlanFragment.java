package com.p14137775.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

import wrappers.SQLWrapper;

public class HistorySearchPlanFragment extends Fragment {

    ArrayList<String> plans;
    private OnPlanRecordsSelected mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        plans = new SQLWrapper(getActivity().getApplicationContext()).getPlanNames();
        return inflater.inflate(R.layout.fragment_historysearch, parent, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {

        final ListView listView = (ListView) view.findViewById(R.id.listView);
        final EditText search = (EditText) view.findViewById(R.id.editText);
        Collections.sort(plans, String.CASE_INSENSITIVE_ORDER);
        if (plans.isEmpty()) {
            plans.add("No records have been made yet, Get to it!");
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.layout_exerciseitem, R.id.textView, plans);
        listView.setAdapter(adapter);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) listView.getItemAtPosition(position);
                if (!name.equals("No records have been made yet, Get to it!")) {
                    mCallback.onPlanRecordsSelected(name);
                }
            }
        });
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPlanRecordsSelected) {
            mCallback = (OnPlanRecordsSelected) context;
        } else {
            throw new ClassCastException(context.toString());
        }
    }

    interface OnPlanRecordsSelected {
        void onPlanRecordsSelected(String name);
    }
}
