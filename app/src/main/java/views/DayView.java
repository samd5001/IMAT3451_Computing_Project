package views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.p14137775.myapplication.R;

import java.util.ArrayList;

import classes.Day;
import classes.Exercise;
import wrappers.SQLWrapper;

public class DayView extends LinearLayout {
    private TextView title;
    private LinearLayout exercises;

    public DayView(Context context , SQLWrapper db, Day day) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_dayview, this);
        init(db, day);
    }

    public DayView(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_dayview, this);
        init(null, null);
    }

    @SuppressLint("SetTextI18n")
    private void init(SQLWrapper db, Day day) {
        title = (TextView) findViewById(R.id.title);
        exercises = (LinearLayout) findViewById(R.id.exerciseContainer);
        if (day == null) {
            title.setText("Rest day");
            TextView desc = (TextView) findViewById(R.id.description);
            desc.setText("No exercises for this day");
        } else {
            title.setText("Day " + day.getDayNumber());
            ArrayList<Exercise> exercise = day.getExercises(db);
            for (int i = 0; i < exercise.size(); i++) {
                ExerciseView exerciseView = new ExerciseView(getContext());
                exerciseView.setName(exercise.get(i).getName());
                exercises.addView(exerciseView);
            }
        }
    }
}
