package com.example.mexpense.fragments.main;

import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.mexpense.R;
import com.example.mexpense.databinding.FragmentExpenseFormBinding;
import com.example.mexpense.entity.Expense;
import com.example.mexpense.repository.ExpenseRepository;
import com.example.mexpense.ultilities.Constants;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ExpenseFormFragment extends Fragment implements View.OnClickListener {

    private ExpenseFormViewModel mViewModel;
    final Calendar myCalendar = Calendar.getInstance();
    private FragmentExpenseFormBinding binding;
    DatePickerDialog.OnDateSetListener date;
    ExpenseRepository repository;

    public static ExpenseFormFragment newInstance() {
        return new ExpenseFormFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(ExpenseFormViewModel.class);
        binding = FragmentExpenseFormBinding.inflate(inflater, container, false);

        AutoCompleteTextView categoryView = binding.inputTextCategories;
        repository = new ExpenseRepository(getContext());

        String[] items = new String[]{"Travel", "Flight", "Telephone", "Mortgage", "Meals", "Refreshments", "Gifts", "Medical", "Printing"};
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.dropdown_item, items);
        categoryView.setAdapter(adapter);

        TextInputEditText editDate = binding.inputTextDate;
        Button saveButton = binding.btnSave;

        saveButton.setOnClickListener(this);
        editDate.setOnClickListener(this);
        categoryView.setOnClickListener(this);

        date = (datePicker, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            updateDate();
        };

        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSave:
                if(validation()){
                    addExpense();
                }
                break;
            case R.id.inputTextDate:
                setDate();
                break;
            default:
                break;
        }
    }

    private boolean validation(){
        if(binding.inputTextName.getText().toString().equals("")){
            makeToast("Please enter the expense's name");
            return false;
        }

        if(binding.inputTextCategories.getText().toString().equals("")){
            makeToast("Please select a category");
            return false;
        }

        if(binding.inputTextDate.getText().toString().equals("")){
            makeToast("Please select a date");
            return false;
        }

        if(binding.inputTextCost.getText().toString().equals("")){
            makeToast("Please enter the expense's cost");
            return false;
        }

        if(binding.inputTextDestination.getText().toString().equals("")){
            makeToast("Please enter the destination");
            return false;
        }

        return true;
    }

    private void addExpense(){
        repository.addExpense(getFormInput());
    }

    private Expense getFormInput(){
        String name = binding.inputTextName.getText().toString();
        String category = binding.inputTextCategories.getText().toString();
        String destination = binding.inputTextDestination.getText().toString();
        String date = binding.inputTextDate.getText().toString();
        boolean assessment = binding.switchRequiredAssessment.isChecked();
        String description = binding.inputTextDescription.getText().toString();
        double cost = Double.parseDouble(binding.inputTextCost.getText().toString());
        return new Expense( -1, name, category, destination, date, assessment,description, cost);
    }

    private void updateDate(){
        String format = "dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        binding.inputTextDate.setText(dateFormat.format(myCalendar.getTime()));
    }

    public void setDate() {
        new DatePickerDialog(getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void makeToast(String toast){
        Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();
    }
}