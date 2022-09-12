package com.example.mexpense.entity;

import com.example.mexpense.ultilities.Constants;

public class Trip {
    private int id;
    private String name;
    private String destination;
    private String startDate;
    private String endDate;
    private boolean requiredAssessment;
    private String description;
    private double total;

    public Trip() {
        this.id = Constants.NEW_TRIP;
        this.name = "";
        this.destination = "";
        this.startDate = "";
        this.endDate = "";
        this.requiredAssessment = false;
        this.description = "";
    }

    public Trip(int id, String name, String destination, String startDate, String endDate, boolean requiredAssessment, String description) {
        this.id = id;
        this.name = name;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.requiredAssessment = requiredAssessment;
        this.description = description;
        this.total = 0;
    }

    public Trip(int id, String name, String destination, String startDate, String endDate, boolean requiredAssessment, String description, Double total) {
        this.id = id;
        this.name = name;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.requiredAssessment = requiredAssessment;
        this.description = description;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String date) {
        this.startDate = date;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public boolean getRequiredAssessment() {
        return requiredAssessment;
    }

    public void setRequiredAssessment(boolean requiredAssessment) {
        this.requiredAssessment = requiredAssessment;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
