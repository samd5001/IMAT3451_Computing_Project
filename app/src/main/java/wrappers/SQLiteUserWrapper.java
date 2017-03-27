package wrappers;

import android.content.ContentValues;
import android.content.Context;
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

    public SQLiteUserWrapper(Context context) {
        super(context, dbName, null, 1);
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

        // Inserting Row
        db.insert(tableUser, null, values);
        db.close();
    }

    /**
     * Getting user data from database
     * */
    public User getUser() {

        String selectQuery = "SELECT  * FROM " + tableUser;
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
        db.delete(tableUser, null, null);
        db.close();
    }

}
