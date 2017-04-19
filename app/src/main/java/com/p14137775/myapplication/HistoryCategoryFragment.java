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

public class HistoryCategoryFragment extends Fragment {

    private OnCategorySelected mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_historycategory, parent, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        String[] areas = {"Exercises", "Plans", "All"};
        final ListView listView = (ListView) view.findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.layout_exerciseitem, R.id.textView, areas);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String category = (String) listView.getItemAtPosition(position);
                mCallback.onCategorySelected(category);
            }
        });
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCategorySelected) {
            mCallback = (OnCategorySelected) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement OnCategorySelected");
        }
    }

    interface OnCategorySelected {
        void onCategorySelected(String name);
    }
}
