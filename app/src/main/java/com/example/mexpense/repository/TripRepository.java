package com.example.mexpense.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mexpense.entity.Trip;
import com.example.mexpense.services.SqlService;
import com.example.mexpense.ultilities.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TripRepository extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "trips_table";

    private SQLiteDatabase database;

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        this.database = sqLiteDatabase;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        onCreate(database);
    }

    public TripRepository(Context context) {
        super(context, "mExpense", null, 2);
        SqlService sqlService = new SqlService(context);
        database = sqlService.getDatabase();
    }

    public List<Trip> getTrips() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor c = db.rawQuery(query, null);
        return getList(c);
    }

    public Trip getTripById(int id) {
        Trip t = new Trip();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + Constants.COLUMN_ID_TRIP + "=" + id, null);
        while (c.moveToNext()){
            t.setId(Integer.parseInt(c.getString(0)));
            t.setName(c.getString(1));
            t.setDestination(c.getString(2));
            t.setStartDate(c.getString(3));
            t.setEndDate(c.getString(4));
            t.setRequiredAssessment(Integer.parseInt(c.getString(5)) == 1);
            t.setParticipants(Integer.parseInt(c.getString(6)));
            t.setDescription(c.getString(7));
            t.setTotal(Double.parseDouble(c.getString(8)));
        }
        c.close();
        return t;
    }

    public void addTrip(Trip trip) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(Constants.COLUMN_NAME_TRIP , trip.getName());
        cv.put(Constants.COLUMN_DESTINATION_TRIP , trip.getDestination());
        cv.put(Constants.COLUMN_START_DATE_TRIP , trip.getStartDate());
        cv.put(Constants.COLUMN_END_DATE_TRIP , trip.getEndDate());
        cv.put(Constants.COLUMN_REQUIRED_ASSESSMENT_TRIP , trip.getRequiredAssessment() ? "1" : "0");
        cv.put(Constants.COLUMN_PARTICIPANT_TRIP , trip.getParticipants());
        cv.put(Constants.COLUMN_DESCRIPTION_TRIP , trip.getDescription());
        cv.put(Constants.COLUMN_TOTAL_TRIP , 0.0);

        db.insert(TABLE_NAME, Constants.COLUMN_DESCRIPTION_TRIP , cv);
    }

    public void deleteTrip(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, Constants.COLUMN_ID_TRIP  + "=?", new String[]{String.valueOf(id)});
        db.delete(Constants.EXPENSE_TABLE_NAME, Constants.COLUMN_TRIP_ID_EXPENSE  + "=?", new String[]{String.valueOf(id)});
    }

    public void updateTrip(int id, Trip trip){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(Constants.COLUMN_NAME_TRIP , trip.getName());
        cv.put(Constants.COLUMN_DESTINATION_TRIP , trip.getDestination());
        cv.put(Constants.COLUMN_START_DATE_TRIP , trip.getStartDate());
        cv.put(Constants.COLUMN_END_DATE_TRIP , trip.getEndDate());
        cv.put(Constants.COLUMN_REQUIRED_ASSESSMENT_TRIP , trip.getRequiredAssessment() ? "1" : "0");
        cv.put(Constants.COLUMN_PARTICIPANT_TRIP , trip.getParticipants());
        cv.put(Constants.COLUMN_DESCRIPTION_TRIP , trip.getDescription());
        cv.put(Constants.COLUMN_TOTAL_TRIP , trip.getTotal());

        db.update(TABLE_NAME, cv, Constants.COLUMN_ID_TRIP  + "=?", new String[]{String.valueOf(id)});
    }

    public void updateTotal(int id, double total){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Constants.COLUMN_TOTAL_TRIP , total);
        db.update(TABLE_NAME, cv, Constants.COLUMN_ID_TRIP  + "=?", new String[]{String.valueOf(id)});
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.execSQL("DELETE FROM expenses_table");
    }

    public List<Trip> masterSearch(String name, String destination, String start, String end){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE ";
        boolean isFirst = true;
        if(!name.equals("")) {
            query += Constants.COLUMN_NAME_TRIP  + " LIKE '%" + name + "%'";
            isFirst = false;
        }

        if(!destination.equals("")){
            if(!isFirst){
                query += " AND ";
            }

            query += Constants.COLUMN_DESTINATION_TRIP  + " LIKE '%" + destination + "%'";
            isFirst = false;
        }

        if(!isFirst){
            query += " AND ";
        }

        query += Constants.COLUMN_START_DATE_TRIP  + " >= '" + start + "' AND " + Constants.COLUMN_END_DATE_TRIP  + " <= '" + end + "'";

        Cursor c = db.rawQuery(query, null);
        return getList(c);
    }

    public List<Trip> getList(Cursor c){
        List<Trip> trips = new ArrayList<>();
        while (c.moveToNext()){
            String name = c.getString(1);
            String destination = c.getString(2);
            String startDate = c.getString(3);
            String endDate = c.getString(4);
            boolean assessment = Objects.equals(c.getString(5), "1");
            int participant = Integer.parseInt(c.getString(6));
            String description = c.getString(7);
            Double total = Double.parseDouble(c.getString(8));
            Trip t = new Trip(Integer.parseInt(c.getString(0)),name, destination, startDate, endDate, assessment, participant, description, total);
            trips.add(t);
        }
        c.close();
        return trips;
    }

}