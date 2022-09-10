package com.example.mexpense.fragments.main;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mexpense.entity.Expense;
import com.example.mexpense.repository.ExpenseRepository;

import java.util.ArrayList;
import java.util.List;

public class ExpenseMainViewModel extends ViewModel {
    public MutableLiveData<List<Expense>> expenseList = new MutableLiveData<List<Expense>>();
    ExpenseRepository repository;

    {
        repository = new ExpenseRepository();
    }

    public void getExpenses(){
        repository.getExpenses(expenseList);
    }
}