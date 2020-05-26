package wrappers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
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
    private static final String tableExercisesSyncNew = "exercisesSync";
    private static final String tableExercisesSyncDelete = "exercisesDelete";
    private static final String tablePlans = "plans";
    private static final String tableDays = "days";
    private static final String tableRecords = "records";
    private static final String tableRecordsSyncNew = "recordsSync";
    private static final String tableRecordsSyncDelete = "recordsDelete";
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
    private static final String keyReps = "reps";
    private static final String keyDayNum = "dayNumber";
    private static final String keyEx = "exercises";
    private static final String keyID = "id";
    private static final String keyExName = "exerciseName";
    private static final String keyPlName = "planName";
    private static final String keyEmail = "email";
    private static final String keyPass = "password";
    private static final String keyDob = "dob";
    private static final String keyGender = "gender";
    private static final String keyHeight = "height";
    private static final String keyWeight = "weight";
    private static final String keyGoal = "goal";
    private SharedPreferences prefs;
    private Context context;

    public SQLWrapper(Context context) {
        super(context, dbName, null, 1);
        this.prefs = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        this.context = context;
    }

    /**
     * creates database tables and retrieves non-user created data from server
     *
     * @param db database connection
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //String to create user table
        String createUser = "CREATE TABLE " + tableUser + "(" +
                keyEmail + " varchar(50) NOT NULL PRIMARY KEY, " +
                keyPass + " varchar(16) NOT NULL, " +
                keyName + " varchar(40) NOT NULL, " +
                keyDob + " date, " +
                keyGender + " bit, " +
                keyHeight + " float, " +
                keyWeight + " float, " +
                keyGoal + " bit)";

        //String to create exercises table
        String createExercises = "CREATE TABLE " + tableExercises + "(" +
                keyName + " varchar(30) NOT NULL PRIMARY KEY, " +
                keyDesc + " varchar(300) NOT NULL, " +
                keyType + " tinyint NOT NULL, " +
                keyImage + " varchar(50), " +
                keyMin + " int, " +
                keyMax + " int, " +
                keyAreas + " varchar(50), " +
                keyUser + " tinyint NOT NULL)";

        //String to create plans table
        String createPlans = "CREATE TABLE " + tablePlans + "(" +
                keyName + " varchar(20) NOT NULL PRIMARY KEY, " +
                keyDesc + " varchar(300) NOT NULL, " +
                keyDays + " varchar(30) NOT NULL, " +
                keyUser + " tinyint NOT NULL)";

        //String to create days table
        String createDays = "CREATE TABLE " + tableDays + "(" +
                keyPlName + " varchar(30) NOT NULL, " +
                keyDayNum + " int NOT NULL, " +
                keyEx + " varchar(300) NOT NULL, " +
                keySets + " varchar(100) NOT NULL," +
                keyReps + " varchar(200) NOT NULL, PRIMARY KEY ( " +
                keyPlName + " , " + keyDayNum + " ), FOREIGN KEY (" +
                keyPlName + ") REFERENCES " + tablePlans + "(" + keyName + ") ON DELETE CASCADE)";

        // String to create records table
        String createRecords = "CREATE TABLE " + tableRecords + "(" +
                keyID + " int NOT NULL, " +
                keyExName + " varchar(20) NOT NULL, " +
                keyPlName + " varchar(20), " +
                keyDayNum + " tinyint, " +
                keyTime + " DATETIME NOT NULL, " +
                keySets + " text, " +
                "PRIMARY KEY (" + keyID + ", " + keyTime + "), FOREIGN KEY (" +
                keyExName + ") REFERENCES " + tableExercises + "(" + keyID +
                ") ON DELETE CASCADE)";

        // String to create tables to manage syncing
        String createUnsyncedExercises = "CREATE TABLE " + tableExercisesSyncNew + " AS SELECT * FROM " + tableExercises;
        String createUnsyncedRecords = "CREATE TABLE " + tableRecordsSyncNew + " AS SELECT * FROM " + tableRecords;
        String createDeletedExercises = "CREATE TABLE " + tableExercisesSyncDelete + "( " + keyName + " int NOT NULL)";
        String createDeletedRecords = "CREATE TABLE " + tableRecordsSyncDelete + " (" + keyID + " int NOT NULL)";

        //Executes all SQL Strings
        db.execSQL(createUser);
        db.execSQL(createExercises);
        db.execSQL(createPlans);
        db.execSQL(createDays);
        db.execSQL(createRecords);
        db.execSQL(createUnsyncedExercises);
        db.execSQL(createUnsyncedRecords);
        db.execSQL(createDeletedExercises);
        db.execSQL(createDeletedRecords);

        // API request to get default data from server
        StringRequest request = new StringRequest(Request.Method.GET,
                URLWrapper.getDefaultDataURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    JSONArray jArr = jObj.getJSONArray("exercises");
                    for (int i = 0; i < jArr.length(); i++) {
                        JSONObject jData = jArr.getJSONObject(i);
                        Exercise exercise = loadJSONExercise(jData);
                        storeExercise(exercise, false);
                    }

                    jArr = jObj.getJSONArray("plans");
                    for (int i = 0; i < jArr.length(); i++) {
                        JSONObject jData = jArr.getJSONObject(i);
                        Plan plan = loadJSONPlan(jData);
                        storePlan(plan);
                    }
                    jArr = jObj.getJSONArray("days");
                    for (int i = 0; i < jArr.length(); i++) {
                        JSONObject jData = jArr.getJSONObject(i);
                        Day day = loadJSONDay(jData);
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
        });
        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        wrappers.VolleyWrapper.getInstance().addToRequestQueue(request, "sync");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tableUser);
        db.execSQL("DROP TABLE IF EXISTS " + tableExercises);
        db.execSQL("DROP TABLE IF EXISTS " + tableExercisesSyncNew);
        db.execSQL("DROP TABLE IF EXISTS " + tableExercisesSyncDelete);
        db.execSQL("DROP TABLE IF EXISTS " + tablePlans);
        db.execSQL("DROP TABLE IF EXISTS " + tableDays);
        db.execSQL("DROP TABLE IF EXISTS " + tableRecords);
        db.execSQL("DROP TABLE IF EXISTS " + tableRecordsSyncNew);
        db.execSQL("DROP TABLE IF EXISTS " + tableRecordsSyncDelete);
        onCreate(db);
    }

    /**
     * Stores user details in database and downloads user created data from the server
     * Also handles merging of data created before login if any
     *
     * @param user User to be logged in
     */
    @SuppressLint("ApplySharedPref")
    public void loginUser(final User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(keyEmail, user.getEmail());
        values.put(keyPass, user.getPassword());
        values.put(keyName, user.getName());
        values.put(keyDob, user.getDob());
        values.put(keyGender, user.getGender());
        values.put(keyHeight, user.getHeight());
        values.put(keyWeight, user.getWeight());
        values.put(keyGoal, user.getGoal());
        db.insert(tableUser, null, values);
        prefs.edit().putBoolean("loggedIn", true).commit();
        final ArrayList<ExerciseRecord> newRecords = getAllRecords();
        StringRequest request = new StringRequest(Request.Method.POST,
                URLWrapper.getUserDataURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                SQLiteDatabase db = getWritableDatabase();
                if (!newRecords.isEmpty()) {
                    db.execSQL("DELETE FROM " + tableRecords);
                }
                try {
                    JSONObject jResponse = new JSONObject(response);
                    if (!jResponse.getString("exercises").equals("null")) {
                        JSONArray jArr = jResponse.getJSONArray("exercises");
                        for (int i = 0; i < jArr.length(); i++) {
                            JSONObject jObj = jArr.getJSONObject(i);
                            Exercise exercise = loadJSONExercise(jObj);
                            storeExercise(exercise, false);
                        }
                    }

                    if (!jResponse.getString("plans").equals("null")) {
                        JSONArray jArr = jResponse.getJSONArray("plans");
                        for (int i = 0; i < jArr.length(); i++) {
                            JSONObject jObj = jArr.getJSONObject(i);
                            Plan plan = loadJSONPlan(jObj);
                            storePlan(plan);
                        }
                        jArr = jResponse.getJSONArray("days");
                        for (int i = 0; i < jArr.length(); i++) {
                            JSONObject jObj = jArr.getJSONObject(i);
                            Day day = loadJSONDay(jObj);
                            storeDay(day, false);
                        }
                    }

                    if (!jResponse.getString("records").equals("null")) {
                        JSONArray jArr = jResponse.getJSONArray("records");
                        for (int i = 0; i < jArr.length(); i++) {
                            JSONObject jObj = jArr.getJSONObject(i);
                            ExerciseRecord record = loadJSONRecord(jObj);
                            storeRecord(record, false);
                        }
                    }

                    if (!newRecords.isEmpty()) {
                        int newID = getLastRecordID() + 1;
                        for (ExerciseRecord record : newRecords) {
                            record.setId(newID);
                            storeRecord(record, true);
                            newID++;
                        }
                    }
                    if (getLastPlan() != null) {
                        prefs.edit().putString("currentPlan", getLastPlan().getPlanName()).apply();
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
                params.put("password", user.getPassword());
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyWrapper.getInstance().addToRequestQueue(request, "login");
    }

    /**
     * Checks for a internet connection on the device
     *
     * @return state of interenet connection
     */
    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
    }

    /**
     * Stores any local data not saved on the server and checks the for differences
     * between local and remote databases
     */
    @SuppressLint("ApplySharedPref")
    public void syncData() {
        if (prefs.getBoolean("syncing", false)) {
            VolleyWrapper.getInstance().getRequestQueue().cancelAll("syncComplete");
            VolleyWrapper.getInstance().getRequestQueue().cancelAll("syncBegin");
            prefs.edit().putBoolean("syncing", false).commit();
        }
        if (isConnected() && getUser() != null) {
            prefs.edit().putBoolean("syncing", true).commit();
            final SQLiteDatabase db = getWritableDatabase();

            // Exercises insertions
            String selectQuery = ("SELECT * FROM " + tableExercisesSyncNew);
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                ArrayList<Exercise> exercises = new ArrayList<>();
                while (!cursor.isAfterLast()) {
                    exercises.add(loadExercise(cursor));
                    cursor.moveToNext();
                }
                cursor.close();
                db.execSQL("DELETE FROM " + tableExercisesSyncNew);
                for (Exercise exercise : exercises) {
                    storeExerciseRemote(exercise);
                }
            }

            // Exercises deletions
            selectQuery = ("SELECT * FROM " + tableExercisesSyncDelete);
            cursor = db.rawQuery(selectQuery, null);
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                ArrayList<String> exercises = new ArrayList<>();
                while (!cursor.isAfterLast()) {
                    exercises.add(cursor.getString(0));
                    cursor.moveToNext();
                }
                cursor.close();
                db.execSQL("DELETE FROM " + tableExercisesSyncDelete);
                for (String exercise : exercises) {
                    deleteExerciseRemote(exercise);
                }
            }

            // Records insertions
            selectQuery = ("SELECT * FROM " + tableRecordsSyncNew);
            cursor = db.rawQuery(selectQuery, null);
            ArrayList<ExerciseRecord> records = new ArrayList<>();
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    records.add(loadRecord(cursor));
                    cursor.moveToNext();
                }
                cursor.close();
                db.execSQL("DELETE FROM " + tableRecordsSyncNew);
                for (ExerciseRecord record : records) {
                    storeRecordRemote(record);
                }
            }

            // Records deletions
            selectQuery = ("SELECT * FROM " + tableRecordsSyncDelete);
            cursor = db.rawQuery(selectQuery, null);
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                ArrayList<Integer> recordIDs = new ArrayList<>();
                ArrayList<String> recordTimes = new ArrayList<>();
                while (!cursor.isAfterLast()) {
                    recordIDs.add(cursor.getInt(0));
                    recordTimes.add(cursor.getString(1));
                    cursor.moveToNext();
                }
                cursor.close();
                db.execSQL("DELETE FROM " + tableRecordsSyncDelete);
                int i = 0;
                for (Integer recordID : recordIDs) {
                    deleteRecordRemote(recordID, recordTimes.get(i));
                }
            }
            Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    StringRequest request = new StringRequest(Request.Method.POST, URLWrapper.syncBeginURL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jObj = new JSONObject(response);
                                boolean error = jObj.getBoolean("error");
                                // Checks for error message
                                if (error) {
                                    // logs out user as details no longer present
                                    logoutUser();
                                    Toast.makeText(context, "Account details have been changed please log back in", Toast.LENGTH_LONG).show();
                                    prefs.edit().putBoolean("syncing", false).commit();
                                } else {
                                    // gets row differences between local and remote db
                                    final int newExercises = jObj.getInt("exercises") - getExerciseCount();
                                    final int newPlans = jObj.getInt("plans") - getPlanCount();
                                    final int newRecords = jObj.getInt("records") - getRecordCount();
                                    // sends second sync request with difference in rows
                                    if (newExercises != 0 || newPlans != 0 || newRecords != 0) {
                                        StringRequest syncRequest = new StringRequest(Request.Method.POST, URLWrapper.syncCompleteURL, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONObject jObj = new JSONObject(response);
                                                    boolean error = jObj.getBoolean("error");
                                                    if (!error) {
                                                        SQLiteDatabase db = getWritableDatabase();
                                                        if (!jObj.getString("exercises").equals("null")) {
                                                            JSONArray jArr = jObj.getJSONArray("exercises");
                                                            // inserts exercse response
                                                            if (newExercises > 0) {
                                                                for (int i = 0; i < jArr.length(); i++) {
                                                                    JSONObject jEx = jArr.getJSONObject(i);
                                                                    Exercise exercise = loadJSONExercise(jEx);
                                                                    storeExercise(exercise, false);
                                                                }
                                                            } else {
                                                                // creates a table to contain the exercise names then deletes any exercises that aren't in the table
                                                                db.execSQL("CREATE TABLE temp(" + keyName + " varchar(50))");
                                                                ContentValues values = new ContentValues();
                                                                for (int i = 0; i < jArr.length(); i++) {
                                                                    String name = jArr.getString(i);
                                                                    values.put("name", name);
                                                                    db.insert("temp", null, values);
                                                                }
                                                                db.execSQL("DELETE FROM " + tableExercises + " WHERE " + keyUser + " = 1 AND " + keyName + " NOT IN (SELECT * FROM temp)");
                                                                db.execSQL("DROP TABLE IF EXISTS temp");
                                                            }
                                                        }

                                                        if (!jObj.getString("plans").equals("null")) {
                                                            JSONArray jArr = jObj.getJSONArray("plans");
                                                            if (newPlans > 0) {
                                                                for (int i = 0; i < jArr.length(); i++) {
                                                                    JSONObject jPl = jArr.getJSONObject(i);
                                                                    Plan plan = loadJSONPlan(jPl);
                                                                    storePlan(plan);
                                                                }
                                                            } else {
                                                                db.execSQL("CREATE TABLE temp(" + keyName + " varchar(50))");
                                                                ContentValues values = new ContentValues();
                                                                for (int i = 0; i < jArr.length(); i++) {
                                                                    String name = jArr.getString(i);
                                                                    values.put("name", name);
                                                                    db.insert("temp", null, values);
                                                                }
                                                                db.execSQL("DELETE FROM " + tablePlans + " WHERE " + keyUser + " = 1 AND " + keyName + " NOT IN (SELECT * FROM temp)");
                                                                db.execSQL("DROP TABLE IF EXISTS temp");
                                                            }
                                                        }

                                                        if (!jObj.getString("days").equals("null")) {
                                                            JSONArray jArr = jObj.getJSONArray("days");
                                                            for (int i = 0; i < jArr.length(); i++) {
                                                                JSONObject jDy = jArr.getJSONObject(i);
                                                                Day day = loadJSONDay(jDy);
                                                                storeDay(day, false);
                                                            }
                                                        }

                                                        // inserts new records
                                                        if (!jObj.getString("records").equals("null")) {
                                                            JSONArray jArr = jObj.getJSONArray("records");
                                                            for (int i = 0; i < jArr.length(); i++) {
                                                                JSONObject jRec = jArr.getJSONObject(i);
                                                                ExerciseRecord record = loadJSONRecord(jRec);
                                                                storeRecord(record, false);
                                                            }
                                                        }

                                                        // deletes old records using a table of record ids found on the server
                                                        if (!jObj.getString("recordIDs").equals("null")) {
                                                            JSONArray jArr = jObj.getJSONArray("recordIDs");
                                                            db.execSQL("CREATE TABLE temp(" + keyID + " int, " + keyTime + " datetime)");
                                                            ContentValues values = new ContentValues();
                                                            for (int i = 0; i < jArr.length(); i++) {
                                                                JSONObject jRec = jArr.getJSONObject(i);
                                                                values.put(keyID, jRec.getInt(keyID));
                                                                values.put(keyTime, jRec.getString(keyTime));
                                                                db.insert("temp", null, values);
                                                            }
                                                            String deleteQuery = "DELETE FROM " + tableRecords + " WHERE " + keyID + " NOT IN " +
                                                                    "(SELECT " + keyID + " FROM temp WHERE " + tableRecords + "." + keyID + " = temp." + keyID + " AND " +
                                                                    tableRecords + "." + keyTime + " = temp." + keyTime + ") AND " + keyTime + " NOT IN (SELECT " + keyTime + " FROM temp WHERE " + tableRecords + "." + keyID + " = temp." + keyID + " AND " +
                                                                    tableRecords + "." + keyTime + " = temp." + keyTime + ")";
                                                            db.execSQL(deleteQuery);
                                                            db.execSQL("DROP TABLE IF EXISTS temp");
                                                        }
                                                        prefs.edit().putBoolean("syncing", false).commit();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    prefs.edit().putBoolean("syncing", false).commit();
                                                }

                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                error.printStackTrace();
                                                prefs.edit().putBoolean("syncing", false).commit();
                                            }
                                        }) {
                                            @Override
                                            protected Map<String, String> getParams() {
                                                Map<String, String> params = new HashMap<>();
                                                if (newExercises != 0) {
                                                    params.put("exercises", String.valueOf(newExercises));
                                                }
                                                if (newPlans != 0) {
                                                    params.put("plans", String.valueOf(newPlans));
                                                }
                                                if (newRecords < 0) {
                                                    params.put("records", String.valueOf(newRecords));
                                                } else if (newRecords > 0) {
                                                    params.put("lastid", String.valueOf(getLastRecordID()));
                                                }
                                                params.put("email", getUser().getEmail());
                                                params.put("password", getUser().getPassword());
                                                return params;
                                            }
                                        };
                                        syncRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                        VolleyWrapper.getInstance().addToRequestQueue(syncRequest, "syncComplete");
                                    } else {
                                        prefs.edit().putBoolean("syncing", false).commit();
                                    }
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
                            params.put("email", getUser().getEmail());
                            params.put("password", getUser().getPassword());
                            return params;
                        }
                    };
                    request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    VolleyWrapper.getInstance().addToRequestQueue(request, "syncBegin");
                }
            };
            handler.postDelayed(runnable, 2000);
        }
    }

    public void updateUser(final User user, final String oldEmail) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + tableUser);
        ContentValues values = new ContentValues();
        values.put(keyEmail, user.getEmail());
        values.put(keyPass, user.getPassword());
        values.put(keyName, user.getName());
        values.put(keyDob, user.getDob());
        values.put(keyGender, user.getGender());
        values.put(keyHeight, user.getHeight());
        values.put(keyWeight, user.getWeight());
        values.put(keyGoal, user.getGoal());
        db.insert(tableUser, null, values);
        StringRequest request = new StringRequest(Request.Method.POST,
                URLWrapper.updateUserURL, new Response.Listener<String>() {
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
                params.put("email", oldEmail);
                params.put("password", user.getPassword());
                params.put("newEmail", user.getEmail());
                params.put("name", user.getName());
                params.put("goal", String.valueOf(user.getGoal()));
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyWrapper.getInstance().addToRequestQueue(request, "update");

    }

    public User getUser() {
        String selectQuery = "SELECT  * FROM " + tableUser;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            User user = new User(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4), cursor.getDouble(5), cursor.getDouble(6), cursor.getInt(7));
            cursor.close();
            db.close();
            return user;
        } else {
            cursor.close();
            db.close();
            return null;
        }
    }

    @SuppressLint("ApplySharedPref")
    public void logoutUser() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + tableUser);
        db.execSQL("DELETE FROM " + tableRecords);
        db.execSQL("DELETE FROM " + tableDays + " WHERE " + keyPlName + " IN (SELECT name FROM " + tablePlans + " WHERE " + keyUser + " = 1)");
        db.execSQL("DELETE FROM " + tablePlans + " WHERE " + keyUser + " = 1");
        db.execSQL("DELETE FROM " + tableExercises + " WHERE " + keyUser + " = 1");
        db.close();
        prefs.edit().putBoolean("loggedIn", false).commit();
        prefs.edit().putString("currentPlan", "").apply();
    }

    public void deleteUser(final User user) {
        StringRequest request = new StringRequest(Request.Method.POST,
                URLWrapper.deleteUserURL, new Response.Listener<String>() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onResponse(String response) {
                if (response.equals("error")) {
                    Toast.makeText(context, "Unable to delete account please try again.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show();
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
                params.put("password", user.getPassword());
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyWrapper.getInstance().addToRequestQueue(request, "delete");
    }

    public void resetData() {
        User user = getUser();
        if (user != null) {
            logoutUser();
        }
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + tableUser);
        db.execSQL("DROP TABLE IF EXISTS " + tableExercises);
        db.execSQL("DROP TABLE IF EXISTS " + tableExercisesSyncNew);
        db.execSQL("DROP TABLE IF EXISTS " + tableExercisesSyncDelete);
        db.execSQL("DROP TABLE IF EXISTS " + tablePlans);
        db.execSQL("DROP TABLE IF EXISTS " + tableDays);
        db.execSQL("DROP TABLE IF EXISTS " + tableRecords);
        db.execSQL("DROP TABLE IF EXISTS " + tableRecordsSyncNew);
        db.execSQL("DROP TABLE IF EXISTS " + tableRecordsSyncDelete);
        onCreate(db);
        if (user != null) {
            loginUser(user);
        }
        prefs.edit().putString("currentPlan", "").apply();
        Toast.makeText(context, "Data Reset", Toast.LENGTH_SHORT).show();
    }

    public void resetUser() {
        final User user = getUser();
        logoutUser();
        StringRequest request = new StringRequest(Request.Method.POST,
                URLWrapper.resetUserURL, new Response.Listener<String>() {
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
                params.put("email", user.getEmail());
                params.put("password", user.getPassword());
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyWrapper.getInstance().addToRequestQueue(request, "reset");
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + tableUser);
        db.execSQL("DROP TABLE IF EXISTS " + tableExercises);
        db.execSQL("DROP TABLE IF EXISTS " + tableExercisesSyncNew);
        db.execSQL("DROP TABLE IF EXISTS " + tableExercisesSyncDelete);
        db.execSQL("DROP TABLE IF EXISTS " + tablePlans);
        db.execSQL("DROP TABLE IF EXISTS " + tableDays);
        db.execSQL("DROP TABLE IF EXISTS " + tableRecords);
        db.execSQL("DROP TABLE IF EXISTS " + tableRecordsSyncNew);
        db.execSQL("DROP TABLE IF EXISTS " + tableRecordsSyncDelete);
        onCreate(db);
        loginUser(user);
        Toast.makeText(context, "Account Reset", Toast.LENGTH_SHORT).show();
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
        db.insertWithOnConflict(tableExercises, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        if (newExercise && getUser() != null) {
            storeExerciseRemote(exercise);
        }
        db.close();
    }

    private void storeExerciseRemote(final Exercise exercise) {
        StringRequest request = new StringRequest(Request.Method.POST,
                URLWrapper.storeDataURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                SQLiteDatabase db = getWritableDatabase();
                String selectQuery = "SELECT * FROM " + tableExercisesSyncNew + " WHERE " + keyName + " = " + exercise.getName();
                Cursor cursor = db.rawQuery(selectQuery, null);
                if (cursor.getCount() == 0) {
                    ContentValues values = new ContentValues();
                    values.put(keyName, exercise.getName());
                    values.put(keyDesc, exercise.getDescription());
                    values.put(keyType, exercise.getType());
                    values.put(keyImage, exercise.getImageURL());
                    values.put(keyMin, exercise.getMinThreshold());
                    values.put(keyMax, exercise.getMaxThreshold());
                    values.put(keyAreas, exercise.getAreasWorked());
                    values.put(keyUser, (exercise.isUserMade()) ? 1 : 0);
                    db.insert(tableExercisesSyncNew, null, values);
                }
                cursor.close();
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
        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        wrappers.VolleyWrapper.getInstance().addToRequestQueue(request, "storeExercise");
    }

    public Exercise getExercise(String name) {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + tableExercises + " WHERE " + keyName + " = '" + name + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        Exercise exercise = loadExercise(cursor);
        cursor.close();
        db.close();
        return exercise;
    }

    public void deleteExercise(final String name) {
        SQLiteDatabase db = getWritableDatabase();
        String deleteQuery = "DELETE FROM " + tableExercises + " WHERE " + keyName + " = '" + name + "'";
        db.execSQL(deleteQuery);
        if (getUser() != null) {
            deleteExerciseRemote(name);
        }
    }

    private void deleteExerciseRemote(final String name) {
        StringRequest request = new StringRequest(Request.Method.POST,
                wrappers.URLWrapper.deleteURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                SQLiteDatabase db = getWritableDatabase();
                String selectQuery = "SELECT * FROM " + tableExercisesSyncDelete + " WHERE " + keyName + " = " + name;
                Cursor cursor = db.rawQuery(selectQuery, null);
                if (cursor.getCount() == 0) {
                    ContentValues values = new ContentValues();
                    values.put(keyExName, name);
                    db.insert(tableExercisesSyncDelete, null, values);
                }
                cursor.close();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(keyEmail, getUser().getEmail());
                params.put(keyExName, name);
                return params;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        wrappers.VolleyWrapper.getInstance().addToRequestQueue(request, "deleteExercise");
    }

    private int getExerciseCount() {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + tableExercises;
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
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

    public ArrayList<String> getAllRecordedExerciseAreasList(String area) {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT name FROM " + tableExercises + " WHERE " + keyName + " IN (SELECT " + keyExName + " FROM " + tableRecords + ") AND " + keyAreas + " LIKE '%" + area + "%'";
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

    private void storePlan(Plan plan) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(keyName, plan.getName());
        values.put(keyDesc, plan.getDescription());
        values.put(keyDays, plan.getDays());
        values.put(keyUser, (plan.isUserMade()) ? 1 : 0);
        db.insertWithOnConflict(tablePlans, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    private int getPlanCount() {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + tablePlans;
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public Plan getPlan(String name) {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + tablePlans + " WHERE " + keyName + " = '" + new QueryValidator(name).validateQuery() + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        Plan plan = loadPlan(cursor);
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
            plans.add(loadPlan(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return plans;
    }

    public ArrayList<String> getPlanNames() {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + tablePlans + " WHERE " + keyName + " IN (SELECT " + keyPlName + " FROM " + tableRecords + ")";
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        ArrayList<String> plans = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            plans.add(loadPlan(cursor).getName());
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return plans;
    }

    private void storeDay(Day day, boolean newDay) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(keyPlName, day.getPlanName());
        values.put(keyDayNum, day.getDayNumber());
        values.put(keyEx, day.getExercisesJSON());
        values.put(keySets, day.getSets());
        values.put(keyReps, day.getReps());
        db.insertWithOnConflict(tableDays, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public Day getDay(String planName, int dayNum) {
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT * FROM " + tableDays + " WHERE " + keyPlName + " = '" + new QueryValidator(planName).validateQuery() + "' AND " + keyDayNum + " = " + dayNum;
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        Day day = loadDay(cursor);
        cursor.close();
        db.close();
        return day;
    }

    public void storeRecord(ExerciseRecord record, boolean newRecord) {
        SQLiteDatabase db = getWritableDatabase();
        String selectQuery = ("SELECT * FROM " + tableRecords);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (record.getId() == 0) {
            if (cursor.getCount() != 0) {
                cursor.moveToLast();
                record.setId(cursor.getInt(0) + 1);
            } else {
                record.setId(1);
            }
        }
        cursor.close();
        ContentValues values = new ContentValues();
        values.put(keyID, record.getId());
        values.put(keyExName, record.getExerciseName());
        values.put(keyPlName, record.getPlanName());
        values.put(keyDayNum, record.getDayNum());
        values.put(keyTime, record.getTime());
        values.put(keySets, record.getSets());

        db.insertWithOnConflict(tableRecords, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        if (!record.getPlanName().equals(" "))
            prefs.edit().putString("currentPlan", record.getPlanName()).apply();
        db.close();
        if (getUser() != null && newRecord) {
            storeRecordRemote(record);

        }
    }

    private void storeRecordRemote(final ExerciseRecord record) {
        StringRequest request = new StringRequest(Request.Method.POST,
                URLWrapper.storeDataURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                SQLiteDatabase db = getWritableDatabase();
                String selectQuery = "SELECT * FROM " + tableRecordsSyncNew + " WHERE " + keyID + " = " + record.getId();
                Cursor cursor = db.rawQuery(selectQuery, null);
                if (cursor.getCount() == 0) {
                    ContentValues values = new ContentValues();
                    values.put(keyID, record.getId());
                    values.put(keyExName, record.getExerciseName());
                    values.put(keyPlName, record.getPlanName());
                    values.put(keyDayNum, record.getDayNum());
                    values.put(keyTime, record.getTime());
                    values.put(keySets, record.getSets());
                    db.insert(tableRecordsSyncNew, null, values);
                }
                cursor.close();
                db.close();
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
                params.put(keyEmail, getUser().getEmail());
                return params;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        wrappers.VolleyWrapper.getInstance().addToRequestQueue(request, "storeRecord");
    }

    public void deleteRecord(final int recordID, final String time) {
        SQLiteDatabase db = getWritableDatabase();
        String deleteQuery = "DELETE FROM " + tableRecords + " WHERE " + keyID + " = " + recordID;
        db.execSQL(deleteQuery);
        if (getUser() != null) {
            deleteRecordRemote(recordID, time);
        }
    }

    private void deleteRecordRemote(final int recordID, final String time) {

        StringRequest request = new StringRequest(Request.Method.POST,
                wrappers.URLWrapper.deleteURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                SQLiteDatabase db = getWritableDatabase();
                String selectQuery = "SELECT * FROM " + tableRecordsSyncDelete + " WHERE " + keyName + " = " + recordID + " AND " + keyTime + " = " + time;
                Cursor cursor = db.rawQuery(selectQuery, null);
                if (cursor.getCount() == 0) {
                    ContentValues values = new ContentValues();
                    values.put(keyID, recordID);
                    db.insert(tableExercisesSyncNew, null, values);
                }
                cursor.close();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(keyEmail, getUser().getEmail());
                params.put(keyID, String.valueOf(recordID));
                return params;
            }

        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        wrappers.VolleyWrapper.getInstance().addToRequestQueue(request, "deleteRecord");
    }

    private int getRecordCount() {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + tableRecords;
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    private int getLastRecordID() {
        SQLiteDatabase db = getWritableDatabase();
        String selectQuery = ("SELECT * FROM " + tableRecords);
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToLast();
        int id = 0;
        if (cursor.getCount() != 0) {
            id = cursor.getInt(0);
        }
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
            record = loadRecord(cursor);
        }
        cursor.close();
        db.close();
        return record;
    }

    public ArrayList<ExerciseRecord> getLastTwoRecords(String name) {
        SQLiteDatabase db = getWritableDatabase();
        String selectQuery = ("SELECT * FROM " + tableRecords + " WHERE " + keyExName + " = '" + name + "'");
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<ExerciseRecord> records = new ArrayList<>();
        if (cursor.getCount() > 0) {
            cursor.moveToLast();
            if (cursor.getCount() > 1) {
                cursor.moveToPrevious();
                records.add(loadRecord(cursor));
                cursor.moveToNext();
            }
            records.add(loadRecord(cursor));
            Collections.reverse(records);
        }
        cursor.close();
        db.close();
        return records;
    }

    public ArrayList<ExerciseRecord> getLastRecords(String name) {
        SQLiteDatabase db = getWritableDatabase();
        String selectQuery = ("SELECT * FROM " + tableRecords + " WHERE " + keyExName + " = '" + name + "'");
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToLast();
        ArrayList<ExerciseRecord> records = new ArrayList<>();
        if (cursor.getCount() >= 3) {
            for (int i = 0; i < 3; i++) {
                records.add(loadRecord(cursor));
                cursor.moveToPrevious();
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
                records.add(loadRecord(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return records;
    }

    public ExerciseRecord getLastPlanRecord(String exName, String plName, int dayNum) {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = ("SELECT * FROM " + tableRecords + " WHERE " + keyExName + " = '" + exName + "' AND " + keyPlName + " = '" + new QueryValidator(plName).validateQuery() + "' AND " + keyDayNum + " = " + dayNum);
        Cursor cursor = db.rawQuery(selectQuery, null);
        ExerciseRecord record = null;
        if (cursor.getCount() != 0) {
            cursor.moveToLast();
            record = loadRecord(cursor);
        }
        cursor.close();
        db.close();
        return record;
    }

    public boolean checkAreaRecords(String area) {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = ("SELECT * FROM " + tableRecords + " WHERE " + keyExName + " IN (SELECT " + keyName + " FROM " + tableExercises + " WHERE " + keyAreas + " LIKE '%" + area + "%')");
        Cursor cursor = db.rawQuery(selectQuery, null);
        boolean records = false;
        if (cursor.getCount() != 0) {
            records = true;
        }
        cursor.close();
        db.close();
        return records;
    }

    private ExerciseRecord getLastPlan() {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = ("SELECT * FROM " + tableRecords + " WHERE " + keyPlName + " != ' '");
        Cursor cursor = db.rawQuery(selectQuery, null);
        ExerciseRecord record = null;
        if (cursor.getCount() != 0) {
            cursor.moveToLast();
            record = loadRecord(cursor);
        }
        cursor.close();
        db.close();
        return record;
    }

    public ArrayList<ExerciseRecord> getRecords(String name) {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = ("SELECT * FROM " + tableRecords + " WHERE " + keyExName + " = '" + name + "'");
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<ExerciseRecord> records = new ArrayList<>();
        if (cursor.getCount() != 0) {
            cursor.moveToLast();
            while (!cursor.isBeforeFirst()) {
                records.add(loadRecord(cursor));
                cursor.moveToPrevious();
            }
        }
        cursor.close();
        db.close();
        return records;
    }

    public ArrayList<ExerciseRecord> getDayRecords(String name, int daynum) {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = ("SELECT * FROM " + tableRecords + " WHERE " + keyPlName + " = '" + new QueryValidator(name).validateQuery() + "' AND " + keyDayNum + " = " + daynum);
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<ExerciseRecord> records = new ArrayList<>();
        if (cursor.getCount() != 0) {
            cursor.moveToLast();
            while (!cursor.isBeforeFirst()) {
                records.add(loadRecord(cursor));
                cursor.moveToPrevious();
            }
        }
        cursor.close();
        db.close();
        return records;
    }

    private Exercise loadExercise(Cursor cursor) {
        return new Exercise(cursor.getString(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3), cursor.getInt(4), cursor.getInt(5), cursor.getString(6), (cursor.getInt(7) != 0));
    }

    private Exercise loadJSONExercise(JSONObject jObj) throws JSONException {
        return new Exercise(jObj.getString(keyName), jObj.getString(keyDesc), jObj.getInt(keyType), jObj.getString(keyImage), jObj.getInt(keyMin), jObj.getInt(keyMax), jObj.getString(keyAreas), (jObj.getInt(keyUser) > 0));
    }

    private Plan loadPlan(Cursor cursor) {
        return new Plan(cursor.getString(0), cursor.getString(1), cursor.getString(2), (cursor.getInt(3) != 0));
    }

    private Plan loadJSONPlan(JSONObject jObj) throws JSONException {
        return new Plan(jObj.getString(keyName), jObj.getString(keyDesc), jObj.getString(keyDays), (jObj.getInt("userMade") > 0));
    }

    private Day loadDay(Cursor cursor) {
        return new Day(cursor.getString(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
    }

    private Day loadJSONDay(JSONObject jObj) throws JSONException {
        return new Day(jObj.getString("planName"), jObj.getInt("dayNumber"), jObj.getString("exercises"), jObj.getString("sets"), jObj.getString("reps"));
    }

    private ExerciseRecord loadRecord(Cursor cursor) {
        return new ExerciseRecord(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getString(4), cursor.getString(5));
    }

    private ExerciseRecord loadJSONRecord(JSONObject jObj) throws JSONException {
        return new ExerciseRecord(jObj.getInt(keyID), jObj.getString(keyExName), jObj.getString(keyPlName), jObj.getInt(keyDayNum), jObj.getString(keyTime), jObj.getString(keySets));
    }
}
