package views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.p14137775.myapplication.R;

import classes.Set;


public class SetRecordView extends LinearLayout{
    private TextView reps;
    private TextView weight;

    public SetRecordView(Context context, Set set) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_setrecordview, this);
        init(set);
    }

    public SetRecordView(Context context, AttributeSet attrs) {
        super (context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_setrecordview, this);
    }

    private void init(Set set) {
        reps = (TextView) findViewById(R.id.reps);
        weight = (TextView) findViewById(R.id.weight);
        reps.setText(String.valueOf(set.getReps()));
        weight.setText(String.valueOf(set.getWeight()));
    }

    public void setSet(String reps, String weight) {
        this.reps.setText(reps);
        this.weight.setText(weight);
    }
}

