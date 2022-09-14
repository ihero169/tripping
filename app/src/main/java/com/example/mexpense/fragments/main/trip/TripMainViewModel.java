package com.example.mexpense.fragments.main.trip;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mexpense.entity.Trip;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TripMainViewModel extends ViewModel {
    public MutableLiveData<List<Trip>> tripList = new MutableLiveData<>();

    public void sortById(boolean reversed) {
        List<Trip> list = tripList.getValue();
        Collections.sort(list, Comparator.comparingInt(Trip::getId));
        if(reversed){
            Collections.reverse(list);
        }
        tripList.setValue(list);
    }
}