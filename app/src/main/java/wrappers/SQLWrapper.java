package wrappers;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import classes.Day;
import classes.Exercise;
import classes.ExerciseRecord;
import classes.Plan;
import classes.QueryValidator;
import classes.User;

public class SQLWrapper extends SQLiteOpenHelper {
    
    private static final String dbName = "db";
    private static final String tableUser = "user";
    private static final String tableExercises = "exercises";
    private static final String tablePlans = "plans";
    private static final String tableDays = "days";
    private static final String tableRecords = "records";
    private static final String keyName = "name";
    private static final String keyDesc = "description";
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
    private static final String keyEx = "exercises";
    private static final String keyID = "id";
    private static final String keyExName = "exerciseName";
    private static final String keyPlName = "planName";
    private static final String keyEmail = "email";
    private static final String keyDob = "dob";
    private static final String keyGender = "gender";
    private static final String keyHeight = "height";
    private static final String keyWeight = "weight";
    private static final String keyGoal = "goal";
    private static final String keyUnits = "units";
    private SharedPreferences prefs;

    public SQLWrapper(Context context, SharedPreferences prefs) {
        super(context, dbName, null, 1);
        this.prefs = prefs;
    }

    public SQLWrapper(Context context) {
        super(context, dbName, null, 1);
    }

