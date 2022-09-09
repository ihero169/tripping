package com.example.mexpense.entity;

public class Expense {
    private int id;
    private String name;
    private String category;
    private String destination;
    private String date;
    private boolean requiredAssessment;
    private String description;
    private double cost;

    public Expense(int id, String name, String category, String destination, String date, boolean requiredAssessment, String description, double cost) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.destination = destination;
        this.date = date;
        this.requiredAssessment = requiredAssessment;
        this.description = description;
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", destination='" + destination + '\'' +
                ", date='" + date + '\'' +
                ", requiredAssessment=" + requiredAssessment +
                ", description='" + description + '\'' +
                ", cost=" + cost +
                '}';
    }

    // Getter and Setters
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean getRequiredAssessment() {
        return requiredAssessment;
    }

    public void setRequiredAssessment(boolean requiredAssessment) {
        this.requiredAssessment = requiredAssessment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
