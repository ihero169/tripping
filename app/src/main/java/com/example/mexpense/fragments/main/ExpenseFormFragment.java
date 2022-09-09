package com.example.mexpense.fragments.main;

import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.mexpense.R;
import com.example.mexpense.databinding.FragmentExpenseFormBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ExpenseFormFragment extends Fragment implements View.OnClickListener {

    private ExpenseFormViewModel mViewModel;
    final Calendar myCalendar = Calendar.getInstance();
    private FragmentExpenseFormBinding binding;
    DatePickerDialog.OnDateSetListener date;

    public static ExpenseFormFragment newInstance() {
        return new ExpenseFormFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(ExpenseFormViewModel.class);
        binding = FragmentExpenseFormBinding.inflate(inflater, container, false);

        AutoCompleteTextView categoryView = binding.autoCompleteTextview;

        String[] items = new String[]{"Travel", "Flight", "Telephone", "Mortgage", "Meals", "Refreshments", "Gifts", "Medical Expenses", "Printing"};
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.dropdown_item, items);

        categoryView.setAdapter(adapter);

        TextInputEditText editDate = binding.inputTextDate;

        editDate.setOnClickListener(this);

        categoryView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                echoCategory();
            }
        });

        date = (datePicker, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            updateDate();
        };

        return binding.getRoot();
    }

//    private Expense getFormInput(){
//        Optional<String> optional = mViewModel.expense.getValue.getId();
//        int id;
//        if(!optional.isPresent()) {
//            id = Constants.NEW_EXPENSE;
//        } else id = Integer.parseInt(optional.toString());
//
//        String name = binding.editTextName.getText().toString();
//        String category = binding.editTextCategory.getText().toString();
//        String destination = binding.editTextDestination.getText().toString();
//        String date = binding.editTextDate.getText().toString();
//        boolean assessment = binding.switchRequiredAssessment.getShowText();
//        String description = binding.editTextDescription.getText().toString();
//        double cost = Double.parseDouble(binding.editTextCost.getText().toString());
//        return new Expense(id, name, category, destination, date, assessment, description, cost);
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.inputTextDate:
                setDate();
                break;
            case R.id.autoCompleteTextview:
                echoCategory();
            default:
                break;
        }
    }

    private void echoCategory(){
        AutoCompleteTextView categoryView = binding.autoCompleteTextview;
        Toast.makeText(getContext(), categoryView.getText().toString(), Toast.LENGTH_SHORT).show();
    }

    private void updateDate(){
        String format = "dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        binding.inputTextDate.setText(dateFormat.format(myCalendar.getTime()));
    }

    public void setDate() {
        new DatePickerDialog(getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}