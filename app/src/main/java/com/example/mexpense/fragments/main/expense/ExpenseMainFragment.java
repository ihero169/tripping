package com.example.mexpense.fragments.main.expense;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.example.mexpense.services.TripService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;

public class ExpenseMainFragment extends Fragment implements View.OnClickListener, ExpenseAdapter.ItemListener {

    private ExpenseMainViewModel mViewModel;
    private FragmentExpenseMainBinding binding;
    private ExpenseAdapter adapter;
    private ExpenseService expenseService;
    private TripService tripService;
    private int tripId;

    public static ExpenseMainFragment newInstance() {
        return new ExpenseMainFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(ExpenseMainViewModel.class);
        binding = FragmentExpenseMainBinding.inflate(inflater, container, false);

        expenseService = new ExpenseService(getContext());
        tripService = new TripService(getContext());

        tripId = getArguments().getInt("tripId");
        Log.i("Trip", "onCreateView: " + tripId);

//        TextView name = binding.textExpenseTripName;
//        name.setOnClickListener(this);

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

        mViewModel.trip.observe(
                getViewLifecycleOwner(),
                trip -> {
                    binding.textExpenseTripName.setText(trip.getName());
                    binding.textTripStartDate.setText(trip.getStartDate());
                    binding.textTripEndDate.setText(trip.getEndDate());
                    binding.textExpenseTripDestination.setText(trip.getDestination());
                }
        );

        expenseService.getExpenses(mViewModel.expenseList, tripId);
        tripService.getTrip(mViewModel.trip, tripId);
        tripService.updateTotal(tripId, getTotal());

        FloatingActionButton btnAdd = binding.btnAdd;
        btnAdd.setOnClickListener(this);

        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAdd:
                addExpense();
            case R.id.textExpenseTripName:
                Navigation.findNavController(getView()).navigate(R.id.expenseMainFragment);
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

    public double getTotal() {
        double total = 0.0;
        List<Expense> expenses = mViewModel.expenseList.getValue();
        for (int i = 0; i < (expenses != null ? expenses.size() : 0); i++) {
            total += expenses.get(i).getCost();
            binding.textTotal.setText(Double.toString(total));
        }
        return total;
    }
}