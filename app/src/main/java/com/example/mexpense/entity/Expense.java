package com.example.mexpense.entity;

import com.example.mexpense.ultilities.Constants;


public class Expense {
    private int id;
    private String category;
    private String date;
    private double cost;
    private String comment;
    private int tripId;
    private double latitude;
    private double longitude;

    public Expense(int id, String category, double cost, String date, String comment, int tripId, double latitude, double longitude) {
        this.id = id;
        this.category = category;
        this.date = date;
        this.cost = cost;
        this.comment = comment;
        this.tripId = tripId;
    }

    public Expense() {
        id = Constants.NEW_EXPENSE;
        category = "";
        date = "";
        cost = 0;
        comment = "";
        latitude = 0.0;
        longitude = 0.0;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", category='" + category + '\'' +
                ", date='" + date + '\'' +
                ", cost=" + cost +
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
}
