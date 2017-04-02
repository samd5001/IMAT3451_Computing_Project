package com.p14137775.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ExerciseView extends LinearLayout {
    private TextView name;

    public ExerciseView(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_exerciseview, this);
        init();
    }

    public ExerciseView(Context context, AttributeSet attrs) {
        super (context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_setview, this);
        init();
    }

    private void init() {
        name = (TextView) findViewById(R.id.title);
    }

    public void setName(String name) {
        this.name.setText(name);
    }
}
