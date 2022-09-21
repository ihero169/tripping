package com.example.mexpense.services;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.mexpense.entity.Trip;
import com.example.mexpense.repository.ExpenseRepository;
import com.example.mexpense.repository.TripRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TripService {
    TripRepository repository;

    public TripService(Context context){
        repository = new TripRepository(context);
    }

    public void getTrips(MutableLiveData<List<Trip>> tripList) {
        tripList.setValue(repository.getTrips());
    }

    public void getTrip(MutableLiveData<Trip> trip, int id){
        Trip t;
        if(id == -1){
            t = new Trip();
        } else t = repository.getTripById(id);
        trip.setValue(t);
    }

    public void addTrip(Trip trip){
        repository.addTrip(trip);
    }

    public void updateTrip(int id, Trip trip){
        repository.updateTrip(id, trip);
    }

    public void updateTotal(int id, double total) {
        repository.updateTotal(id, total);
    }

    public void deleteTrip(int tripId) {
        repository.deleteTrip(tripId);
    }

    public void resetDatabase() {
        repository.deleteAll();
    }

    public void searchTripByDestination(MutableLiveData<List<Trip>> trip, String destination) {
        trip.setValue(repository.searchTripByDestination(destination));
    }
}
