package com.example.mexpense.fragments.main.expense;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mexpense.R;
import com.example.mexpense.adapters.ExpenseAdapter;
import com.example.mexpense.databinding.FragmentExpenseMainBinding;
import com.example.mexpense.entity.Expense;
import com.example.mexpense.services.ExpenseService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;

public class ExpenseMainFragment extends Fragment implements View.OnClickListener, ExpenseAdapter.ItemListener {

    private ExpenseMainViewModel mViewModel;
    private FragmentExpenseMainBinding binding;
    private ExpenseAdapter adapter;
    private ExpenseService service;
    private int tripId;

    public static ExpenseMainFragment newInstance() {
        return new ExpenseMainFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(ExpenseMainViewModel.class);
        binding = FragmentExpenseMainBinding.inflate(inflater, container, false);
        service = new ExpenseService(getContext());

        tripId = getArguments().getInt("tripId");

        RecyclerView rv = binding.expenseRecyclerView;
        rv.setHasFixedSize(true);

        mViewModel.expenseList.observe(
                getViewLifecycleOwner(),
                expenses -> {
                    adapter = new ExpenseAdapter(expenses, this);
                    binding.expenseRecyclerView.setAdapter(adapter);
                    binding.expenseRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                }
        );

        service.getExpenses(mViewModel.expenseList, tripId);
        setTotal(-1, -1, -1);

        FloatingActionButton btnAdd = binding.btnAdd;
        btnAdd.setOnClickListener(this);

        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAdd:
                addExpense();
            default:
                return;
        }
    }

    @Override
    public void onItemClick(int expenseId) {
        Bundle bundle = new Bundle();
        bundle.putInt("expenseId", expenseId);
        Log.d("Android", "Id: " + expenseId);
        Navigation.findNavController(getView()).navigate(R.id.expenseDetailFragment, bundle);
    }

    public void addExpense() {
        Bundle bundle = new Bundle();
        bundle.putInt("expenseId", -1);
        bundle.putInt("tripId", tripId);
        Log.d("Android", "Add new expense");
        Navigation.findNavController(getView()).navigate(R.id.expenseFormFragment, bundle);
    }

    public void setTotal(int day, int month, int year) {
        Calendar cal = Calendar.getInstance();
        double total = 0.0;
        // int month = cal.get(Calendar.MONTH);
        List<Expense> expenses = mViewModel.expenseList.getValue();
        for (int i = 0; i < expenses.size(); i++) {
            String[] date = expenses.get(i).getDate().split("/");
            if (day != -1) {
                if (Integer.parseInt(date[0]) == day) {
                    total += expenses.get(i).getCost();
                }
                else continue;
            } else if (month != -1) {
                if (Integer.parseInt(date[1]) == month + 1) {
                    total += expenses.get(i).getCost();
                }
                else continue;
            } else if (year != -1) {
                if (Integer.parseInt(date[2]) == year) {
                    total += expenses.get(i).getCost();
                }
                else continue;
            } else {
                total += expenses.get(i).getCost();
            }
            binding.textTotal.setText(Double.toString(total));
        }
    }
}