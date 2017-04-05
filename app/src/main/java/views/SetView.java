package views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.p14137775.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

public class SetView extends LinearLayout {
    private TextView setNum;
    private EditText reps;
    private EditText weight;

    public SetView(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_setview, this);
        init();
    }

    public SetView(Context context, AttributeSet attrs) {
        super (context, attrs);
        init();
    }

    private void init() {
        setNum = (TextView) findViewById(R.id.set);
        reps = (EditText) findViewById(R.id.reps);
        weight = (EditText) findViewById(R.id.weight);
    }


    @SuppressLint("SetTextI18n")
    public void setSetNum(int num) {
        setNum.setText("Set " + num);
    }

    public void setWeightReps (double weight, int reps) {
        this.weight.setText(String.valueOf(weight));
        this.reps.setText(String.valueOf(reps));
    }

    public String getSets() {return setNum.getText().toString().trim();}

    public float getReps() {
        if (!reps.getText().toString().isEmpty()) {
            return Float.valueOf(reps.getText().toString().trim());
        } else
            return 0;
    }

    public float getWeight() {
        if (!weight.getText().toString().isEmpty()) {
            return Float.valueOf(weight.getText().toString().trim());
        } else {
            return 0;
        }
    }


    public void setReps(int reps) {
        this.reps.setText(String.valueOf(reps));
    }

    public JSONObject getJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("reps", reps.getText().toString().trim());
            json.put("time", 0);
            json.put("weight", weight.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
