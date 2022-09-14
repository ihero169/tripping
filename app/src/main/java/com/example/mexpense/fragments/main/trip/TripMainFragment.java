package com.example.mexpense.fragments.main.trip;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import com.example.mexpense.R;
import com.example.mexpense.adapters.TripAdapter;
import com.example.mexpense.databinding.FragmentTripMainBinding;
import com.example.mexpense.entity.Trip;
import com.example.mexpense.services.TripService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TripMainFragment extends Fragment implements View.OnClickListener, TripAdapter.TripItemListener {

    private TripMainViewModel mViewModel;
    private FragmentTripMainBinding binding;
    private TripAdapter adapter;
    private TripService service;
    boolean sortAsc = true;

    public static TripMainFragment newInstance() {
        return new TripMainFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(TripMainViewModel.class);
        binding = FragmentTripMainBinding.inflate(inflater, container, false);
        service = new TripService(getContext());

        RecyclerView rv = binding.tripRecyclerView;
        rv.setHasFixedSize(true);
        FloatingActionButton addButton = binding.btnAddTrip;
        addButton.setOnClickListener(this);

        mViewModel.tripList.observe(
                getViewLifecycleOwner(),
                trips -> {
                    adapter = new TripAdapter(trips, this);
                    binding.tripRecyclerView.setAdapter(adapter);
                    binding.tripRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                }
        );
        service.getTrips(mViewModel.tripList);

        Button sortBtn = binding.btnSort;
        sortBtn.setOnClickListener(this);

        SearchView searchView = binding.tripSearchView;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchTrip(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchTrip(s);
                return true;
            }

            public void searchTrip(String s){
                service.searchTripByName(mViewModel.tripList, s);
            }
        });

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
        menu.findItem(R.id.action_delete).setVisible(false);
        menu.findItem(R.id.action_edit).setVisible(false);
        menu.findItem(R.id.action_reset).setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Navigation.findNavController(getView()).navigate(R.id.tripMainFragment);
                return true;
            case R.id.action_reset:
                resetDatabase();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddTrip:
                addTrip();
            case R.id.btnSort:
                mViewModel.sortById(sortAsc);
                sortAsc = !sortAsc;
            default:
                return;
        }
    }

    @Override
    public void onItemClick(int tripId) {
        Bundle bundle = new Bundle();
        bundle.putInt("tripId", tripId);
        Log.d("Android", "Id: " + tripId);
        Navigation.findNavController(getView()).navigate(R.id.expenseMainFragment, bundle);
    }

    private void addTrip(){
        Bundle bundle = new Bundle();
        bundle.putInt("tripId", -1);
        Log.d("Android", "Add new trip");
        Navigation.findNavController(getView()).navigate(R.id.tripFormFragment, bundle);
    }

    private void resetDatabase() {
        new AlertDialog.Builder(getContext()).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirmation").setMessage("Are you sure?")
                .setPositiveButton("Yes", (arg0, arg1) -> {
                    service.resetDatabase();
                    List<Trip> newList = new ArrayList<>();
                    mViewModel.tripList.setValue(newList);
                    Log.i("Database", "Database wiped");
                }).setNegativeButton("No", null).show();

    }
}