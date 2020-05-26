package classes;


import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import wrappers.SQLWrapper;

public class Day {
    private String planName;
    private int dayNumber;
    private String exercises;
    private String sets;
    private String reps;

    public Day(String planName, int dayNumber, String exercises, String sets, String reps) {
        this.planName = planName;
        this.dayNumber = dayNumber;
        this.exercises = exercises;
        this.sets = sets;
        this.reps = reps;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public ArrayList<Exercise> getExercises(SQLWrapper db) {
        ArrayList<Exercise> exercises = new ArrayList<>();
        try {
            JSONArray jArr = new JSONArray(this.exercises);
            for (int i = 0; i < jArr.length(); i++) {
                exercises.add(db.getExercise(jArr.getString(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return exercises;
    }

    public String getExercisesJSON() {
        return exercises;
    }

    public void setExercises(String exercises) {
        this.exercises = exercises;
    }

    public String getSets() {
        return sets;
    }

    public void setSets(String sets) {
        this.sets = sets;
    }

    public String getReps() {
        return reps;
    }

    public void setReps(String reps) {
        this.reps = reps;
    }
}
