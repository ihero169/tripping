package com.example.mexpense.fragments.main.expense;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mexpense.entity.Expense;

public class ExpenseDetailViewModel extends ViewModel {
    MutableLiveData<Expense> expense = new MutableLiveData<>();
}