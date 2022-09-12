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
import java.util.Objects;

public class ExpenseRepository extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "expenses_table";

    public static final String COLUMN_ID = "expense_id";
    public static final String COLUMN_CATEGORY = "category"; // Required
    public static final String COLUMN_COST = "cost"; // Required
    public static final String COLUMN_DATE = "date"; // Required
    public static final String COLUMN_COMMENT = "comment"; // Optional
    public static final String COLUMN_TRIP_ID = "trip_id";

    private SQLiteDatabase database;

    private static final String DATABASE_CREATE = String.format(
            "CREATE TABLE %s (" +
                    " %s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " %s TEXT, " +
                    " %s REAL, " +
                    " %s TEXT, " +
                    " %s TEXT, " +
                    " %s INTEGER )",
            TABLE_NAME, COLUMN_ID, COLUMN_CATEGORY, COLUMN_COST,  COLUMN_DATE, COLUMN_COMMENT, COLUMN_TRIP_ID
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
        while (c.moveToNext())
        {
            if(Integer.parseInt(c.getString(5)) == trip){
                String category = c.getString(1);
                double cost = Double.parseDouble(c.getString(2));
                String date = c.getString(3);
                String comment = c.getString(4);
                int trip_id = Integer.parseInt(c.getString(5));
                Expense expense = new Expense(Integer.parseInt(c.getString(0)), category, cost, date, comment, trip_id);
                Log.w("Database", expense.toString());
                expenses.add(expense);
            }
        }
        c.close();
        db.close();
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
            e.setDate(c.getString(3));
            e.setComment(c.getString(4));
            e.setTripId(Integer.parseInt(c.getString(5)));
            Log.i("DB", e.toString());
        }
        c.close();
        db.close();
        return e;
    }

    public void addExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CATEGORY, expense.getCategory());
        cv.put(COLUMN_COST, expense.getCost());
        cv.put(COLUMN_DATE, expense.getDate());
        cv.put(COLUMN_COMMENT, expense.getComment());
        cv.put(COLUMN_TRIP_ID, expense.getTripId());
        db.insert(TABLE_NAME, COLUMN_COMMENT, cv);
        db.close();
    }

    public void deleteExpenses(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void updateExpense(int id, Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_CATEGORY, expense.getCategory().toLowerCase());
        cv.put(COLUMN_COST, expense.getCost());
        cv.put(COLUMN_DATE, expense.getDate());
        cv.put(COLUMN_COMMENT, expense.getComment());

        db.update(TABLE_NAME, cv, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }
}
