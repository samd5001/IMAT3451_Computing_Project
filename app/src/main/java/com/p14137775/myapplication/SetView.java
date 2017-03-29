package com.p14137775.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SetView extends LinearLayout {

    View view;
    TextView setNum;
    EditText reps;
    EditText weight;

    public SetView(Context context) {
        super(context);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_setview, this);
    }

    public SetView(Context context, AttributeSet attrs) {
        super (context, attrs);
        
    }

    private void init(Context context) {
        view = inflate(context, R.layout.layout_setview, this);
        setNum = (TextView) view.findViewById(R.id.set);
        reps = (EditText) view.findViewById(R.id.reps);
        weight = (EditText) view.findViewById(R.id.weight);
    }


    @SuppressLint("SetTextI18n")
    public void setSetNum(int num) {
        setNum.setText("Set " + num);
    }

    public void setFields(boolean type) {
        if (type) {
            reps.setHint("Reps");
            weight.setHint("Weight");
        }
    }

    public int getReps() {
        return Integer.valueOf(reps.getText().toString().trim());
    }

    public int getWeight() {
        return Integer.valueOf(weight.getText().toString().trim());
    }



}
