package com.example.mexpense.fragments.main.trip;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mexpense.R;
import com.example.mexpense.databinding.FragmentTripFormBinding;
import com.example.mexpense.entity.Trip;
import com.example.mexpense.services.TripService;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TripFormFragment extends Fragment implements View.OnClickListener  {

    private TripFormViewModel mViewModel;
    private FragmentTripFormBinding binding;

    final Calendar myCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener startDate;
    private DatePickerDialog.OnDateSetListener endDate;

    private TripService service;

    int tripId;

    public static TripFormFragment newInstance() {
        return new TripFormFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentTripFormBinding.inflate(inflater, container, false);
        service = new TripService(getContext());

        try {
            tripId = getArguments().getInt("tripId");
        } catch (Exception e) {
            tripId = -1;
        }

        TextInputEditText editStartDate = binding.inputStartDate;
        editStartDate.setOnClickListener(this);
        startDate = (datePicker, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            updateDate(binding.inputStartDate);
        };

        TextInputEditText editEndDate = binding.inputEndDate;
        editEndDate.setOnClickListener(this);
        endDate = (datePicker, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            updateDate(binding.inputEndDate);
        };

        Button btnSaveTrip = binding.btnSaveTrip;
        btnSaveTrip.setOnClickListener(this);

        return binding.getRoot();
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
            case R.id.btnSaveTrip:
                handleSave();
            default:
                break;
        }
    }

    private void handleSave(){
        if (validation()) {
            new AlertDialog.Builder(getContext()).setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Confirmation").setMessage("Are you sure?")
                    .setPositiveButton("Yes", (arg0, arg1) -> {
                        if (tripId == -1) {
                            service.addTrip(getFormInput());
                        } else {
                            service.updateTrip(tripId, getFormInput());
                        }
                        Navigation.findNavController(getView()).navigate(R.id.tripMainFragment);
                    }).setNegativeButton("No", null).show();
        }
    }

    private boolean validation(){
        if (binding.inputTextName.getText().toString().equals("")) {
            makeToast("Please enter the trip's name");
            return false;
        }

        if (binding.inputTextDestination.getText().toString().equals("")) {
            makeToast("Please enter the destination");
            return false;
        }

        if (binding.inputStartDate.getText().toString().equals("")) {
            makeToast("Please select the start date");
            return false;
        }

        if (binding.inputEndDate.getText().toString().equals("")) {
            makeToast("Please enter the end date");
            return false;
        }

        return true;
    }

    private Trip getFormInput(){
        String name = binding.inputTextName.getText().toString();
        String destination = binding.inputTextDestination.getText().toString();
        String startDate = binding.inputStartDate.getText().toString();
        String endDate = binding.inputEndDate.getText().toString();
        boolean assessment = binding.switchRequiredAssessment.isChecked();
        String description = binding.inputTextDescription.getText().toString();
        return new Trip(-1, name, destination, startDate, endDate, assessment, description);
    }

    private void updateDate(TextView dateView) {
        String format = "dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
        dateView.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void makeToast(String toast) {
        Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();
    }
}