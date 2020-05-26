package com.p14137775.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class WelcomeFragment extends Fragment {
    private String title;
    private boolean last;
    private OnWelcomeComplete mCallback;

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        title = getArguments().getString("title");
        last = getArguments().getBoolean("last", false);
        return inflater.inflate(R.layout.fragment_welcome, parent, false);
    }

    public void onViewCreated(View view, Bundle savedInstance) {
        TextView title = (TextView) view.findViewById(R.id.textView);
        TextView description = (TextView) view.findViewById(R.id.textView2);
        TextView skip = (TextView) view.findViewById(R.id.textView3);
        TextView next = (TextView) view.findViewById(R.id.textView4);

        title.setText(this.title);
        switch (this.title) {
            case "Tracking":
                description.setText(R.string.exerciseWelcome);
                break;
            case "Plans":
                description.setText(R.string.planWelcome);
                break;
            case "History":
                description.setText(R.string.historyWelcome);
                break;
        }

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onWelcomeComplete();
            }
        });

        if (!last) {
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) getActivity()).incrementTab();
                }
            });
        } else {
            ((ViewGroup) skip.getParent()).removeView(skip);
            next.setText(R.string.finish);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onWelcomeComplete();
                }
            });
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWelcomeComplete) {
            mCallback = (OnWelcomeComplete) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement WelcomeFragment.OnWelcomeComplete");
        }
    }


    interface OnWelcomeComplete {
        void onWelcomeComplete();
    }
}
