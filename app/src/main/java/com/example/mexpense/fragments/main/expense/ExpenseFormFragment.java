
package com.example.mexpense.fragments.main.expense;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.mexpense.R;
import com.example.mexpense.databinding.FragmentExpenseFormBinding;
import com.example.mexpense.entity.Expense;
import com.example.mexpense.services.ExpenseService;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ExpenseFormFragment extends Fragment implements View.OnClickListener {

    private ExpenseFormViewModel mViewModel;
    final Calendar myCalendar = Calendar.getInstance();
    private FragmentExpenseFormBinding binding;
    DatePickerDialog.OnDateSetListener date;
    private ExpenseService service;
    int expenseId;
    int tripId;

    public static ExpenseFormFragment newInstance() {
        return new ExpenseFormFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(ExpenseFormViewModel.class);
        binding = FragmentExpenseFormBinding.inflate(inflater, container, false);
        service = new ExpenseService(getContext());
        try {
            expenseId = getArguments().getInt("expenseId");
        } catch (Exception e) {
            expenseId = -1;
        }
        tripId = getArguments().getInt("tripId");

        getCategories();
        TextInputEditText editDate = binding.inputDate;
        Button saveButton = binding.btnSaveExpense;

        saveButton.setOnClickListener(this);
        editDate.setOnClickListener(this);

        date = (datePicker, year, month, day) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, day);
            updateDate();
        };

        mViewModel.expense.observe(
                getViewLifecycleOwner(),
                expense -> {
                    binding.inputTextCategories.setText(expense.getCategory());
                    getCategories();
                    binding.inputDate.setText(expense.getDate());
                    binding.inputCost.setText(String.valueOf(expense.getCost()));
                }
        );
        service.getExpenseById(mViewModel.expense, expenseId);

        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSaveTrip:
                handleSave();
                break;
            case R.id.inputDate:
                setDate();
                break;
            default:
                break;
        }
    }

    private void getCategories(){
        AutoCompleteTextView categoryView = binding.inputTextCategories;
        String[] items = new String[]{"Travel", "Flight", "Telephone", "Mortgage", "Meals", "Refreshments", "Gifts", "Medical", "Printing"};
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.dropdown_item, items);
        categoryView.setAdapter(adapter);
        categoryView.setOnClickListener(this);
    }

    private void handleSave() {
        if (validation()) {
            new AlertDialog.Builder(getContext()).setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Confirmation").setMessage("Are you sure?")
                    .setPositiveButton("Yes", (arg0, arg1) -> {
                        if (expenseId == -1) {
                            service.addExpense(getFormInput());
                        } else {
                            service.updateExpense(expenseId, getFormInput());
                        }
                        Navigation.findNavController(getView()).navigate(R.id.expenseMainFragment);
                    }).setNegativeButton("No", null).show();
        }
    }

    private boolean validation() {
        if (binding.inputTextCategories.getText().toString().equals("")) {
            makeToast("Please select a category");
            return false;
        }

        if (binding.inputDate.getText().toString().equals("")) {
            makeToast("Please select a date");
            return false;
        }

        if (binding.inputCost.getText().toString().equals("")) {
            makeToast("Please enter the expense's cost");
            return false;
        }

        return true;
    }

    private Expense getFormInput() {
        String category = binding.inputTextCategories.getText().toString();
        String date = binding.inputDate.getText().toString();
        String comment = binding.inputTextComment.getText().toString();
        double cost = Double.parseDouble(binding.inputCost.getText().toString());
        return new Expense(-1, category, cost, date, comment, tripId);
    }

    private void updateDate() {
        String format = "dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
        binding.inputDate.setText(dateFormat.format(myCalendar.getTime()));
    }

    public void setDate() {
        new DatePickerDialog(getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void makeToast(String toast) {
        Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();
    }
}