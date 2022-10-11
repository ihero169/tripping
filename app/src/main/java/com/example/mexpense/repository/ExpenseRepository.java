package com.example.mexpense.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.mexpense.entity.Expense;

import java.util.ArrayList;
import java.util.List;

public class ExpenseRepository extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "expenses_table";

    public static final String COLUMN_ID = "expense_id";
    public static final String COLUMN_CATEGORY = "category"; // Required
    public static final String COLUMN_COST = "cost"; // Required
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_DATE = "date"; // Required
    public static final String COLUMN_COMMENT = "comment"; // Optional
    public static final String COLUMN_TRIP_ID = "trip_id";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_IMAGE = "image";

    private SQLiteDatabase database;

    private static final String DATABASE_CREATE = String.format(
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
            TABLE_NAME, COLUMN_ID, COLUMN_CATEGORY, COLUMN_COST, COLUMN_AMOUNT, COLUMN_DATE, COLUMN_COMMENT, COLUMN_TRIP_ID, COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_IMAGE
    );

    public ExpenseRepository(@Nullable Context context) {
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

    public List<Expense> getExpenses(int trip) {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        while (c.moveToNext()) {
            if (Integer.parseInt(c.getString(6)) == trip) {
                String category = c.getString(1);
                double cost = Double.parseDouble(c.getString(2));
                int amount = Integer.parseInt(c.getString(3));
                String date = c.getString(4);
                String comment = c.getString(5);
                int trip_id = Integer.parseInt(c.getString(6));
                double latitude = Double.parseDouble(c.getString(7));
                double longitude = Double.parseDouble(c.getString(8));
                Expense expense = new Expense(Integer.parseInt(c.getString(0)), category, cost, amount, date, comment, trip_id, latitude, longitude);
                expenses.add(expense);
            }
        }
        c.close();
        return expenses;
    }

    public List<Expense> getAllExpenses() {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        while (c.moveToNext()) {
            String category = c.getString(1);
            double cost = Double.parseDouble(c.getString(2));
            int amount = Integer.parseInt(c.getString(3));
            String date = c.getString(4);
            String comment = c.getString(5);
            int trip_id = Integer.parseInt(c.getString(6));
            double latitude = Double.parseDouble(c.getString(7));
            double longitude = Double.parseDouble(c.getString(8));
            Expense expense = new Expense(Integer.parseInt(c.getString(0)), category, cost, amount, date, comment, trip_id, latitude, longitude);
            expenses.add(expense);
        }
        c.close();
        return expenses;
    }

    public Expense getExpenseById(int id) {
        Expense e = new Expense();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=" + id, null);
        while (c.moveToNext()) {
            e.setId(Integer.parseInt(c.getString(0)));
            e.setCategory(c.getString(1));
            e.setCost(Double.parseDouble(c.getString(2)));
            e.setAmount(Integer.parseInt(c.getString(3)));
            e.setDate(c.getString(4));
            e.setComment(c.getString(5));
            e.setTripId(Integer.parseInt(c.getString(6)));
            e.setLatitude(Double.parseDouble(c.getString(7)));
            e.setLongitude(Double.parseDouble(c.getString(8)));
        }
        c.close();
        return e;
    }

    public void addExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CATEGORY, expense.getCategory());
        cv.put(COLUMN_COST, expense.getCost());
        cv.put(COLUMN_AMOUNT, expense.getAmount());
        cv.put(COLUMN_DATE, expense.getDate());
        cv.put(COLUMN_COMMENT, expense.getComment());
        cv.put(COLUMN_TRIP_ID, expense.getTripId());
        cv.put(COLUMN_LATITUDE, expense.getLatitude());
        cv.put(COLUMN_LONGITUDE, expense.getLongitude());
        db.insert(TABLE_NAME, COLUMN_COMMENT, cv);
    }

    public void deleteExpenses(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void updateExpense(int id, Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_CATEGORY, expense.getCategory());
        cv.put(COLUMN_COST, expense.getCost());
        cv.put(COLUMN_AMOUNT, expense.getAmount());
        cv.put(COLUMN_DATE, expense.getDate());
        cv.put(COLUMN_COMMENT, expense.getComment());

        db.update(TABLE_NAME, cv, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
    }
}
