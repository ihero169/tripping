package com.example.mexpense.fragments.main.expense;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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

        try{
            tripId = getArguments().getInt("tripId");
            Log.i("Trip", "onCreateView: " + tripId);
        } catch (Exception e){
            Log.e("Trip", "Failed to get tripId");
            Log.e("Trip", e.toString());
        }

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
                    binding.textParticipants.setText("Number of Participant: " + trip.getParticipants());
                    binding.textExpenseTripName.setText(trip.getName());
                    binding.textTripStartDate.setText(trip.getStartDate());
                    binding.textTripEndDate.setText(trip.getEndDate());
                    binding.textExpenseTripDestination.setText(trip.getDestination());
                    binding.textRequiredAssessment.setText(trip.getRequiredAssessment() ? "Assessment Required " : "Assessment Not Required");
                }
        );

        expenseService.getExpenses(mViewModel.expenseList, tripId);
        tripService.getTrip(mViewModel.trip, tripId);
        tripService.updateTotal(tripId, getTotal());

        FloatingActionButton btnAdd = binding.btnAddExpense;
        btnAdd.setOnClickListener(this);

        AppCompatActivity app = (AppCompatActivity)getActivity();
        ActionBar ab = app.getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_home);
        setHasOptionsMenu(true);

        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater){
        inflater.inflate(R.menu.menu_trip_fragment, menu);
        menu.findItem(R.id.action_delete).setVisible(true);
        menu.findItem(R.id.action_edit).setVisible(true);
        menu.findItem(R.id.action_reset).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Navigation.findNavController(getView()).navigate(R.id.tripMainFragment);
                return true;
            case R.id.action_delete:
                handleDelete();
                return true;
            case R.id.action_edit:
                handleEdit();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddExpense:
                addExpense();
            default:
                return;
        }
    }

    @Override
    public void onItemClick(int expenseId) {
        Bundle bundle = getBundle(expenseId);
        Log.d("Android", "Id: " + expenseId);
        Navigation.findNavController(getView()).navigate(R.id.expenseFormFragment, bundle);
    }

    public void addExpense() {
        Bundle bundle = getBundle(-1);
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

    private Bundle getBundle(int expenseId){
        Bundle bundle = new Bundle();
        bundle.putInt("expenseId", expenseId);
        bundle.putInt("tripId", tripId);
        bundle.putString("startDate", binding.textTripStartDate.getText().toString());
        bundle.putString("endDate", binding.textTripEndDate.getText().toString());
        return bundle;
    }

    private void handleDelete(){
        new AlertDialog.Builder(getContext()).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirmation").setMessage("Are you sure?")
                .setPositiveButton("Yes", (arg0, arg1) -> {
                    tripService.deleteTrip(tripId);
                    Bundle bundle = new Bundle();
                    Log.e("Action", "Delete trip: " + tripId);
                    Navigation.findNavController(getView()).navigate(R.id.tripMainFragment, bundle);
                }).setNegativeButton("No", null).show();
    }

    private void handleEdit(){
        Bundle bundle = getBundle(tripId);
        Log.d("Android", "Id: " + tripId);
        Navigation.findNavController(getView()).navigate(R.id.tripFormFragment, bundle);
    }
}