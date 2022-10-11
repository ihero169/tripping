
package com.example.mexpense.fragments.main.expense;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.mexpense.R;
import com.example.mexpense.databinding.FragmentExpenseFormBinding;
import com.example.mexpense.entity.Expense;
import com.example.mexpense.services.ExpenseService;
import com.example.mexpense.services.LocationService;
import com.example.mexpense.ultilities.Constants;
import com.example.mexpense.ultilities.Utilities;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class ExpenseFormFragment extends Fragment implements View.OnClickListener {

    private ExpenseFormViewModel mViewModel;
    final Calendar myCalendar = Calendar.getInstance();
    private FragmentExpenseFormBinding binding;
    private ExpenseService service;

    DatePickerDialog.OnDateSetListener date;

    private int expenseId;
    private int tripId;

    private TextInputLayout categoryLayout;
    private TextInputLayout costLayout;
    private TextInputLayout dateLayout;

    private AutoCompleteTextView editCategory;
    private TextInputEditText editCost;
    private TextInputEditText editDate;
    private TextInputEditText editAmount;

    private Button buttonAddImage;

    private LocationService locationService;

    private static final String CATEGORY_KEY = "category";
    private static final String COST_KEY = "cost";
    private static final String AMOUNT_KEY = "amount";
    private static final String DATE_KEY = "date";
    private static final String COMMENT_KEY = "comment";

    public static ExpenseFormFragment newInstance() {
        return new ExpenseFormFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(ExpenseFormViewModel.class);
        binding = FragmentExpenseFormBinding.inflate(inflater, container, false);
        service = new ExpenseService(getContext());
        locationService = new LocationService(getContext());
        locationService.getLocation();

        try {
            expenseId = getArguments().getInt("expenseId");
        } catch (Exception e) {
            expenseId = -1;
        }

        tripId = getArguments().getInt("tripId");

        categoryLayout = binding.textInputLayoutCategory;
        costLayout = binding.textInputLayoutCost;
        dateLayout = binding.textInputLayoutDate;
        editCategory = binding.inputTextCategories;
        editCost = binding.inputCost;
        editDate = binding.inputDate;
        editAmount = binding.inputAmount;
        buttonAddImage = binding.btnImage;

        Button saveButton = binding.btnSaveExpense;
        saveButton.setOnClickListener(this);
        editDate.setOnClickListener(this);
        buttonAddImage.setOnClickListener(this);

        date = (datePicker, year, month, day) -> {
            setCalendar(year, month, day);
            updateDate();
        };

        mViewModel.expense.observe(
                getViewLifecycleOwner(),
                expense -> {
                    if(savedInstanceState != null){
                        binding.inputTextCategories.setText(savedInstanceState.getString(CATEGORY_KEY));
                        getCategories();
                        binding.inputDate.setText(expense.getDate());
                        binding.inputCost.setText(String.valueOf(savedInstanceState.getDouble(COST_KEY)));
                        binding.inputAmount.setText(String.valueOf(savedInstanceState.getInt(AMOUNT_KEY)));
                        binding.inputTextComment.setText(savedInstanceState.getString(COMMENT_KEY));
                    } else {
                        binding.inputTextCategories.setText(expense.getCategory());
                        getCategories();
                        binding.inputDate.setText(expense.getDate());
                        binding.inputCost.setText(String.valueOf(expense.getCost()));
                        binding.inputAmount.setText(String.valueOf(expense.getAmount()));
                        binding.inputTextComment.setText(expense.getComment());
                    }
                }
        );
        service.getExpenseById(mViewModel.expense, expenseId);

        AppCompatActivity app = (AppCompatActivity)getActivity();
        ActionBar ab = app.getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_back);
        ab.setTitle("Editing Expense");
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
                Utilities.hideInput(getActivity(), getView());
                // Close service
                locationService.removeService();
                Utilities.hideInput(getActivity(), getView());
                Navigation.findNavController(getView()).navigateUp();
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
        menu.findItem(R.id.action_upload).setVisible(false);
        if (e != null
                && e.getId() == Constants.NEW_EXPENSE){
            menu.findItem(R.id.action_delete).setVisible(false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putString(CATEGORY_KEY, editCategory.getText().toString());
        savedInstanceState.putDouble(COST_KEY, Double.parseDouble(editCost.getText().toString()));
        savedInstanceState.putInt(AMOUNT_KEY, Integer.parseInt(editAmount.getText().toString()));
        savedInstanceState.putString(DATE_KEY, editDate.getText().toString());
        savedInstanceState.putString(COMMENT_KEY, binding.inputTextComment.getText().toString());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSaveExpense:
                locationService.getLocation();
                handleSave();
                break;
            case R.id.btnImage:
                setImage();
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

    private void setImage(){
        int MY_PERMISSIONS_REQUEST_CAMERA=0;
        Log.i("CAMERA ACCESS", "setImage:  setting image");
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA))
            {
                openCamera();
            }
            else
            {
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA );
            }
        }
    }

    private void openCamera(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 1001);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap photo = (Bitmap) data.getExtras().get("data");
    }

    private void getCategories(){
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.dropdown_item, Constants.categories);
        editCategory.setAdapter(adapter);
        editCategory.setOnClickListener(this);
    }

    private void handleDelete(){
        new AlertDialog.Builder(getContext()).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirmation").setMessage("Are you sure?")
                .setPositiveButton("Yes", (arg0, arg1) -> {
                    service.deleteExpense(expenseId);
                    Bundle bundle = new Bundle();
                    bundle.putInt("tripId", tripId);

                    Utilities.hideInput(getActivity(), getView());
                    // Close service;
                    locationService.removeService();
                    Utilities.hideInput(getActivity(), getView());
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
                        // Close service;
                        locationService.removeService();
                        Utilities.hideInput(getActivity(), getView());
                        Navigation.findNavController(getView()).navigateUp();
                    }).setNegativeButton("No", null).show();
        }
    }

    private boolean validation() {
        boolean result = true;
        String startDate = getArguments().getString("startDate");
        String endDate = getArguments().getString("endDate");

        if (editCategory.getText().toString().equals("")) {
            categoryLayout.setError("Please select a category");
            result = false;
        } else {
            categoryLayout.setError(null);
        }

        if (editAmount.getText().toString().equals("")) {
            binding.textInputLayoutAmount.setError("Please enter the amount");
            result = false;
        } else {
            categoryLayout.setError(null);
        }

        if (editDate.getText().toString().equals("")) {
            dateLayout.setError("Please select a date");
            result = false;
        } else {
            if(!dateValidation(startDate, endDate, binding.inputDate.getText().toString())){
                dateLayout.setError("Expense date must be within " + startDate + " and " + endDate);
                result = false;
            }
            else {
                dateLayout.setError(null);
            }
        }

        if (Float.parseFloat(editCost.getText().toString()) == 0.0) {
            costLayout.setError("Please enter a cost");
            result = false;
        } else {
            costLayout.setError(null);
        }

        return result;
    }

    private boolean dateValidation(String startStr, String endStr, String expenseStr){
        DateTimeFormatter sql = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_DATABASE);
        LocalDate startDate = LocalDate.parse(startStr, sql);
        LocalDate endDate = LocalDate.parse(endStr, sql);
        LocalDate expenseDate = LocalDate.parse(expenseStr, sql);
        return expenseDate.isAfter(startDate) && expenseDate.isBefore(endDate) || expenseDate.isEqual(startDate)  || expenseDate.isEqual(endDate);
    }

    private Expense getFormInput() {
        String category = editCategory.getText().toString();
        String date = editDate.getText().toString();
        String comment = binding.inputTextComment.getText().toString();
        double cost = Double.parseDouble(editCost.getText().toString());
        int amount = Integer.parseInt(editAmount.getText().toString());
        locationService.getLocation();
        return new Expense(-1, category, cost, amount, date, comment, tripId, locationService.getLatitude(), locationService.getLongitude());
    }

    private void updateDate() {
        String format = Constants.DATE_FORMAT_DATABASE;
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
        editDate.setText(dateFormat.format(myCalendar.getTime()));
    }
    public void setDate() {
        DateTimeFormatter source = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT_DATABASE);
        if(expenseId == -1){
            LocalDate startDate = LocalDate.parse(getArguments().getString("startDate"), source);
            setCalendar(startDate.getYear(), startDate.getMonthValue() - 1, startDate.getDayOfMonth());
        } else {
            LocalDate currentDate = LocalDate.parse(mViewModel.expense.getValue().getDate(), source);
            setCalendar(currentDate.getYear(), currentDate.getMonthValue() - 1, currentDate.getDayOfMonth());
        }
        new DatePickerDialog(getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void setCalendar(int year, int month, int day){
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, month);
        myCalendar.set(Calendar.DAY_OF_MONTH, day);
    }
}