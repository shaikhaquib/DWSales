package dealwithusmailcom.dwsales;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table name
    private static final String TABLE_USER = "user";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_DESIG = "desig";
    private static final String KEY_STATUS = "ststus";
    private static final String KEY_CREATED_AT = "created_at";

    // Login table name
    private static final String TABLE_SMSREPORT = "smsreport";

    // Login Table Columns names
    private static final String KEY_SMSID = "id";
    private static final String KEY_DATE= "date";
    private static final String KEY_COUNT = "count";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT,"
                + KEY_STATUS + " TEXT ," + KEY_DESIG + " TEXT,"
                + KEY_CREATED_AT + " TEXT," + KEY_TOKEN + " TEXT" + ")";

        /*String CREATE_SMS_REPORT_TABLE = "CREATE TABLE " + TABLE_SMSREPORT + "("
                + KEY_SMSID + " TEXT,"
                + KEY_DATE + " TEXT," + KEY_COUNT + " TEXT" + ")";*/

        db.execSQL(CREATE_LOGIN_TABLE);
      //  db.execSQL(CREATE_SMS_REPORT_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
     //   db.execSQL("DROP TABLE IF EXISTS " + TABLE_SMSREPORT);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String name, String email, String uid,String status, String desig, String created_at, String token) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_UID, uid); // Email
        values.put(KEY_STATUS, status); // Email
        values.put(KEY_DESIG, desig); // Email
        values.put(KEY_CREATED_AT, created_at); // Created At
        values.put(KEY_TOKEN, token); // Created At

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

/*
    public void addSMSReport (String id,String date, String count)
    {


        SQLiteDatabase db = this.getReadableDatabase();
        //     onCreate(db);
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SMSREPORT + " WHERE " + KEY_SMSID + " =?",new String[] {id});
        // Cursor cursor=db.rawQuery("Select * from " + USER_TABLE_ADDICON + " where " + icon + " = " + COLUMN_ICON,null);
        System.out.println(cursor);


        if(cursor.getCount() <= 0){
            ContentValues values = new ContentValues();
            values.put(KEY_SMSID, id);
            values.put(KEY_DATE, date);
            values.put(KEY_COUNT,count);


            long id1 = db.insert(TABLE_SMSREPORT, null, values);
            db.close();

            Log.d(TAG, "User medicine history inserted " +id1);}else {
            System.out.println("Allready Exisist");
            db.close();
        }
    }
*/

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("uid", cursor.getString(3));
            user.put("status", cursor.getString(4));
            user.put("desig", cursor.getString(5));
            user.put("created_at", cursor.getString(6));
            user.put("token", cursor.getString(7));

        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

/*
    public Cursor getAddIcon(){
        //String selectQuery = "select * from " + USER_TABLE_ADDICON;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from "+ TABLE_SMSREPORT, null);
        return cursor;
    }
*/


    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

/*
    public void deleteSMSreport() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_SMSREPORT, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }
*/
///*
//
//
//
//
//
//
//
//
//
//
//
//*/

}