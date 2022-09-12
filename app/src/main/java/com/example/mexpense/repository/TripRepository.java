package com.example.mexpense.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.mexpense.entity.Trip;
import com.example.mexpense.ultilities.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TripRepository extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "trips_table";

    public static final String COLUMN_ID = "trip_id";
    public static final String COLUMN_NAME = "category"; // Required
    public static final String COLUMN_DESTINATION = "cost"; // Required
    public static final String COLUMN_START_DATE = "startDate"; // Required
    public static final String COLUMN_END_DATE = "endDate"; // Required
    public static final String COLUMN_REQUIRED_ASSESSMENT = "comment"; // Optional
    public static final String COLUMN_DESCRIPTION = "description";

    private SQLiteDatabase database;

    private static final String DATABASE_CREATE = String.format(
            "CREATE TABLE %s (" +
                    " %s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " %s TEXT, " +
                    " %s TEXT, " +
                    " %s TEXT, " +
                    " %s TEXT, " +
                    " %s TEXT, " +
                    " %s TEXT)",
            TABLE_NAME, COLUMN_ID, COLUMN_NAME, COLUMN_DESTINATION, COLUMN_START_DATE, COLUMN_END_DATE, COLUMN_REQUIRED_ASSESSMENT, COLUMN_DESCRIPTION
    );

    public TripRepository(Context context) {
        super(context, TABLE_NAME, null, 1);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        this.database = sqLiteDatabase;
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        Log.w(this.getClass().getName(), TABLE_NAME + " upgraded from version " + oldVersion + "to new version " + newVersion);
        onCreate(database);
    }

    public List<Trip> getTrips() {
        List<Trip> trips = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        while (c.moveToNext()){
            String name = c.getString(1);
            String destination = c.getString(2);
            String startDate = c.getString(3);
            String endDate = c.getString(4);
            boolean assessment = Objects.equals(c.getString(5), "1");
            String description = c.getString(6);
            Trip t = new Trip(Integer.parseInt(c.getString(0)),name, destination, startDate, endDate, assessment, description);
            trips.add(t);
            Log.w(Constants.SQL, t.toString());
        }
        c.close();
        db.close();
        return trips;
    }

    public Trip getTripById(int id) {
        Trip t = new Trip();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=" + id, null);
        while (c.moveToNext()){
            t.setId(Integer.parseInt(c.getString(0)));
            t.setName(c.getString(1));
            t.setDestination(c.getString(2));
            t.setStartDate(c.getString(3));
            t.setEndDate(c.getString(4));
            t.setDescription(c.getString(5));
            t.setRequiredAssessment(c.getString(6).equals("1"));
        }
        c.close();
        db.close();
        Log.w(Constants.SQL, t.toString());
        return t;
    }

    public void addTrip(Trip trip) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, trip.getName());
        cv.put(COLUMN_DESTINATION, trip.getDestination());
        cv.put(COLUMN_START_DATE, trip.getStartDate());
        cv.put(COLUMN_END_DATE, trip.getEndDate());
        cv.put(COLUMN_REQUIRED_ASSESSMENT, trip.getRequiredAssessment() ? "1" : "0");
        cv.put(COLUMN_DESCRIPTION, trip.getDescription());

        db.insert(TABLE_NAME, COLUMN_DESCRIPTION, cv);
        db.close();
    }

    public void deleteTrip(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void updateTrip(int id, Trip trip){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, trip.getName());
        cv.put(COLUMN_DESTINATION, trip.getDestination());
        cv.put(COLUMN_START_DATE, trip.getStartDate());
        cv.put(COLUMN_END_DATE, trip.getEndDate());
        cv.put(COLUMN_REQUIRED_ASSESSMENT, trip.getRequiredAssessment() ? "1" : "0");
        cv.put(COLUMN_DESCRIPTION, trip.getDescription());

        db.update(TABLE_NAME, cv, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }
}
