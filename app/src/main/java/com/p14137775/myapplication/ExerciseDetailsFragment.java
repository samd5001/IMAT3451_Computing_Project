package com.p14137775.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import classes.Exercise;
import wrappers.VolleyWrapper;

public class ExerciseDetailsFragment extends Fragment {
    private Exercise exercise;
    private OnBegin mCallback;
    private ImageLoader mImageLoader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        exercise = ((MainActivity)getActivity()).getExercise();
        return inflater.inflate(R.layout.fragment_exercisedetails, parent, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        NetworkImageView image = (NetworkImageView) view.findViewById(R.id.networkImageView);
        mImageLoader = VolleyWrapper.getInstance().getImageLoader();
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

    }

    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBegin) {
            mCallback = (OnBegin) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement ExerciseAreasFragment.OnAreaSelected");
        }
    }

    interface OnBegin {
        void onBegin();
    }
}
