package com.example.mexpense.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.mexpense.entity.Expense;
import com.example.mexpense.ultilities.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ExpenseRepository extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "expenses_table";

    public static final String COLUMN_ID = "expense_id";
    public static final String COLUMN_NAME = "name"; // Required
    public static final String COLUMN_DESTINATION = "destination"; // Required
    public static final String COLUMN_DATE = "date"; // Required
    public static final String COLUMN_REQUIRED_ASSESSMENT = "required_assessment"; // Required
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
            TABLE_NAME, COLUMN_ID, COLUMN_NAME, COLUMN_CATEGORY, COLUMN_DESTINATION, COLUMN_DATE, COLUMN_REQUIRED_ASSESSMENT, COLUMN_DESCRIPTION, COLUMN_COST
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

    public void addExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_CATEGORY, expense.getCategory().toLowerCase());
        cv.put(COLUMN_COST, expense.getCost());
        cv.put(COLUMN_DATE, expense.getDate());
        cv.put(COLUMN_NAME, expense.getName());
        cv.put(COLUMN_DESCRIPTION, expense.getDescription());
        cv.put(COLUMN_DESTINATION, expense.getDestination());
        cv.put(COLUMN_REQUIRED_ASSESSMENT, expense.getRequiredAssessment() ? "1" : "0");

        long insert = db.insert(TABLE_NAME, COLUMN_DESCRIPTION, cv);
    }

    public void getExpenses(MutableLiveData<List<Expense>> expenseList) {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        while (c.moveToNext()) {
            String name = c.getString(1);
            String category = c.getString(2);
            String destination = c.getString(3);
            String date = c.getString(4);
            boolean assessment = Objects.equals(c.getString(5), "1");
            String description = c.getString(6);
            double cost = Double.parseDouble(c.getString(7));
            Expense expense = new Expense(Integer.parseInt(c.getString(0)), name, category, destination, date, assessment, description, cost);
            Log.w("Database", expense.toString());
            expenses.add(expense);
        }
        c.close();
        db.close();
        expenseList.setValue(expenses);
    }
}
