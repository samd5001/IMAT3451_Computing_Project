package com.p14137775.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class DummyFragment extends Fragment {

        public static final String COLOR = "color";
        public static final String TEXT = "text";

        public static Fragment newInstance(String text, int color) {
            DummyFragment f = new DummyFragment();
            Bundle args = new Bundle();
            args.putInt(COLOR, color);
            args.putString(TEXT, text);
            f.setArguments(args);
            return f;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment, container, false);
            FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.frameLayout);
            TextView textView = (TextView) view.findViewById(R.id.textView2);
            frameLayout.setBackgroundColor(getArguments().getInt(COLOR));
            textView.setText(getArguments().getString(TEXT));
            return view;
        }
}