    /**
     * creates database tables and retrieves non-user created data from server
     *
     * @param db database connection
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
       String createUser= "CREATE TABLE " + tableUser + "(" +
                keyEmail + " varchar(50) NOT NULL PRIMARY KEY, " +
                keyName + " varchar(40) NOT NULL, " +
                keyDob + " date, " +
                keyGender + " bit, " +
                keyHeight + " float, " +
                keyWeight + " float, " +
                keyGoal + " bit, " +
                keyUnits + " bit)";

        String createExercises= "CREATE TABLE " + tableExercises + "(" +
            keyName + " varchar(20) NOT NULL UNIQUE PRIMARY KEY, " +
                keyDesc + " varchar(300) NOT NULL, " +
            keyType + " tinyint NOT NULL, " +
            keyImage + " varchar(50), " +
            keyMin + " int, " +
            keyMax + " int, " +
            keyAreas + " varchar(10), " +
            keyUser + " tinyint NOT NULL)";

        String createPlans = "CREATE TABLE " + tablePlans + "(" +
            keyName + " varchar(20) NOT NULL UNIQUE PRIMARY KEY, " +
                keyDesc + " varchar(300) NOT NULL, " +
            keyDays + " varchar(14) NOT NULL, " +
            keyUser + " tinyint NOT NULL)";

        String createDays = "CREATE TABLE " + tableDays + "(" +
                keyPlName + " varchar(20) NOT NULL, " +
            keyDayNum + " int NOT NULL, " +
                keyEx + " varchar(20) NOT NULL, " +
            keySets + " varchar(20) NOT NULL, PRIMARY KEY ( " +
                keyPlName + " , " + keyDayNum + " ), FOREIGN KEY (" +
                keyPlName + ") REFERENCES " + tablePlans + "(" + keyName + ") ON DELETE CASCADE)";

        String createRecords = "CREATE TABLE " + tableRecords + "(" +
            keyID + " int NOT NULL PRIMARY KEY, " +
            keyExName + " varchar(20) NOT NULL, " +
                keyPlName + " varchar(20), " +
            keyDayNum + " tinyint, " +
            keyTime + " DATETIME NOT NULL, " +
            keySets + " text, FOREIGN KEY (" +
            keyExName + ") REFERENCES " + tableExercises + "(" + keyID +
            ") ON DELETE CASCADE)";

        db.execSQL(createUser);
        db.execSQL(createExercises);
        db.execSQL(createPlans);
        db.execSQL(createDays);
        db.execSQL(createRecords);

        StringRequest strReq = new StringRequest(Request.Method.GET,
                wrappers.URLWrapper.getDefaultExercisesURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jArr = new JSONArray(response);
                    for (int i = 0; i < jArr.length(); i++) {
                        JSONObject jObj = jArr.getJSONObject(i);
                        jObj = jObj.getJSONObject("exercise");
                        Exercise exercise = new Exercise(jObj.getString(keyName), jObj.getString(keyDesc), jObj.getInt(keyType), jObj.getString(keyImage), jObj.getInt(keyMin), jObj.getInt(keyMax), jObj.getString(keyAreas), (jObj.getInt(keyUser) > 0));
                        storeExercise(exercise, false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                onCreate(getWritableDatabase());
            }
        });
        wrappers.VolleyWrapper.getInstance().addToRequestQueue(strReq);


        strReq = new StringRequest(Request.Method.GET,
                wrappers.URLWrapper.getDefaultPlansURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jArr = new JSONArray(response);
                    for (int i = 0; i < jArr.length(); i++) {
                        JSONObject jObj = jArr.getJSONObject(i);
                        jObj = jObj.getJSONObject("plan");
                        Plan plan = new Plan(jObj.getString(keyName), jObj.getString(keyDesc), jObj.getString(keyDays), (jObj.getInt("userMade") > 0));
                        storePlan(plan, false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                onCreate(getWritableDatabase());
            }
        });

        wrappers.VolleyWrapper.getInstance().addToRequestQueue(strReq);

        strReq = new StringRequest(Request.Method.GET,
                wrappers.URLWrapper.getDefaultDaysURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jArr = new JSONArray(response);
                    for (int i = 0; i < jArr.length(); i++) {
                        JSONObject jObj = jArr.getJSONObject(i);
                        jObj = jObj.getJSONObject("day");
                        Day day = new Day(jObj.getString("planName"), jObj.getInt("dayNumber"), jObj.getString("exercises"), jObj.getString("sets"));
                        storeDay(day, false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                onCreate(getWritableDatabase());
            }
        });
        wrappers.VolleyWrapper.getInstance().addToRequestQueue(strReq);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tableUser);
        db.execSQL("DROP TABLE IF EXISTS " + tableExercises);
        db.execSQL("DROP TABLE IF EXISTS " + tablePlans);
        db.execSQL("DROP TABLE IF EXISTS " + tableDays);
        db.execSQL("DROP TABLE IF EXISTS " + tableRecords);
        onCreate(db);
    }

    /**
     * Stores user details in database and downloads user created data from the server
     * Also handles merging of data created before login if any
     *
     * @param user User to be logged in
     */
    public void loginUser(final User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(keyEmail, user.getEmail());
        values.put(keyName, user.getName());
        values.put(keyDob, user.getDob());
        values.put(keyGender, user.getGender());
        values.put(keyHeight, user.getHeight());
        values.put(keyWeight, user.getWeight());
        values.put(keyGoal, user.getGoal());
        db.insert(tableUser, null, values);
        final ArrayList<ExerciseRecord> newRecords = getAllRecords();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URLWrapper.getUserDataURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                SQLiteDatabase db = getWritableDatabase();
                if (!newRecords.isEmpty()) {
                    db.execSQL("DELETE FROM " + tableRecords);
                }
                try {
                    JSONObject jResponse = new JSONObject(response);
                    JSONArray jArr = jResponse.getJSONArray("records");
                    for (int i = 0; i < jArr.length(); i++) {
                        JSONObject jObj = jArr.getJSONObject(i);
                        jObj = jObj.getJSONObject("record");
                        ExerciseRecord record = new ExerciseRecord(jObj.getInt(keyID), jObj.getString(keyExName), jObj.getString(keyPlName), jObj.getInt(keyDayNum), jObj.getString(keyTime), jObj.getString(keySets));
                        storeRecord(record, false);
                    }
                    if (!newRecords.isEmpty()) {
                        int newID = getLastRecordID() + 1;
                        for (ExerciseRecord record : newRecords) {
                            record.setId(newID);
                            storeRecord(record, true);
                            newID++;
                        }
                    }
                    jArr = jResponse.getJSONArray("exercises");
                    for (int i = 0; i < jArr.length(); i++) {
                        JSONObject jObj = jArr.getJSONObject(i);
                        jObj = jObj.getJSONObject("exercise");
                        Exercise exercise = new Exercise(jObj.getString(keyName), jObj.getString(keyDesc), (jObj.getInt(keyType)), jObj.getString(keyImage), jObj.getInt(keyMin), jObj.getInt(keyMax), jObj.getString(keyAreas), (jObj.getInt(keyUser) > 0));
                        storeExercise(exercise, false);
                    }
                    jArr = jResponse.getJSONArray("plans");
                    for (int i = 0; i < jArr.length(); i++) {
                        JSONObject jObj = jArr.getJSONObject(i);
                        jObj = jObj.getJSONObject("plan");
                        Plan plan = new Plan(jObj.getString(keyName), jObj.getString(keyDesc), jObj.getString(keyDays), (jObj.getInt("userMade") > 0));
                        storePlan(plan, false);
                    }
                    jArr = jResponse.getJSONArray("days");
                    for (int i = 0; i < jArr.length(); i++) {
                        JSONObject jObj = jArr.getJSONObject(i);
                        jObj = jObj.getJSONObject("day");
                        Day day = new Day(jObj.getString("planName"), jObj.getInt("dayNumber"), jObj.getString("exercises"), jObj.getString("sets"));
                        storeDay(day, false);
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
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", user.getEmail());
                return params;
            }
        };
        wrappers.VolleyWrapper.getInstance().addToRequestQueue((strReq));
    }

