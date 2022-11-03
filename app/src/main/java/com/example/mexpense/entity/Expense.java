package com.example.mexpense.entity;

import androidx.annotation.NonNull;

import com.example.mexpense.ultilities.Constants;
import com.google.gson.Gson;

public class Expense {
    private int id;
    private String category;
    private String date;
    private double cost;
    private int amount;
    private String comment;
    private int tripId;
    private double latitude;
    private double longitude;
    private String imagePath;

    public Expense(int id, String category, double cost, int amount, String date, String comment, int tripId, double latitude, double longitude, String imagePath) {
        this.id = id;
        this.category = category;
        this.date = date;
        this.cost = cost;
        this.amount = amount;
        this.comment = comment;
        this.tripId = tripId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imagePath = imagePath;
    }

    public Expense() {
        id = Constants.NEW_EXPENSE;
        category = "";
        date = "";
        cost = 0;
        amount = 0;
        comment = "";
        latitude = 0.0;
        longitude = 0.0;
        imagePath = "";
    }

    public String toJson() {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return json;
    }

    public Expense fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Expense.class);
    }

    @NonNull
    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", date='" + date + '\'' +
                ", cost=" + cost +
                ", amount=" + amount +
                ", comment='" + comment + '\'' +
                ", tripId=" + tripId +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    // Getter and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getImage() {
        return imagePath;
    }

    public void setImage(String imagePath) {
        this.imagePath = imagePath;
    }
}
