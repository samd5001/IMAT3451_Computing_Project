package com.p14137775.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import classes.Plan;
import wrappers.SQLWrapper;

public class PlanSearchFragment extends Fragment{

    private OnPlanSelected mCallback;
    private ArrayList<Plan> plans;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        plans = new SQLWrapper(getActivity().getApplicationContext()).getPlans();
        return inflater.inflate(R.layout.fragment_plansearch, parent, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        final ListView listView = (ListView) view.findViewById(R.id.listView);
        PlanAdapter adapter = new PlanAdapter(getActivity().getApplicationContext(), R.layout.layout_planitem, plans);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Plan plan = (Plan) listView.getItemAtPosition(position);
                mCallback.onPlanSelected(plan);
            }
        });
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPlanSelected) {
            mCallback = (OnPlanSelected) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement PlanSearchFragment.OnPlanSelected");
        }
    }

    interface OnPlanSelected {
        void onPlanSelected(Plan plan);
    }

    private static class PlanAdapter extends ArrayAdapter {

        private PlanAdapter(Context context, int resource, List<Plan> objects) {
            super(context, resource, objects);
        }

        @SuppressLint("InflateParams")
        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

            View v = convertView;

            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.layout_planitem, null);
            }

            Plan plan = (Plan) getItem(position);

            if (plan != null) {
                TextView title = (TextView) v.findViewById(R.id.textView);
                TextView description = (TextView) v.findViewById(R.id.textView2);
                title.setText(plan.getName());
                description.setText(plan.getName());
                description.setText(plan.getDescription());

            }

            return v;
        }
    }
}
