package wrappers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import classes.Exercise;
import classes.ExerciseRecord;

public class SQLExercisesWrapper extends SQLiteOpenHelper {
    
    private static final String dbName = "data";
    private static final String tableExercises = "exercises";
    private static final String tablePlans = "plans";
    private static final String tableDays = "days";
    private static final String tableRecords = "records";
    private static final String keyName = "name";
    private static final String keyDescription = "description";
    private static final String keyType = "type";
    private static final String keyMin = "minThreshold";
    private static final String keyMax = "maxThreshold";
    private static final String keyAreas = "areasWorked";
    private static final String keyUser = "userMade";
    private static final String keyDays = "days";
    private static final String keyTime = "timeDone";
    private static final String keySets = "sets";
    private static final String keyDayNum = "dayNumber";
    private static final String keyExercises = "exercises";
    private static final String keyID = "id";
    private static final String keyExName = "exerciseName";
    private static final String keyPlanName = "planName";

    public SQLExercisesWrapper(Context context) {
        super(context, dbName, null, 1);
    }
    
    @Override
    public void onCreate(final SQLiteDatabase db) {
        String createExercises= "CREATE TABLE " + tableExercises + "(" +
            keyName + " varchar(20) NOT NULL  UNIQUE PRIMARY KEY, " +
            keyDescription + " varchar(300) NOT NULL " +
            keyType + " tinyint NOT NULL, " +
            keyMin + " int, " +
            keyMax + " int, " +
            keyAreas + " varchar(10), " +
            keyUser + " tinyint NOT NULL)";

        String createPlans = "CREATE TABLE " + tablePlans + "(" +
            keyName + " varchar(20) NOT NULL UNIQUE PRIMARY KEY, " +
            keyDescription + " varchar(300) NOT NULL, " +
            keyDays + " varchar(14) NOT NULL, " +
            keyUser + " tinyint NOT NULL)";

        String createDays = "CREATE TABLE " + tableDays + "(" +
            keyPlanName + " int NOT NULL, " +
            keyDayNum + " int NOT NULL, " +
            keyExercises + " varchar(20) NOT NULL, PRIMARY KEY ( " +
            keyPlanName + " , " + keyDayNum + " ), FOREIGN KEY (" +
            keyPlanName + ") REFERENCES " + tablePlans + "(" + keyName + ") ON DELETE CASCADE)";

        String createRecords = "CREATE TABLE " + tableRecords + "(" +
            keyID + " int NOT NULL PRIMARY KEY, " +
            keyExName + " int NOT NULL, " +
            keyPlanName + " int , " +
            keyTime + " DATETIME NOT NULL, " +
            keySets + " text, FOREIGN KEY (" +
            keyExName + ") REFERENCES " + tableExercises + "(" + keyID +
            ") ON DELETE CASCADE, FOREIGN KEY (" + keyPlanName + ") REFERENCES " + tablePlans + "(" + keyID + ")";

        String tag_string_req = "req_exercises";
        StringRequest strReq = new StringRequest(Request.Method.GET,
                URLWrapper.exercisesURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jArr = new JSONArray(response);
                    for (int i = 0; i < jArr.length(); i++) {
                        JSONObject jObj = jArr.getJSONObject(i);
                        Exercise exercise = new Exercise(jObj.getString("name"), jObj.getString("description"), jObj.getInt("type"), jObj.getInt("minThreshold"), jObj.getInt("maxThreshold"), jObj.getString("areasWorked"), (jObj.getInt("userMade") > 0));
                        storeExercise(exercise);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        VolleyWrapper.getInstance().addToRequestQueue(strReq, tag_string_req);

        String getPlans = "SELECT * FROM " + tablePlans + " WHERE " + keyUser + " = 0";

        db.execSQL(createExercises);
        db.execSQL(createPlans);
        db.execSQL(createDays);
        db.execSQL(createRecords);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tableExercises);
        db.execSQL("DROP TABLE IF EXISTS " + tablePlans);
        db.execSQL("DROP TABLE IF EXISTS " + tableDays);
        db.execSQL("DROP TABLE IF EXISTS " + tableRecords);
        onCreate(db);
    }

    public void storeExercise(Exercise exercise) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(keyName, exercise.getName());
        values.put(keyDescription, exercise.getDescription());
        values.put(keyType, exercise.getType());
        values.put(keyMin, exercise.getMinThreshold());
        values.put(keyMax, exercise.getMaxThreshold());
        values.put(keyAreas, exercise.getAreasWorked());
        values.put(keyUser, (exercise.isUserMade()) ? 1 : 0);
        db.insert(tableExercises, null, values);
        db.close();
    }

    public Exercise getExercise(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + tableExercises + " WHERE " + keyName + " = " + name;
        boolean user;
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        Exercise exercise = new Exercise(cursor.getString(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), cursor.getString(5), (cursor.getInt(6) != 0));
        cursor.close();
        return exercise;
    }

    public ArrayList getExerciseList(String area) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + tableExercises + " WHERE " + keyAreas + " LIKE %" + area + "%";
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        ArrayList<String> names = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            names.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return names;
    }

    public void storeRecord(ExerciseRecord record) {

    }
}
