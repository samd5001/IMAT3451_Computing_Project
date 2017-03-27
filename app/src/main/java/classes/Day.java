package classes;


import java.util.ArrayList;

public class Day {
    private int dayNumber;
    private int daysAfter;
    private ArrayList exercises;

    public Day(int dayNumber, int daysAfter, ArrayList exercises) {
        this.dayNumber = dayNumber;
        this.daysAfter = daysAfter;
        this.exercises = exercises;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public int getDaysAfter() {
        return daysAfter;
    }

    public void setDaysAfter(int daysAfter) {
        this.daysAfter = daysAfter;
    }

    public ArrayList getExercises() {
        return exercises;
    }

    public void setExercises(ArrayList exercises) {
        this.exercises = exercises;
    }
}
