package com.example.mexpense.services;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.mexpense.ultilities.Constants;

public class SqlService extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "mExpense";
    public static final String TRIPS_TABLE_NAME = "trips_table";
    public static final String EXPENSE_TABLE_NAME = "expenses_table";

    private SQLiteDatabase database;

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
                    " %s BLOB," +
                    " FOREIGN KEY(trip_id) REFERENCES trips_table(trip_id) ON DELETE CASCADE )",
            EXPENSE_TABLE_NAME, Constants.COLUMN_ID_EXPENSE, Constants.COLUMN_CATEGORY_EXPENSE, Constants.COLUMN_COST_EXPENSE, Constants.COLUMN_AMOUNT_EXPENSE, Constants.COLUMN_DATE_EXPENSE, Constants.COLUMN_COMMENT_EXPENSE, Constants.COLUMN_TRIP_ID_EXPENSE, Constants.COLUMN_LATITUDE_EXPENSE, Constants.COLUMN_LONGITUDE_EXPENSE, Constants.COLUMN_IMAGE_EXPENSE
    );


    public SqlService(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 2);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        this.database = sqLiteDatabase;
        database.execSQL(TRIP_DATABASE_CREATE);
        Log.i("SQLITE", "TRIP DB CREATED");
        database.execSQL(EXPENSE_DATABASE_CREATE);
        Log.i("SQLITE", "EXPENSE DB CREATED");
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

    public SQLiteDatabase getDatabase(){
        return database;
    }
}