    public void syncData() {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                URLWrapper.syncBeginURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };
        wrappers.VolleyWrapper.getInstance().addToRequestQueue((strReq));
        /*todo*/
    }

    public User getUser() {
        if (prefs.getBoolean("loggedIn", true)) {
            String selectQuery = "SELECT  * FROM " + tableUser;
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            User user = new User(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getDouble(4), cursor.getDouble(5), cursor.getInt(6), cursor.getInt(7));
            cursor.close();
            db.close();
            return user;
        } else {
            return null;
        }
    }

    public void logoutUser() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + tableUser);
        db.execSQL("DELETE FROM " + tableRecords);
        db.execSQL("DELETE FROM " + tableDays + " WHERE " + keyPlName + " IN (SELECT name FROM " + tablePlans + " WHERE " + keyUser + " = 1)");
        db.execSQL("DELETE FROM " + tablePlans + " WHERE " + keyUser + " = 1");
        db.execSQL("DELETE FROM " + tableExercises + " WHERE " + keyUser + " = 1");
        db.close();
        prefs.edit().putBoolean("loggedIn", false).apply();
    }

    public void storeExercise(Exercise exercise, boolean newExercise) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(keyName, exercise.getName());
        values.put(keyDesc, exercise.getDescription());
        values.put(keyType, exercise.getType());
        values.put(keyImage, exercise.getImageURL());
        values.put(keyMin, exercise.getMinThreshold());
        values.put(keyMax, exercise.getMaxThreshold());
        values.put(keyAreas, exercise.getAreasWorked());
        values.put(keyUser, (exercise.isUserMade()) ? 1 : 0);
        db.insert(tableExercises, null, values);
        if (newExercise && getUser() != null) {
            storeExerciseRemote(exercise, getUser().getEmail());
        }
        db.close();
    }

    private void storeExerciseRemote(final Exercise exercise, final String email) {
        StringRequest request = new StringRequest(Request.Method.POST,
                URLWrapper.storeDataURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (!(error instanceof NoConnectionError)) {
                    storeExerciseRemote(exercise, email);
                }
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(keyEmail, getUser().getEmail());
                params.put(keyName, exercise.getName());
                params.put(keyDesc, exercise.getDescription());
                params.put(keyType, String.valueOf(exercise.getType()));
                params.put(keyImage, exercise.getImageURL());
                params.put(keyMin, String.valueOf(exercise.getMinThreshold()));
                params.put(keyMax, String.valueOf(exercise.getMaxThreshold()));
                params.put(keyAreas, exercise.getAreasWorked());
                params.put(keyUser, String.valueOf(1));
                return params;
            }
        };
        wrappers.VolleyWrapper.getInstance().addToRequestQueue(request);
    }

    public Exercise getExercise(String name) {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + tableExercises + " WHERE " + keyName + " = '" + name + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        Exercise exercise = new Exercise(cursor.getString(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3), cursor.getInt(4), cursor.getInt(5), cursor.getString(6), (cursor.getInt(7) != 0));
        cursor.close();
        db.close();
        return exercise;
    }

    public void deleteExercise(final String name, final String type) {
        SQLiteDatabase db = getWritableDatabase();
        String deleteQuery = "DELETE FROM " + tableExercises + " WHERE " + keyName + " = '" + name + "'";
        db.execSQL(deleteQuery);
        if (getUser() != null) {
            StringRequest request = new StringRequest(Request.Method.POST,
                    wrappers.URLWrapper.deleteURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }

            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    if (!(error instanceof NoConnectionError)) {
                        deleteExercise(name, type);
                    }
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put(keyEmail, getUser().getEmail());
                    params.put(keyName, name);
                    params.put(keyType, type);
                    return params;
                }

            };
            wrappers.VolleyWrapper.getInstance().addToRequestQueue(request);
        }
    }

    public ArrayList<String> getExerciseList(String area) {
        SQLiteDatabase db = getReadableDatabase();
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

    public void storePlan(Plan plan, boolean newPlan) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(keyName, plan.getName());
        values.put(keyDesc, plan.getDescription());
        values.put(keyDays, plan.getDays());
        values.put(keyUser, (plan.isUserMade()) ? 1 : 0);
        db.insert(tablePlans, null, values);
        db.close();
        if (plan.isUserMade() && getUser() != null && newPlan) {
            storePlanRemote(plan, getUser().getEmail());
        }
    }

    public void storePlanRemote(final Plan plan, final String email) {
        StringRequest request = new StringRequest(Request.Method.POST,
                URLWrapper.storeDataURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                storePlanRemote(plan, email);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(keyEmail, email);
                params.put(keyName, plan.getName());
                params.put(keyDesc, plan.getDescription());
                params.put(keyDayNum, plan.getDays());
                params.put(keyUser, "1");
                return params;
            }

        };
        wrappers.VolleyWrapper.getInstance().addToRequestQueue(request);
    }

    public Plan getPlan(String name) {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + tablePlans + " WHERE " + keyName + " = '" + name + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        Plan plan = new Plan(cursor.getString(0), cursor.getString(1), cursor.getString(2), (cursor.getInt(3) != 0));
        cursor.close();
        db.close();
        return plan;
    }

    public ArrayList<Plan> getPlans() {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + tablePlans;
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        ArrayList<Plan> plans = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            plans.add(new Plan(cursor.getString(0), cursor.getString(1), cursor.getString(2), (cursor.getInt(3) != 0)));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return plans;
    }

    public void storeDay(Day day, boolean newDay) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(keyPlName, day.getPlanName());
        values.put(keyDayNum, day.getDayNumber());
        values.put(keyEx, day.getExercises());
        values.put(keySets, day.getSets());
        db.insert(tableDays, null, values);
        db.close();
        if (getUser() != null && newDay) {
            storeDayRemote(day, getUser().getEmail());
        }
    }

    private void storeDayRemote(final Day day, final String email) {
        StringRequest request = new StringRequest(Request.Method.POST,
                URLWrapper.storeDataURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                storeDayRemote(day, email);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(keyEmail, email);
                params.put(keyPlName, day.getPlanName());
                params.put(keyDayNum, day.getPlanName());
                params.put(keyEx, day.getExercises());
                params.put(keySets, day.getSets());
                return params;
            }
        };
        wrappers.VolleyWrapper.getInstance().addToRequestQueue(request);
    }

    public Day getDay(String planName, int dayNum) {
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT * FROM " + tableDays + " WHERE " + keyPlName + " = '" + new QueryValidator(planName).validateQuery() + "' AND " + keyDayNum + " = " + dayNum;
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        Day day = new Day(cursor.getString(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3));
        cursor.close();
        db.close();
        return day;
    }

    public void storeRecord(ExerciseRecord record, boolean newRecord) {
        SQLiteDatabase db = getWritableDatabase();
        String selectQuery = ("SELECT * FROM " + tableRecords);
        Cursor cursor = db.rawQuery(selectQuery, null);
        int id = 0;
        if (cursor.getCount() != 0) {
            cursor.moveToLast();
            id = cursor.getInt(0) + 1;
        }
        cursor.close();
        ContentValues values = new ContentValues();
        values.put(keyID, id);
        values.put(keyExName, record.getExerciseName());
        values.put(keyPlName, record.getPlanName());
        values.put(keyDayNum, record.getDayNum());
        values.put(keyTime, record.getTime());
        values.put(keySets, record.getSets());
        db.insert(tableRecords, null, values);
        db.close();
        if (getUser() != null) {
            if (newRecord) {
                record.setId(id);
                storeRecordRemote(record, getUser().getEmail());
            }
        }
    }

    private void storeRecordRemote(final ExerciseRecord record, final String email) {
        StringRequest request = new StringRequest(Request.Method.POST,
                URLWrapper.storeDataURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (!(error instanceof NoConnectionError)) {
                    storeRecordRemote(record, email);
                }
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(keyID, String.valueOf(record.getId()));
                params.put(keyExName, record.getExerciseName());
                params.put(keyPlName, record.getPlanName());
                params.put(keyDayNum, String.valueOf(record.getDayNum()));
                params.put(keyTime, record.getTime());
                params.put(keySets, record.getSets());
                params.put(keyEmail, email);
                return params;
            }

        };
        wrappers.VolleyWrapper.getInstance().addToRequestQueue(request);
    }

    public int getLastRecordID() {
        SQLiteDatabase db = getWritableDatabase();
        String selectQuery = ("SELECT * FROM " + tableRecords);
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToLast();
        int id = new ExerciseRecord(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getString(4), cursor.getString(5)).getId();
        cursor.close();
        db.close();
        return id;
    }

    public ExerciseRecord getLastRecord(String name) {
        SQLiteDatabase db = getWritableDatabase();
        String selectQuery = ("SELECT * FROM " + tableRecords + " WHERE " + keyExName + " = '" + name + "'");
        Cursor cursor = db.rawQuery(selectQuery, null);
        ExerciseRecord record = null;
        if (cursor.getCount() != 0) {
            cursor.moveToLast();
            record = new ExerciseRecord(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getString(4), cursor.getString(5));
        }
        cursor.close();
        db.close();
        return record;
    }

    public ArrayList<ExerciseRecord> getLastRecords() {
        SQLiteDatabase db = getWritableDatabase();
        String selectQuery = ("SELECT * FROM " + tableRecords);
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<ExerciseRecord> records = new ArrayList<>();
        if (cursor.getCount() != 0) {
            for (int i = 0; i < 5; i++) {
                records.add(new ExerciseRecord(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getString(4), cursor.getString(5)));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return records;
    }

    public ArrayList<ExerciseRecord> getAllRecords() {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = ("SELECT * FROM " + tableRecords);
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<ExerciseRecord> records = new ArrayList<>();
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                records.add(new ExerciseRecord(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getString(4), cursor.getString(5)));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return records;
    }

    public ExerciseRecord getLastPlanRecord(String exName, String plName) {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = ("SELECT * FROM " + tableRecords + " WHERE " + keyExName + " = '" + exName + "' AND " + keyPlName + " = '" + plName + "'");
        Cursor cursor = db.rawQuery(selectQuery, null);
        ExerciseRecord record = null;
        if (cursor.getCount() != 0) {
            cursor.moveToLast();
            record = new ExerciseRecord(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getString(4), cursor.getString(5));
        }
        return record;
    }

    public ArrayList<ExerciseRecord> getRecords(String name) {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = ("SELECT * FROM " + tableRecords + " WHERE " + keyExName + " = '" + name + "'");
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<ExerciseRecord> records = new ArrayList<>();
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                records.add(new ExerciseRecord(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getString(4), cursor.getString(5)));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return records;
    }
}
