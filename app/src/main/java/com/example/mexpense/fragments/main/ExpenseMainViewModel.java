package com.example.mexpense.fragments.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mexpense.entity.Expense;

import java.util.ArrayList;
import java.util.List;

public class ExpenseMainViewModel extends ViewModel {
    public MutableLiveData<List<Expense>> expenseList = new MutableLiveData<List<Expense>>();

    {
        // Expense(int id, String name, String category, String destination, String date, boolean requiredAssessment, String description, float cost)

        List<Expense> temp = new ArrayList<>();
        temp.add(new Expense(1, "Taxi", "taxi", "Tran Van Bach", "Sep 09, 2022", false, "None", 1.23));
        temp.add(new Expense(2, "Meeting with FPT", "flight", "FPT Hoa Lac", "Sep 09, 2022", false, "None", 4.23));
        temp.add(new Expense(3, "Lunch with Mr. Bach", "meals", "Lang, Ha Noi", "Jul 08, 2022", false, "None", 3.23));
        temp.add(new Expense(4, "Coffee with Mr. Tung", "refreshments", "Tran Van Bach", "Aug 09, 2022", false, "None", 2.23));
        expenseList.setValue(temp);
    }
}