package com.example.pocketmapdirections.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.pocketmapdirections.models.PlacesModel;
import com.example.pocketmapdirections.models.PlanedRouteModel;

import java.util.ArrayList;
import java.util.List;

import static com.example.pocketmapdirections.models.PlacesModel.COLUMN_ID;


public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "favorite_location_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(PlanedRouteModel.CREATE_TABLE2);
        db.execSQL(PlacesModel.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + PlacesModel.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PlanedRouteModel.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertNote(String location, String subtitle) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(PlacesModel.COLUMN_TITLE, subtitle);
        values.put(PlacesModel.COLUMN_SUB_TITLE, location);

        // insert row
        long id = db.insert(PlacesModel.TABLE_NAME, null, values);
        Log.d("database", "ok Done" + id);
        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public long insertRoute(String start, String end, String date, String time) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(PlanedRouteModel.COLUMN_START_POINT, start);
        values.put(PlanedRouteModel.COLUMN_END_POINT, end);
        values.put(PlanedRouteModel.COLUMN_DATE, date);
        values.put(PlanedRouteModel.COLUMN_TIME, time);

        // insert row
        long id = db.insert(PlanedRouteModel.TABLE_NAME, null, values);
        Log.d("database", "ok Done" + id);
        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public PlacesModel getUrlpath(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(PlacesModel.TABLE_NAME,
                new String[]{PlacesModel.COLUMN_ID, PlacesModel.COLUMN_TITLE, PlacesModel.COLUMN_SUB_TITLE},
                PlacesModel.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        PlacesModel note = new PlacesModel(
                cursor.getInt(cursor.getColumnIndex(PlacesModel.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(PlacesModel.COLUMN_TITLE)),
                cursor.getString(cursor.getColumnIndex(PlacesModel.COLUMN_SUB_TITLE)));

        // close the db connection
        cursor.close();

        return note;
    }

    public List<PlacesModel> getAllNotes() {
        List<PlacesModel> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + PlacesModel.TABLE_NAME + " ORDER BY " +
                PlacesModel.COLUMN_SUB_TITLE + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PlacesModel note = new PlacesModel();
                note.setId(cursor.getInt(cursor.getColumnIndex(PlacesModel.COLUMN_ID)));
                note.setTitle(cursor.getString(cursor.getColumnIndex(PlacesModel.COLUMN_TITLE)));
                note.setSubtitle(cursor.getString(cursor.getColumnIndex(PlacesModel.COLUMN_SUB_TITLE)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }


    public List<PlanedRouteModel> getAllRoutes() {
        List<PlanedRouteModel> notes = new ArrayList<>();

        // Select All Query
        String selectQuery1 = "SELECT  * FROM " + PlanedRouteModel.TABLE_NAME + " ORDER BY " +
                PlanedRouteModel.COLUMN_START_POINT + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery1, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PlanedRouteModel note=new PlanedRouteModel();
                note.setId(cursor.getInt(cursor.getColumnIndex(PlanedRouteModel.COLUMN_ID)));
                note.setStartpoint(cursor.getString(cursor.getColumnIndex(PlanedRouteModel.COLUMN_START_POINT)));
                note.setEndpoint(cursor.getString(cursor.getColumnIndex(PlanedRouteModel.COLUMN_END_POINT)));
                note.setDate(cursor.getString(cursor.getColumnIndex(PlanedRouteModel.COLUMN_DATE)));
                note.setTime(cursor.getString(cursor.getColumnIndex(PlanedRouteModel.COLUMN_TIME)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }

    public int getNotesCount() {
        String countQuery = "SELECT  * FROM " + PlacesModel.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int updateNote(PlacesModel historyModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PlacesModel.COLUMN_TITLE, historyModel.getTitle());

        // updating row
        return db.update(PlacesModel.TABLE_NAME, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(historyModel.getId())});
    }

    public void deleteNote(int note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PlacesModel.TABLE_NAME, PlacesModel.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note)});
        db.close();
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(PlacesModel.TABLE_NAME, null, null);
        db.close();
    }

    public void deleteEntry(long row) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Log.d("ok", "ROWIS" + row);
        sqLiteDatabase.delete(PlacesModel.TABLE_NAME, COLUMN_ID + "=" + row, null);

    }

    public void deleteRoute(long row) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Log.d("ok", "ROWIS" + row);
        sqLiteDatabase.delete(PlanedRouteModel.TABLE_NAME, COLUMN_ID + "=" + row, null);

    }
}
