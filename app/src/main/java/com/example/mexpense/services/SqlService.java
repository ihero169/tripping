package com.example.mexpense.services;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.mexpense.ultilities.Constants;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SqlService extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "mExpense";
    public static final String TRIPS_TABLE_NAME = "trips_table";
    public static final String EXPENSE_TABLE_NAME = "expenses_table";

    private SQLiteDatabase database;

    public static SQLiteDatabase db;
    private static SqlService instance = null;
    private static Context context;

    public static SqlService getInstance(Context context){
        if(instance == null){
            instance = new SqlService(context.getApplicationContext());
        }
        return instance;
    }

    public SqlService(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        database = getWritableDatabase();
    }

    private static final String TRIP_DATABASE_CREATE = String.format(
            "CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " %s TEXT, " +
                    " %s TEXT, " +
                    " %s TEXT, " +
                    " %s TEXT, " +
                    " %s TEXT, " +
                    " %s INTEGER," +
                    " %s TEXT, " +
                    " %s REAL);",
            TRIPS_TABLE_NAME, Constants.COLUMN_ID_TRIP, Constants.COLUMN_NAME_TRIP, Constants.COLUMN_DESTINATION_TRIP, Constants.COLUMN_START_DATE_TRIP, Constants.COLUMN_END_DATE_TRIP, Constants.COLUMN_REQUIRED_ASSESSMENT_TRIP, Constants.COLUMN_PARTICIPANT_TRIP, Constants.COLUMN_DESCRIPTION_TRIP, Constants.COLUMN_TOTAL_TRIP
    );

    private static final String EXPENSE_DATABASE_CREATE = String.format(
            "CREATE TABLE %s (" +
                    " %s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " %s TEXT, " +
                    " %s REAL, " +
                    " %s INTEGER, " +
                    " %s TEXT, " +
                    " %s TEXT, " +
                    " %s INTEGER," +
                    " %s REAL," +
                    " %s REAL," +
                    " %s TEXT," +
                    " FOREIGN KEY(trip_id) REFERENCES trips_table(trip_id) ON DELETE CASCADE )",
            EXPENSE_TABLE_NAME, Constants.COLUMN_ID_EXPENSE, Constants.COLUMN_CATEGORY_EXPENSE, Constants.COLUMN_COST_EXPENSE, Constants.COLUMN_AMOUNT_EXPENSE, Constants.COLUMN_DATE_EXPENSE, Constants.COLUMN_COMMENT_EXPENSE, Constants.COLUMN_TRIP_ID_EXPENSE, Constants.COLUMN_LATITUDE_EXPENSE, Constants.COLUMN_LONGITUDE_EXPENSE, Constants.COLUMN_IMAGE_PATH_EXPENSE
    );

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        this.database = sqLiteDatabase;
        database.execSQL(TRIP_DATABASE_CREATE);
        database.execSQL(EXPENSE_DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        try{
            database.execSQL("DROP TABLE IF EXISTS " + EXPENSE_TABLE_NAME);
            database.execSQL("DROP TABLE IF EXISTS " + TRIPS_TABLE_NAME);
        }
        catch (Exception e){
            Log.i("SQLITE DATABASE", "onUpgrade: " + e);
        }
        onCreate(database);
    }

    public List<String> fetchPayload(){
        List<String> payload = new ArrayList<>();
        Gson g = new Gson();
        database = this.getReadableDatabase();
        Cursor c = database.rawQuery( String.format("SELECT %s AS name, %s AS date, %s AS trip, %s AS amount, %s AS cost, %s AS destination, %s AS comment FROM %s a, %s b WHERE a.%s = b.%s",
                Constants.COLUMN_CATEGORY_EXPENSE, Constants.COLUMN_DATE_EXPENSE, Constants.COLUMN_NAME_TRIP,
                Constants.COLUMN_AMOUNT_EXPENSE, Constants.COLUMN_COST_EXPENSE, Constants.COLUMN_DESTINATION_TRIP,
                Constants.COLUMN_COMMENT_EXPENSE, EXPENSE_TABLE_NAME, TRIPS_TABLE_NAME, Constants.COLUMN_TRIP_ID_EXPENSE, Constants.COLUMN_ID_TRIP), null);
        while (c.moveToNext()){
            payload.add(g.toJson(new CloudData(c)));
        }
        return payload;
    }
}

class CloudData {
    private String name;
    private String date;
    private String trip;
    private int amount;
    private double cost;
    private String destination;
    private String comment;

    public CloudData(Cursor c){
        name = c.getString(0);
        date = c.getString(1);
        trip = c.getString(2);
        amount = c.getInt(3);
        cost = c.getDouble(4);
        destination = c.getString(5);
        comment = c.getString(6);
    }
}

