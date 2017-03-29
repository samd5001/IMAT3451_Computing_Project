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

import classes.Day;
import classes.Exercise;
import classes.ExerciseRecord;
import classes.Plan;

import static android.R.attr.id;

public class SQLDataWrapper extends SQLiteOpenHelper {
    
    private static final String dbName = "data";
    private static final String tableExercises = "exercises";
    private static final String tablePlans = "plans";
    private static final String tableDays = "days";
    private static final String tableRecords = "records";
    private static final String keyName = "name";
    private static final String keyDescription = "description";
    private static final String keyType = "type";
    private static final String keyImage = "imageURL";
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
    private static final String queryRecords = "SELECT * FROM " + tableRecords + " WHERE " + keyExName + " = '";

    public SQLDataWrapper(Context context) {
        super(context, dbName, null, 1);
    }
    
    @Override
    public void onCreate(final SQLiteDatabase db) {
        String createExercises= "CREATE TABLE " + tableExercises + "(" +
            keyName + " varchar(20) NOT NULL UNIQUE PRIMARY KEY, " +
            keyDescription + " varchar(300) NOT NULL, " +
            keyType + " tinyint NOT NULL, " +
            keyImage + " varchar(50), " +
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
            keyPlanName + " varchar(20) NOT NULL, " +
            keyDayNum + " int NOT NULL, " +
            keyExercises + " varchar(20) NOT NULL, " +
            keySets + " varchar(20) NOT NULL, PRIMARY KEY ( " +
            keyPlanName + " , " + keyDayNum + " ), FOREIGN KEY (" +
            keyPlanName + ") REFERENCES " + tablePlans + "(" + keyName + ") ON DELETE CASCADE)";

        String createRecords = "CREATE TABLE " + tableRecords + "(" +
            keyID + " int NOT NULL PRIMARY KEY, " +
            keyExName + " int NOT NULL, " +
            keyPlanName + " int, " +
            keyTime + " DATETIME NOT NULL, " +
            keySets + " text, FOREIGN KEY (" +
            keyExName + ") REFERENCES " + tableExercises + "(" + keyID +
            ") ON DELETE CASCADE, FOREIGN KEY (" + keyPlanName + ") REFERENCES " + tablePlans + "(" + keyID + "))";

        StringRequest strReq = new StringRequest(Request.Method.GET,
                URLWrapper.exercisesURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jArr = new JSONArray(response);
                    for (int i = 0; i < jArr.length(); i++) {
                        JSONObject jObj = jArr.getJSONObject(i);
                        jObj = jObj.getJSONObject("exercise");
                        Exercise exercise = new Exercise(jObj.getString(keyName), jObj.getString(keyDescription), jObj.getInt(keyType), jObj.getString(keyImage), jObj.getInt(keyMin), jObj.getInt(keyMax), jObj.getString(keyAreas), (jObj.getInt(keyUser) > 0));
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
        VolleyWrapper.getInstance().addToRequestQueue(strReq, "req_exercises");


        strReq = new StringRequest(Request.Method.GET,
                URLWrapper.plansURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jArr = new JSONArray(response);
                    for (int i = 0; i < jArr.length(); i++) {
                        JSONObject jObj = jArr.getJSONObject(i);
                        jObj = jObj.getJSONObject("plan");
                        Plan plan = new Plan(jObj.getString(keyName), jObj.getString(keyDescription), jObj.getString(keyDays), (jObj.getInt("userMade") > 0));
                        storePlan(plan);
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
        VolleyWrapper.getInstance().addToRequestQueue(strReq, "req_plans");

        strReq = new StringRequest(Request.Method.GET,
                URLWrapper.daysURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jArr = new JSONArray(response);
                    for (int i = 0; i < jArr.length(); i++) {
                        JSONObject jObj = jArr.getJSONObject(i);
                        jObj = jObj.getJSONObject("day");
                        Day day = new Day(jObj.getString("planName"), jObj.getInt("dayNumber"), jObj.getString("exercises"), jObj.getString("sets"));
                        storeDay(day);
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
        VolleyWrapper.getInstance().addToRequestQueue(strReq, "req_days");


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
        values.put(keyImage, exercise.getImageURL());
        values.put(keyMin, exercise.getMinThreshold());
        values.put(keyMax, exercise.getMaxThreshold());
        values.put(keyAreas, exercise.getAreasWorked());
        values.put(keyUser, (exercise.isUserMade()) ? 1 : 0);
        db.insert(tableExercises, null, values);
        db.close();
    }

    public Exercise getExercise(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + tableExercises + " WHERE " + keyName + " = '" + name + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        Exercise exercise = new Exercise(cursor.getString(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3), cursor.getInt(4), cursor.getInt(5), cursor.getString(6), (cursor.getInt(7) != 0));
        cursor.close();
        db.close();
        return exercise;
    }

    public void storePlan(Plan plan) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(keyName, plan.getName());
        values.put(keyDescription, plan.getDescription());
        values.put(keyDays, plan.getDays().toString());
        values.put(keyUser, (plan.isUserMade()) ? 1 : 0);
        db.insert(tablePlans, null, values);
        db.close();
    }

    public Plan getPlan(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + tablePlans + " WHERE " + keyName + " = '" + name + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        Plan plan = new Plan(cursor.getString(0), cursor.getString(1), cursor.getString(2), (cursor.getInt(3) != 0));
        cursor.close();
        db.close();
        return plan;
    }

    public void storeDay(Day day) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(keyPlanName, day.getPlanName());
        values.put(keyDayNum, day.getDayNumber());
        values.put(keyExercises, day.getExercises());
        values.put(keySets, day.getSets());
        db.insert(tableDays, null, values);
        db.close();
    }

    public Day detDay(String planName, int dayNum) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + tableDays + " WHERE " + keyPlanName + " = '" + planName + "', " + keyDayNum + " = " + dayNum;
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        Day day = new Day(cursor.getString(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3));
        cursor.close();
        db.close();
        return day;
    }


    public ArrayList<String> getExerciseList(String area) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + tableExercises + " WHERE " + keyAreas + " LIKE '%" + area + "%'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        ArrayList<String> names = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            names.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return names;
    }

    public void storeRecord(ExerciseRecord record) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = (queryRecords +  record.getExerciseName() + "'");
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() == 0) {
            int id = 0;
        } else {
            cursor.moveToLast();
            int id = cursor.getInt(0);
        }
        cursor.close();
        ContentValues values = new ContentValues();
        values.put(keyID, id);
        values.put(keyExName, record.getExerciseName());
        values.put(keyPlanName, record.getPlanName());
        values.put(keyTime, record.getTime());
        values.put(keySets, record.getSets());
        db.insert(tableRecords, null, values);
        db.close();
    }

    public ExerciseRecord getLastRecord(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = (queryRecords + name + "'");
        Cursor cursor = db.rawQuery(selectQuery, null);
        ExerciseRecord record = null;
        if (cursor.getCount() != 0) {
            cursor.moveToLast();
            record = new ExerciseRecord(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        }
        cursor.close();
        db.close();
        return record;
    }

    public ArrayList<ExerciseRecord> getRecords(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = (queryRecords + name + "'");
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<ExerciseRecord> records = new ArrayList<>();
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                records.add(new ExerciseRecord(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return records;
    }
}
