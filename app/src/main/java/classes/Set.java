package classes;

import org.json.JSONException;
import org.json.JSONObject;

public class Set {
    private int reps;
    private float time;
    private float weight;


    public Set(int reps, float weight) {
        this.reps = reps;
        this.weight = weight;
        this.time = 0;
    }

    public Set(float time) {
        this.time = time;
        this.reps = 0;
        this.weight = 0;
    }

    public Set(JSONObject set) {
        try {
            reps = set.getInt("reps");
            time = set.getLong("time");
            weight = set.getLong("weight");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String toJSONString() {
        JSONObject json = new JSONObject();
        try {
            json.put("reps", reps);
            json.put("time", time);
            json.put("weight", weight);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}
