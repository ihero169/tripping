package com.example.mexpense.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ExpenseRepository extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "expenses";

    public static final String COLUMN_ID = "expense_id";
    public static final String COLUMN_NAME = "name"; // Required
    public static final String COLUMN_DESTINATION = "destination"; // Required
    public static final String COLUMN_DATE = "date"; // Required
    public static final String COLUMN_REQUIRED_ASSESSMENT = "required assessment"; // Required
    public static final String COLUMN_DESCRIPTION = "description"; // Optional
    public static final String COLUMN_CATEGORY = "category"; // Required
    public static final String COLUMN_COST = "cost"; // Required

    //

    private SQLiteDatabase database;

    private static final String DATABASE_CREATE = String.format(
            "CREATE TABLE %s (" +
                    " %s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " %s TEXT, " +
                    " %s TEXT, " +
                    " %s TEXT, " +
                    " %s TEXT, " +
                    " %s TEXT, " +
                    " %s TEXT, " +
                    " %s TEXT)",
            DATABASE_NAME,  COLUMN_ID, COLUMN_NAME, COLUMN_CATEGORY, COLUMN_DESTINATION, COLUMN_DATE, COLUMN_REQUIRED_ASSESSMENT, COLUMN_DESCRIPTION, COLUMN_COST
    );

    public ExpenseRepository(Context context){
        super(context, DATABASE_NAME, null, 1);
        database = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        Log.w(this.getClass().getName(), DATABASE_NAME + " database upgraded from version " + oldVersion + "to new version " + newVersion);
        onCreate(database);
    }
}
