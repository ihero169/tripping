package com.example.mexpense.fragments.main.trip;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mexpense.entity.Trip;
import com.example.mexpense.ultilities.Constants;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class TripMainViewModel extends ViewModel {
    public MutableLiveData<List<Trip>> tripList = new MutableLiveData<>();
    private List<Trip> backupTrips;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);

    {
        backupTrips = tripList.getValue();
    }

    public void sort() {
        List<Trip> list = tripList.getValue();
        Collections.reverse(list);
        tripList.setValue(list);
    }

    public void narrowByType(String type) {
        List<Trip> trips = tripList.getValue();
        for(int i = trips.size()-1; i >= 0; i--){ // Minus loop due to scaffolding
            if(!trips.get(i).getName().equals(type)){
                trips.remove(i);
            }
        }
        tripList.setValue(trips);
    }

    public void narrowByDestination(String destination){
        List<Trip> trips = tripList.getValue();
        for(int i = trips.size()-1; i >= 0; i--){ // Minus loop due to scaffolding
            if(!trips.get(i).getDestination().toLowerCase().contains(destination)){
                trips.remove(i);
            }
        }
        tripList.setValue(trips);
    }

    public void narrowByStartDate(String date) {
        if(date == "") return;
        List<Trip> trips = tripList.getValue();
        for(int i = trips.size()-1; i >= 0; i--){ // Minus loop due to scaffolding
            LocalDate start = LocalDate.parse(trips.get(i).getStartDate(), formatter);
            LocalDate tripDate = LocalDate.parse(date, formatter);
            if (tripDate.isAfter(start) && !tripDate.isEqual(start)) {
                trips.remove(i);
            }
        }
        tripList.setValue(trips);
    }

    public void narrowByEndDate(String date) {
        if(date == "") return;
        List<Trip> trips = tripList.getValue();
        for(int i = trips.size()-1; i >= 0; i--){ // Minus loop due to scaffolding
            LocalDate end = LocalDate.parse(trips.get(i).getEndDate(), formatter);
            LocalDate tripDate = LocalDate.parse(date, formatter);
            if (tripDate.isBefore(end) && !tripDate.isEqual(end)) {
                trips.remove(i);
            }
        }
        tripList.setValue(trips);
    }

}