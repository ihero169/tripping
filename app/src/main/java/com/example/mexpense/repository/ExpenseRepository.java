package com.example.mexpense.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mexpense.entity.Expense;
import com.example.mexpense.services.SqlService;
import com.example.mexpense.ultilities.Constants;

import java.util.ArrayList;
import java.util.List;

public class ExpenseRepository extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "expenses_table";

    private SQLiteDatabase database;

    public ExpenseRepository(Context context) {
        super(context, "mExpense", null, 2);
        SqlService sqlService = new SqlService(context);
        database = sqlService.getDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        this.database = sqLiteDatabase;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        onCreate(database);
    }

    public List<Expense> getExpenses() {
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
            String imagePath = c.getString(9);
            Expense expense = new Expense(Integer.parseInt(c.getString(0)), category, cost, amount, date, comment, trip_id, latitude, longitude, imagePath);
            expenses.add(expense);
        }
        c.close();
        return expenses;
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
                String imagePath = c.getString(9);
                Expense expense = new Expense(Integer.parseInt(c.getString(0)), category, cost, amount, date, comment, trip_id, latitude, longitude, imagePath);
                expenses.add(expense);
            }
        }
        c.close();
        return expenses;
    }

    public Expense getExpenseById(int id) {
        Expense e = new Expense();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + Constants.COLUMN_ID_EXPENSE + "=" + id, null);
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
            e.setImage(c.getString(9));
        }
        c.close();
        return e;
    }

    public void addExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Constants.COLUMN_CATEGORY_EXPENSE, expense.getCategory());
        cv.put(Constants.COLUMN_COST_EXPENSE, expense.getCost());
        cv.put(Constants.COLUMN_AMOUNT_EXPENSE, expense.getAmount());
        cv.put(Constants.COLUMN_DATE_EXPENSE, expense.getDate());
        cv.put(Constants.COLUMN_COMMENT_EXPENSE, expense.getComment());
        cv.put(Constants.COLUMN_TRIP_ID_EXPENSE, expense.getTripId());
        cv.put(Constants.COLUMN_LATITUDE_EXPENSE, expense.getLatitude());
        cv.put(Constants.COLUMN_LONGITUDE_EXPENSE, expense.getLongitude());
        cv.put(Constants.COLUMN_IMAGE_PATH_EXPENSE, expense.getImage());
        db.insert(TABLE_NAME, Constants.COLUMN_COMMENT_EXPENSE, cv);
    }

    public void deleteExpenses(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, Constants.COLUMN_ID_EXPENSE + "=?", new String[]{String.valueOf(id)});
    }

    public void updateExpense(int id, Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(Constants.COLUMN_CATEGORY_EXPENSE, expense.getCategory());
        cv.put(Constants.COLUMN_COST_EXPENSE, expense.getCost());
        cv.put(Constants.COLUMN_AMOUNT_EXPENSE, expense.getAmount());
        cv.put(Constants.COLUMN_DATE_EXPENSE, expense.getDate());
        cv.put(Constants.COLUMN_COMMENT_EXPENSE, expense.getComment());
        cv.put(Constants.COLUMN_IMAGE_PATH_EXPENSE, expense.getImage());

        db.update(TABLE_NAME, cv, Constants.COLUMN_ID_EXPENSE + "=?", new String[]{String.valueOf(id)});
    }


}
