package com.example.mexpense.services;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.mexpense.entity.Trip;
import com.example.mexpense.repository.TripRepository;
import com.example.mexpense.ultilities.Utilities;

import java.util.List;

public class TripService {
    TripRepository repository;

    public TripService(Context context) {
        repository = new TripRepository(context);
    }

    public void getTrips(MutableLiveData<List<Trip>> tripList) {
        List<Trip> trips = repository.getTrips();
        tripList.setValue(trips);
    }

    public void getTrip(MutableLiveData<Trip> trip, int id) {
        Trip t;
        if (id == -1) {
            t = new Trip();
        } else {
            t = repository.getTripById(id);
        }

        trip.setValue(t);
    }

    public void addTrip(Trip trip) {
        trip.setStartDate(Utilities.convertDate(trip.getStartDate(), true));
        trip.setEndDate(Utilities.convertDate(trip.getEndDate(), true));
        repository.addTrip(trip);
    }

    public void updateTrip(int id, Trip trip) {
        trip.setStartDate(Utilities.convertDate(trip.getStartDate(), true));
        trip.setEndDate(Utilities.convertDate(trip.getEndDate(), true));
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

    public void searchByDestination(MutableLiveData<List<Trip>> trip, String destination) {
        trip.setValue(repository.searchByDestination(destination));
    }

    public void searchByDate(MutableLiveData<List<Trip>> trips, String start, String end) {
        if (start.equals("")) {
            start = "1940-01-01";
        } else {
            start = Utilities.convertDate(start, true);
        }

        if (end.equals("")) {
            end = "2099-01-01";
        } else {
            end = Utilities.convertDate(end, true);
        }

        trips.setValue(
                repository.narrowByDate(start, end)
        );
    }

    public void searchByType(MutableLiveData<List<Trip>> tripList, String type) {
        Log.i("Service", "Search: " + type);
        tripList.setValue(repository.searchByType(type));
    }
}
