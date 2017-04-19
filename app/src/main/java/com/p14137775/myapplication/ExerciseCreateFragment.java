package com.p14137775.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;

import classes.Exercise;
import wrappers.SQLWrapper;

public class ExerciseCreateFragment extends Fragment {
    private OnCreateExercise mCallback;
    private SQLWrapper db;
    private int type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercisecreate, parent, false);
    }

    public void onViewCreated(View view, Bundle savedInstance) {
        final EditText nameText = (EditText) view.findViewById(R.id.ename);
        final CheckBox chest = (CheckBox) view.findViewById(R.id.chest);
        final CheckBox arms = (CheckBox) view.findViewById(R.id.arms);
        final CheckBox legs = (CheckBox) view.findViewById(R.id.legs);
        final CheckBox core = (CheckBox) view.findViewById(R.id.core);
        final CheckBox back = (CheckBox) view.findViewById(R.id.back);
        final CheckBox shoulders = (CheckBox) view.findViewById(R.id.shoulders);
        final EditText descriptionText = (EditText) view.findViewById(R.id.description);
        final Spinner typeSpinner = (Spinner) view.findViewById(R.id.type);
        Button save = (Button) view.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String name = nameText.getText().toString().trim();
                final JSONArray areas = new JSONArray();
                if (chest.isChecked()) {
                    areas.put("Chest");
                }
                if (arms.isChecked()) {
                    areas.put("Arms");
                }
                if (legs.isChecked()) {
                    areas.put("Legs");
                }
                if (core.isChecked()) {
                    areas.put("Core");
                }
                if (back.isChecked()) {
                    areas.put("Back");
                }
                if (shoulders.isChecked()) {
                    areas.put("Shoulders");
                }
                switch (String.valueOf(typeSpinner.getSelectedItem())) {
                    case "Free Weight":
                        type = 0;
                        break;
                    case "Body Weight":
                        type = 1;
                        break;
                    case "Machine":
                        type = 2;
                        break;
                    default:
                        break;
                }
                final String description = descriptionText.getText().toString().trim();
                if (name.isEmpty() || areas.length() == 0 || description.isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Please enter a value for each field", Toast.LENGTH_LONG).show();
                } else {
                    new AlertDialog.Builder(getContext())
                            .setMessage("Do you want to save this exercise?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Exercise exercise = new Exercise(name, description, type, areas.toString());
                                    ((MainActivity) getActivity()).getDb().storeExercise(exercise, true);
                                    mCallback.onCreateExercise();
                                }
                            })
                            .setNegativeButton("No", null).show();
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCreateExercise) {
            mCallback = (OnCreateExercise) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement RegisterCompleteFragment.OnRegisterComplete");
        }
    }

    interface OnCreateExercise {
        void onCreateExercise();
    }
}
