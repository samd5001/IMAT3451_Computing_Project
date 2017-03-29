package classes;


public class Day {
    private String planName;
    private int dayNumber;
    private String exercises;
    private String sets;

    public Day(String planName, int dayNumber, String exercises, String sets) {
        this.planName = planName;
        this.dayNumber = dayNumber;
        this.exercises = exercises;
        this.sets = sets;
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

    public String getExercises() {
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
}
