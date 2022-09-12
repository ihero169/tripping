package com.example.mexpense.fragments.main.trip;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mexpense.entity.Trip;

import java.util.List;

public class TripMainViewModel extends ViewModel {
    public MutableLiveData<List<Trip>> tripList = new MutableLiveData<>();
}