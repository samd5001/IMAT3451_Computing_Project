package wrappers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import models.User;

public class SQLiteUserWrapper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "users";
    private static final String TABLE_USER = "user";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAME = "name";
    private static final String KEY_DOB = "dob";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_GOAL = "goal";

    public SQLiteUserWrapper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUser= "CREATE TABLE " + TABLE_USER + "(" +
            KEY_EMAIL + " varchar(50) NOT NULL UNIQUE PRIMARY KEY, " +
            KEY_NAME + " varchar(40) NOT NULL, " +
            KEY_DOB + " date, " +
            KEY_GENDER + " bit, " +
            KEY_HEIGHT + " float, " +
            KEY_WEIGHT + " float, " +
            KEY_GOAL + " bit)";
        db.execSQL(createUser);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String email, String name,
                        String dob, String gender, String height, String weight, String goal) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EMAIL, email);
        values.put(KEY_NAME, name);
        values.put(KEY_DOB, dob);
        values.put(KEY_GENDER, gender);
        values.put(KEY_HEIGHT, height);
        values.put(KEY_WEIGHT, weight);
        values.put(KEY_GOAL, goal);

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close();
    }

    /**
     * Getting user data from database
     * */
    public User getUser() {

        String selectQuery = "SELECT  * FROM " + TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        User user = new User(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7));
        cursor.close();
        db.close();
        return user;
    }

    public void deleteUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();
    }

}
