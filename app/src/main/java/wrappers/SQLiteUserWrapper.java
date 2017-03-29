package wrappers;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import classes.User;

public class SQLiteUserWrapper extends SQLiteOpenHelper {

    private static final String dbName = "users";
    private static final String tableUser = "user";
    private static final String keyEmail = "email";
    private static final String keyName = "name";
    private static final String keyDob = "dob";
    private static final String keyGender = "gender";
    private static final String keyHeight = "height";
    private static final String keyWeight = "weight";
    private static final String keyGoal = "goal";
    private SharedPreferences prefs;

    public SQLiteUserWrapper(Context context, SharedPreferences prefs) {
        super(context, dbName, null, 1);
        this.prefs = prefs;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUser= "CREATE TABLE " + tableUser + "(" +
                keyEmail + " varchar(50) NOT NULL UNIQUE PRIMARY KEY, " +
                keyName + " varchar(40) NOT NULL, " +
                keyDob + " date, " +
                keyGender + " bit, " +
                keyHeight + " float, " +
                keyWeight + " float, " +
                keyGoal + " bit)";
        db.execSQL(createUser);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tableUser);
        onCreate(db);
    }

    public void storeUser(String email, String name,
                          String dob, String gender, String height, String weight, String goal) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(keyEmail, email);
        values.put(keyName, name);
        values.put(keyDob, dob);
        values.put(keyGender, gender);
        values.put(keyHeight, height);
        values.put(keyWeight, weight);
        values.put(keyGoal, goal);
        db.insert(tableUser, null, values);
        db.close();
        prefs.edit().putBoolean("loggedIn", true).apply();
    }

    public User getUser() {
        if (prefs.getBoolean("loggedIn", true)) {
            String selectQuery = "SELECT  * FROM " + tableUser;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            User user = new User(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
            cursor.close();
            db.close();
            return user;
        } else {
            return null;
        }
    }

    public void logoutUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        String truncateQuery = "TRUNCATE " + tableUser;
        db.execSQL(truncateQuery);
        db.close();
        prefs.edit().putBoolean("loggedIn", false).apply();
    }

}
