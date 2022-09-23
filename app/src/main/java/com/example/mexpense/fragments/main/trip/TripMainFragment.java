package com.example.mexpense.fragments.main.trip;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.mexpense.R;
import com.example.mexpense.adapters.TripAdapter;
import com.example.mexpense.databinding.FragmentTripMainBinding;
import com.example.mexpense.entity.Trip;
import com.example.mexpense.services.TripService;
import com.example.mexpense.ultilities.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TripMainFragment extends Fragment implements View.OnClickListener, TripAdapter.TripItemListener {

    private TripMainViewModel mViewModel;
    private FragmentTripMainBinding binding;
    private TripAdapter adapter;
    private TripService service;

    private DatePickerDialog.OnDateSetListener startDate;
    private DatePickerDialog.OnDateSetListener endDate;

    final Calendar myCalendar = Calendar.getInstance();

    private TextInputEditText editStartDate;
    private TextInputEditText editEndDate;
    private TextInputEditText editDestination;

    private AutoCompleteTextView sortTripType;

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

        Button sortBtn = binding.btnSortId;
        sortBtn.setOnClickListener(this);

        Button resetBtn = binding.btnReset;
        resetBtn.setOnClickListener(this);


        editStartDate = binding.inputStartDate;
        editStartDate.setOnClickListener(this);
        editStartDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                service.getTrips(mViewModel.tripList);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                sortDate();
            }
        });

        startDate = (datePicker, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            updateDate(binding.inputStartDate);
        };

        editEndDate = binding.inputEndDate;
        editEndDate.setOnClickListener(this);
        editEndDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                service.getTrips(mViewModel.tripList);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                sortDate();
            }
        });

        endDate = (datePicker, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            updateDate(binding.inputEndDate);
        };

        sortTripType = binding.inputSortTrip;
        sortTripType.setOnClickListener(this);
        getTrips();
        sortTripType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                service.getTrips(mViewModel.tripList);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String type = sortTripType.getText().toString();
                if(type == ""){
                    mViewModel.narrowByType(type);
                }
            }
        });

        editDestination = binding.editDestination;
        editDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                service.getTrips(mViewModel.tripList);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String destination = editDestination.getText().toString();
                if(destination != ""){
                    mViewModel.narrowByDestination(destination);
                }
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
                break;
            case R.id.btnSortId:
                mViewModel.sort();
                break;
            case R.id.inputStartDate:
                new DatePickerDialog(getContext(), startDate, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.inputEndDate:
                new DatePickerDialog(getContext(), endDate, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.inputSortTrip:
                getTrips();
                break;
            case R.id.btnReset:
                resetNarrowDown();
            default:
                break;
        }
    }

    @Override
    public void onItemClick(int tripId) {
        Bundle bundle = new Bundle();
        bundle.putInt("tripId", tripId);
        Navigation.findNavController(getView()).navigate(R.id.expenseMainFragment, bundle);
    }

    private void sortDate(){
        String startDate = editStartDate.getText().toString();
        String endDate = editEndDate.getText().toString();

        if(!startDate.equals("") && startDate != null && !endDate.equals("") && endDate != null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
            LocalDate start = LocalDate.parse(startDate, formatter);
            LocalDate end = LocalDate.parse(endDate, formatter);

            Log.e("DATE CHECKING", "sortDate: " + start.isBefore(end));
            if(start.isAfter(end)){
                binding.layoutStartDateSort.setError("Invalid start date");
                return;
            } else {
                binding.layoutStartDateSort.setError(null);
            }
        }


        if(startDate.equals("") || startDate == null){
            return;
        } else mViewModel.narrowByStartDate(startDate);

        if(endDate.equals("") || endDate == null){
            return;
        } else mViewModel.narrowByEndDate(endDate);
    }

    private void addTrip(){
        Bundle bundle = new Bundle();
        bundle.putInt("tripId", -1);
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

    private void updateDate(TextView dateView) {
        String format = Constants.DATE_FORMAT;
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
        dateView.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void getTrips() {
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.dropdown_item, Constants.trips);
        sortTripType.setAdapter(adapter);
        sortTripType.setOnClickListener(this);
    }

    private void resetNarrowDown(){
        sortTripType.setText("");
        editStartDate.setText("");
        editEndDate.setText("");
        editDestination.setText("");
        binding.layoutStartDateSort.setError(null);
        hideInput();
        service.getTrips(mViewModel.tripList);
    }

    private void hideInput(){
        InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getView();
        if(view == null){
            view = new View(getActivity());
        }
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}