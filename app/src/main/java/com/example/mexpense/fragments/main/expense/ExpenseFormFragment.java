
package com.example.mexpense.fragments.main.expense;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.mexpense.R;
import com.example.mexpense.databinding.FragmentExpenseFormBinding;
import com.example.mexpense.entity.Expense;
import com.example.mexpense.services.ExpenseService;
import com.example.mexpense.ultilities.Constants;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class ExpenseFormFragment extends Fragment implements View.OnClickListener {

    private ExpenseFormViewModel mViewModel;
    final Calendar myCalendar = Calendar.getInstance();
    private FragmentExpenseFormBinding binding;
    DatePickerDialog.OnDateSetListener date;
    private ExpenseService service;
    private int expenseId;
    private int tripId;

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

        TextInputEditText editDate = binding.inputDate;
        Button saveButton = binding.btnSaveExpense;

        saveButton.setOnClickListener(this);
        editDate.setOnClickListener(this);

        date = (datePicker, year, month, day) -> {
            setCalendar(year, month, day);
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

        AppCompatActivity app = (AppCompatActivity)getActivity();
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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater){
        inflater.inflate(R.menu.menu_trip_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                hideInput();
                Navigation.findNavController(getView()).navigate(R.id.tripMainFragment);
                return true;
            case R.id.action_delete:
                handleDelete();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Expense e = mViewModel.expense.getValue();
        menu.findItem(R.id.action_reset).setVisible(false);
        menu.findItem(R.id.action_edit).setVisible(false);
        if (e != null
                && e.getId() == Constants.NEW_EXPENSE){
            menu.findItem(R.id.action_delete).setVisible(false);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSaveExpense:
                handleSave();
                break;
            case R.id.inputDate:
                setDate();
                break;
            case R.id.inputTextCategories:
                getCategories();
            default:
                break;
        }
    }

    private void getCategories(){
        AutoCompleteTextView categoryView = binding.inputTextCategories;
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.dropdown_item, Constants.categories);
        categoryView.setAdapter(adapter);
        categoryView.setOnClickListener(this);
    }

    private void handleDelete(){
        new AlertDialog.Builder(getContext()).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirmation").setMessage("Are you sure?")
                .setPositiveButton("Yes", (arg0, arg1) -> {
                    service.deleteExpense(expenseId);
                    Bundle bundle = new Bundle();
                    bundle.putInt("tripId", tripId);
                    Log.e("Action", "Delete expense: " + expenseId);

                    hideInput();
                    Navigation.findNavController(getView()).navigate(R.id.expenseMainFragment, bundle);

                }).setNegativeButton("No", null).show();
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

                        Bundle bundle = new Bundle();
                        bundle.putInt("tripId", tripId);
                        Log.e("Trip ID:", "handleSave: " + tripId);

                        hideInput();
                        Navigation.findNavController(getView()).navigate(R.id.expenseMainFragment, bundle);

                    }).setNegativeButton("No", null).show();
        }
    }

    private boolean validation() {

        String startDate = getArguments().getString("startDate");
        String endDate = getArguments().getString("endDate");

        if (binding.inputTextCategories.getText().toString().equals("")) {
            makeToast("Please select a category");
            return false;
        }

        if (binding.inputDate.getText().toString().equals("")) {
            makeToast("Please select a date");
            return false;
        }

        if(!dateValidation(startDate, endDate, binding.inputDate.getText().toString())){
            makeToast("Expense date must be within " + startDate + " and " + endDate);
            return false;
        }

        if (binding.inputCost.getText().toString().equals("")) {
            makeToast("Please enter the expense's cost");
            return false;
        }
        return true;
    }

    private boolean dateValidation(String startStr, String endStr, String expenseStr){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
        LocalDate startDate = LocalDate.parse(startStr, formatter);
        LocalDate endDate = LocalDate.parse(endStr, formatter);
        LocalDate expenseDate = LocalDate.parse(expenseStr, formatter);
        return expenseDate.isAfter(startDate) && expenseDate.isBefore(endDate) || expenseDate.isEqual(startDate)  || expenseDate.isEqual(endDate);
    }

    private Expense getFormInput() {
        String category = binding.inputTextCategories.getText().toString();
        String date = binding.inputDate.getText().toString();
        String comment = binding.inputTextComment.getText().toString();
        double cost = Double.parseDouble(binding.inputCost.getText().toString());
        return new Expense(-1, category, cost, date, comment, tripId);
    }

    private void updateDate() {
        String format = Constants.DATE_FORMAT;
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
        binding.inputDate.setText(dateFormat.format(myCalendar.getTime()));
    }
    public void setDate() {
        if(expenseId == -1){
            DateTimeFormatter source = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
            LocalDate startDate = LocalDate.parse(getArguments().getString("startDate"), source);
            setCalendar(startDate.getYear(), startDate.getMonthValue() - 1, startDate.getDayOfMonth());
        }
        new DatePickerDialog(getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void setCalendar(int year, int month, int day){
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, month);
        myCalendar.set(Calendar.DAY_OF_MONTH, day);
    }

    private void makeToast(String toast) {
        Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();
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