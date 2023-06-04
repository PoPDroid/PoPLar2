package com.yleg.testvictimbutton;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "PoPDB";

    // Table Names
    private static final String TABLE_TODO = "pop";

    // column names
    private static final String KEY_ID = "id";
    private static final String KEY_DEPTH = "depth";
    private static final String KEY_DURATION = "duration";

    // *********************************************************************************************
    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE);


    }

    // Upgrading database **************************************************************************
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);


        // Create tables again
        onCreate(db);
    }

    // Creating Table TABLE_TEAM
    String CREATE_TABLE = "CREATE TABLE " + TABLE_TODO + "("
            + KEY_ID + " integer primary key autoincrement, "
            + KEY_DEPTH + " integer, "
            + KEY_DURATION + " long" + ")";


    // insert values of todo
    public boolean InsertPuzzleDetails(int depth, long duration) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_DEPTH, depth);
        contentValues.put(KEY_DURATION, duration);

        long rowInserted = db.insert(TABLE_TODO, null, contentValues);
        db.close();
        return true;
    }



    // Select values of todo
    public int getCurrentLevel() {
        SQLiteDatabase db = this.getReadableDatabase();
        int res = 0;

        String query = "SELECT COUNT(*) FROM " + TABLE_TODO;
        Cursor mcursor = db.rawQuery(query, null);

        if (mcursor != null) {
            mcursor.moveToFirst();
            try{
                res = mcursor.getInt(0);
            }catch (Exception e){

            }
        }

        return res;

    }

    // Select values of todo
    public int GetCurrentDepth() {
        SQLiteDatabase db = this.getReadableDatabase();
        int res = 0;

        String query = "SELECT MAX(depth) FROM " + TABLE_TODO;
        Cursor mcursor = db.rawQuery(query, null);

        if (mcursor != null) {
            mcursor.moveToFirst();
            try{
                res = mcursor.getInt(0);
            }catch (Exception e){

            }
        }

        return res;

    }

    // Select values of todo
    public Cursor GetAllPuzzleLogs() {
        SQLiteDatabase db = this.getReadableDatabase();


        String query = "SELECT * FROM " + TABLE_TODO;
        Cursor mcursor = db.rawQuery(query, null);

        if (mcursor != null) {
            mcursor.moveToFirst();
        }

        return mcursor;

    }

    // Select values of todo
    public Cursor resetPuzzleLogs() {
        SQLiteDatabase db = this.getReadableDatabase();


        String query = "DELETE FROM " + TABLE_TODO;
        Cursor mcursor = db.rawQuery(query, null);

        if (mcursor != null) {
            mcursor.moveToFirst();
        }

        return mcursor;

    }



}