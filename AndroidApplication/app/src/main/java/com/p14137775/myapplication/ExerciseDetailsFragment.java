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
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import classes.Exercise;
import classes.ExerciseRecord;
import views.RecordView;
import wrappers.SQLWrapper;
import wrappers.VolleyWrapper;

public class ExerciseDetailsFragment extends Fragment {
    private Exercise exercise;
    private OnDetail mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        exercise = ((MainActivity) getActivity()).getExercise();
        return inflater.inflate(R.layout.fragment_exercisedetails, parent, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        NetworkImageView image = (NetworkImageView) view.findViewById(R.id.networkImageView);
        ImageLoader mImageLoader = VolleyWrapper.getInstance().getImageLoader();
        image.setImageUrl(exercise.getImageURL(), mImageLoader);
        TextView name = (TextView) view.findViewById(R.id.textView);
        TextView description = (TextView) view.findViewById(R.id.textView2);
        Button button = (Button) view.findViewById(R.id.button);
        name.setText(exercise.getName());
        description.setText(exercise.getDescription());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onBegin();
            }
        });
        ImageView delete = (ImageView) view.findViewById(R.id.imageView);
        ViewGroup vg = (ViewGroup) delete.getParent();
        if (!exercise.isUserMade()) {
            vg.removeView(delete);
        } else {
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getContext())
                            .setMessage("Are you sure you want to delete this exercise?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    new SQLWrapper(getActivity().getApplicationContext()).deleteExercise(exercise.getName());
                                    mCallback.onDelete(exercise);
                                }
                            })
                            .setNegativeButton("No", null).show();
                }
            });
        }
        ArrayList<ExerciseRecord> records = new SQLWrapper(getContext()).getLastTwoRecords(exercise.getName());
        if (!records.isEmpty()) {
            for (ExerciseRecord record : records) {
                vg.addView(new RecordView(getContext(), record, false));
            }
        }

    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDetail) {
            mCallback = (OnDetail) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement ExerciseDetailsFragment.OnDetail");
        }
    }

    interface OnDetail {
        void onBegin();

        void onDelete(Exercise exercise);
    }
}
