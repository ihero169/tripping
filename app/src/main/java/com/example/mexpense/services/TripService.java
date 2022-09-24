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
    DateTimeFormatter android_format = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
    DateTimeFormatter sql_format = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_DATABASE);

    public TripService(Context context) {
        repository = new TripRepository(context);
    }

    public void getTrips(MutableLiveData<List<Trip>> tripList) {
        List<Trip> trips = repository.getTrips();
        for (Trip t : trips) {
            t.setStartDate(ConvertDate(t.getStartDate(), sql_format, android_format));
            t.setEndDate(ConvertDate(t.getEndDate(), sql_format, android_format));
        }
        tripList.setValue(trips);
    }

    public void getTrip(MutableLiveData<Trip> trip, int id) {
        Trip t;
        if (id == -1) {
            t = new Trip();
        } else {
            t = repository.getTripById(id);
            t.setStartDate(ConvertDate(t.getStartDate(), sql_format, android_format));
            t.setEndDate(ConvertDate(t.getEndDate(), sql_format, android_format));
        }

        trip.setValue(t);
    }

    public void addTrip(Trip trip) {
        trip.setStartDate(ConvertDate(trip.getStartDate(), android_format, sql_format));
        trip.setEndDate(ConvertDate(trip.getEndDate(), android_format, sql_format));
        repository.addTrip(trip);
    }

    public void updateTrip(int id, Trip trip) {
        trip.setStartDate(ConvertDate(trip.getStartDate(), android_format, sql_format));
        trip.setEndDate(ConvertDate(trip.getEndDate(), android_format, sql_format));
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

    public void narrowByDate(MutableLiveData<List<Trip>>  trips, String start, String end){
        if(start.equals("")){
            start = "01 Jan, 1940";
        }

        if(end.equals("")){
            end = "01 Jan, 2099";
        }

        trips.setValue(
                repository.narrowByDate(
                        ConvertDate(start, android_format, sql_format),
                        ConvertDate(end, android_format, sql_format)
                )
        );
    }

    private String ConvertDate(String source, DateTimeFormatter from, DateTimeFormatter target) {
        LocalDate date = LocalDate.parse(source, from);
        return target.format(date);
    }
}
