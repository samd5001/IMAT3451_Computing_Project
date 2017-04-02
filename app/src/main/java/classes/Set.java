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
            time = set.getLong("time");
            weight = set.getLong("weight");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
