package classes;


import org.json.JSONArray;
import org.json.JSONException;

import wrappers.DateTimeWrapper;

public class ExerciseRecord {
    private int id;
    private String exerciseName;
    private String planName;
    private int dayNum;
    private String time;
    private String sets;

    public ExerciseRecord(String exerciseName, String sets) {
        this.id = 0;
        this.exerciseName = exerciseName;
        this.planName = "";
        this.dayNum = 0;
        this.time = new DateTimeWrapper().sqlReady();
        this.sets = sets;
    }

    public ExerciseRecord(String exerciseName, String planName, int dayNum, String sets) {
        this.id = 0;
        this.exerciseName = exerciseName;
        this.planName = planName;
        this.dayNum = dayNum;
        this.time = new DateTimeWrapper().sqlReady();
        this.sets = sets;
    }

    public ExerciseRecord(int id, String exerciseName, String planName, int dayNum, String time, String sets) {
        this.id = id;
        this.exerciseName = exerciseName;
        this.planName = planName;
        this.dayNum = dayNum;
        this.time = time;
        this.sets = sets;
    }

    public int getId() {
        return id;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public String getPlanName() {
        return planName;
    }

    public int getDayNum() {
        return dayNum;
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

    public void setId(int id) {
        this.id = id;
    }
}
