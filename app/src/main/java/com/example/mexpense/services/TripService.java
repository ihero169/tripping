package com.example.mexpense.services;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.mexpense.entity.Trip;
import com.example.mexpense.repository.TripRepository;
import com.example.mexpense.ultilities.Constants;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TripService {
    TripRepository repository;
    DateTimeFormatter android_formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
    DateTimeFormatter sql_formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_DATABASE);

    public TripService(Context context) {
        repository = new TripRepository(context);
    }

    public void getTrips(MutableLiveData<List<Trip>> tripList) {
        List<Trip> trips = repository.getTrips();
        for (Trip t : trips
        ) {
            ConvertDateToAndroid(t);
        }
        tripList.setValue(trips);
    }

    public void getTrip(MutableLiveData<Trip> trip, int id) {
        Trip t;
        if (id == -1) {
            t = new Trip();
        } else t = repository.getTripById(id);
        ConvertDateToAndroid(t);
        trip.setValue(t);
    }

    public void addTrip(Trip trip) {
        ConvertDateToSQL(trip);
        repository.addTrip(trip);
    }

    public void updateTrip(int id, Trip trip) {
        ConvertDateToSQL(trip);
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

    private void ConvertDateToAndroid(Trip trip) {
        LocalDate androidStartDate = LocalDate.parse(trip.getStartDate(), android_formatter);
        LocalDate androidEndDate = LocalDate.parse(trip.getEndDate(), android_formatter);

        trip.setStartDate(sql_formatter.format(androidStartDate).toString());
        trip.setEndDate(sql_formatter.format(androidEndDate).toString());
    }

    private void ConvertDateToSQL(Trip trip) {
        LocalDate sqlStartDate = LocalDate.parse(trip.getStartDate(), sql_formatter);
        LocalDate sqlEndDate = LocalDate.parse(trip.getEndDate(), sql_formatter);

        trip.setStartDate(sql_formatter.format(sqlStartDate).toString());
        trip.setEndDate(sql_formatter.format(sqlEndDate).toString());
    }
}
