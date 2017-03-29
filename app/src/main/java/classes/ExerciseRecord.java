package classes;


import org.json.JSONArray;
import org.json.JSONException;

import wrappers.DateTimeWrapper;

public class ExerciseRecord {
    private String exerciseName;
    private String planName;
    private String time;
    private String sets;

    public ExerciseRecord(String exerciseName, String sets) {
        this.exerciseName = exerciseName;
        this.planName = "";
        this.time = new DateTimeWrapper().sqlReady();
        this.sets = "";
    }

    public ExerciseRecord(String exerciseName, String planName, String sets) {
        this.exerciseName = exerciseName;
        this.planName = planName;
        this.time = new DateTimeWrapper().sqlReady();
        this.sets = "";
    }

    public ExerciseRecord(String exerciseName, String planName, String time, String sets) {
        this.exerciseName = exerciseName;
        this.planName = planName;
        this.time = time;
        this.sets = sets;
    }

    public void addFirstSet(String set) {
        sets = "[" + set;
    }

    public void addSet(String set) {
        sets = sets + ", " + set;
    }

    public void addLastSet(String set) {
        sets = sets + ", " + set + "]";
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public String getPlanName() {
        return planName;
    }

    public String getTime() {
        return time;
    }

    public String getSets() {
        return sets;
    }

    public JSONArray getSetsJSON() {
        JSONArray sets = null;
        try {
            sets = new JSONArray(this.sets);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sets;
    }
}
