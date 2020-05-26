package classes;

import org.json.JSONException;
import org.json.JSONObject;

public class Set {
    private int reps;
    private double time;
    private double weight;

    public Set(JSONObject set) {
        try {
            reps = set.getInt("reps");
            time = set.getDouble("time");
            weight = set.getDouble("weight");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getReps() {
        return reps;
    }

    public double getTime() {
        return time;
    }

    public double getWeight() {
        return weight;
    }
}