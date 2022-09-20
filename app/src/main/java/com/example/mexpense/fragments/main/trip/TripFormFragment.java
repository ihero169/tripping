package com.example.mexpense.fragments.main.trip;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.mexpense.R;
import com.example.mexpense.databinding.FragmentTripFormBinding;
import com.example.mexpense.entity.Trip;
import com.example.mexpense.services.TripService;
import com.example.mexpense.ultilities.Constants;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class TripFormFragment extends Fragment implements View.OnClickListener {

    private TripFormViewModel mViewModel;
    private FragmentTripFormBinding binding;

    final Calendar myCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener startDate;
    private DatePickerDialog.OnDateSetListener endDate;

    private TextInputEditText editStartDate;
    private TextInputEditText editEndDate;
    private AutoCompleteTextView editTripType;
    private TextInputEditText editDestination;
    private TextInputEditText editDescription;
    private TextInputEditText editParticipant;
    private Switch assessment;

    private TripService service;

    int tripId;

    public static TripFormFragment newInstance() {
        return new TripFormFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(TripFormViewModel.class);
        binding = FragmentTripFormBinding.inflate(inflater, container, false);
        service = new TripService(getContext());

        try {
            tripId = getArguments().getInt("tripId");
        } catch (Exception e) {
            tripId = -1;
        }

        editTripType = binding.inputTextTripType;
        editDestination = binding.inputTextDescription;
        editDescription = binding.inputTextDescription;
        editParticipant = binding.inputTextParticipant;
        assessment = binding.switchRequiredAssessment;

        editStartDate = binding.inputStartDate;
        editStartDate.setOnClickListener(this);
        startDate = (datePicker, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            updateDate(binding.inputStartDate);
        };

        editEndDate = binding.inputEndDate;
        editEndDate.setOnClickListener(this);
        endDate = (datePicker, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            updateDate(binding.inputEndDate);
        };

        Button btnSaveTrip = binding.btnSaveTrip;
        btnSaveTrip.setOnClickListener(this);

        mViewModel.trip.observe(
                getViewLifecycleOwner(),
                trip -> {
                    binding.inputTextTripType.setText(trip.getName());
                    getTrips();
                    binding.inputStartDate.setText(trip.getStartDate());
                    binding.inputEndDate.setText(trip.getEndDate());
                    binding.inputTextDestination.setText(trip.getDestination());
                    binding.inputTextDescription.setText(trip.getDestination());
                    binding.inputTextParticipant.setText(String.valueOf(trip.getParticipants()));
                    binding.switchRequiredAssessment.setChecked(trip.getRequiredAssessment());
                }
        );
        service.getTrip(mViewModel.trip, tripId);

        AppCompatActivity app = (AppCompatActivity) getActivity();
        ActionBar ab = app.getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_home);
        setHasOptionsMenu(true);

        requireActivity().invalidateOptionsMenu();

        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_trip_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                hideInput();
                Navigation.findNavController(getView()).navigate(R.id.tripMainFragment);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Trip t = mViewModel.trip.getValue();
        menu.findItem(R.id.action_reset).setVisible(false);
        if (t != null
                && t.getId() == Constants.NEW_EXPENSE) {
            menu.findItem(R.id.action_delete).setVisible(false);
            menu.findItem(R.id.action_edit).setVisible(true);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.inputStartDate:
                new DatePickerDialog(getContext(), startDate, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.inputEndDate:
                new DatePickerDialog(getContext(), endDate, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.inputTextTripType:
                getTrips();
                return;
            case R.id.btnSaveTrip:
                handleSave();
            default:
                break;
        }
    }

    private void handleSave() {
        if (validation()) {
            new AlertDialog.Builder(getContext()).setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Confirmation").setMessage("Are you sure?")
                    .setPositiveButton("Yes", (arg0, arg1) -> {
                        if (tripId == -1) {
                            service.addTrip(getFormInput());
                        } else {
                            service.updateTrip(tripId, getFormInput());
                        }
                        hideInput();
                        Navigation.findNavController(getView()).navigate(R.id.tripMainFragment);
                    }).setNegativeButton("No", null).show();
        }
    }

    private boolean validation() {
        boolean result = true;
        if (editTripType.getText().toString().equals("")) {
            binding.layoutTripType.setError("Please select the trip type");
            result = false;
        } else {
            binding.layoutTripType.setError(null);
        }

        if (editDestination.getText().toString().equals("")) {
            binding.layoutDestination.setError("Please enter a destination");
            result = false;
        } else {
            binding.layoutDestination.setError(null);
        }

        if (editStartDate.getText().toString().equals("")) {
            binding.layoutStartDate.setError("Start date missing");
            result = false;
        } else {
            binding.layoutStartDate.setError(null);
        }

        if (editEndDate.getText().toString().equals("")) {
            binding.layoutEndDate.setError("End date missing");
            result = false;
        }  else {
            binding.layoutEndDate.setError(null);
        }

        if(!editEndDate.getText().toString().equals("") && !editStartDate.getText().toString().equals("")){
            if (!dateValidation(binding.inputStartDate.getText().toString(), binding.inputEndDate.getText().toString())) {
                binding.layoutStartDate.setError("Invalid start date");
                result = false;
            } else {
                binding.layoutStartDate.setError(null);
            }
        }

        if (Integer.parseInt(editParticipant.getText().toString()) == 0) {
            binding.layoutParticipants.setError("Please enter number of participants");
            result = false;
        } else {
            binding.layoutParticipants.setError(null);
        }

        return result;
    }

    private boolean dateValidation(String startStr, String endStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
        LocalDate startDate = LocalDate.parse(startStr, formatter);
        LocalDate endDate = LocalDate.parse(endStr, formatter);
        return endDate.isAfter(startDate) || endDate.isEqual(startDate);
    }

    private Trip getFormInput() {
        String name = editTripType.getText().toString();
        String destination = editDestination.getText().toString();
        String startDate = editStartDate.getText().toString();
        String endDate = editEndDate.getText().toString();
        boolean a = assessment.isChecked();
        String description = editDescription.getText().toString();
        int participants = Integer.parseInt(editParticipant.getText().toString());
        return new Trip(-1, name, destination, startDate, endDate, a, participants, description);
    }

    private void updateDate(TextView dateView) {
        String format = Constants.DATE_FORMAT;
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
        dateView.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void makeToast(String toast) {
        Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();
    }

    private void getTrips() {
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.dropdown_item, Constants.trips);
        editTripType.setAdapter(adapter);
        editTripType.setOnClickListener(this);
    }

    private void hideInput() {
        InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getView();
        if (view == null) {
            view = new View(getActivity());
        }
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}