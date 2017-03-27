package classes;


import java.util.ArrayList;
import java.util.Date;

public class ExerciseRecord {
    private int exerciseID;
    private int workoutID;
    private Date date;
    private ArrayList<Set> sets;

    public ExerciseRecord(int exerciseID) {
        this.exerciseID = exerciseID;
        this.date = new Date();
        this.sets = new ArrayList<>();
    }

    public ExerciseRecord(int exerciseID, int workoutID) {
        this.exerciseID = exerciseID;
        this.workoutID = workoutID;
        this.date = new Date();
        this.sets = new ArrayList<>();
    }

    public ExerciseRecord(int exerciseID, int workoutID, String date, String sets) {
        this.exerciseID = exerciseID;
        this.workoutID = workoutID;
        this.date = new Date();
        this.sets = new ArrayList<>();
    }

    public int getExerciseID() {
        return exerciseID;
    }

    public int getWorkoutID() {
        return workoutID;
    }

    public Date getDate() {
        return date;
    }

    public ArrayList getSets() {
        return sets;
    }
}
