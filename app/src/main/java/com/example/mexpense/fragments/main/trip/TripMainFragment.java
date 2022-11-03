package com.example.mexpense.fragments.main.trip;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mexpense.R;
import com.example.mexpense.adapters.TripAdapter;
import com.example.mexpense.databinding.FragmentTripMainBinding;
import com.example.mexpense.entity.Trip;
import com.example.mexpense.services.TripService;
import com.example.mexpense.ultilities.Constants;
import com.example.mexpense.ultilities.Utilities;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

    private ImageButton searchToggle;
    private Button resetBtn;
    private Button sortBtn;

    private boolean isOpen = false;

    private HttpURLConnection con;

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
                    if (trips.size() == 0) {
                        binding.emptyTripLayout.setVisibility(View.VISIBLE);
                    } else {
                        binding.emptyTripLayout.setVisibility(View.INVISIBLE);
                    }
                }
        );

        service.getTrips(mViewModel.tripList);

        sortBtn = binding.btnSortId;
        sortBtn.setOnClickListener(this);

        resetBtn = binding.btnReset;
        resetBtn.setOnClickListener(this);

        searchToggle = binding.btnSearchToggle;
        searchToggle.setOnClickListener(this);

        editStartDate = binding.inputStartDate;
        editStartDate.setOnClickListener(this);
        editStartDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                service.masterSearch(
                        mViewModel.tripList,
                        sortTripType.getText().toString(),
                        editDestination.getText().toString(),
                        editStartDate.getText().toString(),
                        editEndDate.getText().toString());
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
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                service.masterSearch(
                        mViewModel.tripList,
                        sortTripType.getText().toString(),
                        editDestination.getText().toString(),
                        editStartDate.getText().toString(),
                        editEndDate.getText().toString());
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
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String type = sortTripType.getText().toString();
                Log.i("Search", "Keyword: " + type);
                service.masterSearch(
                        mViewModel.tripList,
                        sortTripType.getText().toString(),
                        editDestination.getText().toString(),
                        editStartDate.getText().toString(),
                        editEndDate.getText().toString());
            }
        });

        editDestination = binding.editDestination;
        editDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                service.masterSearch(
                        mViewModel.tripList,
                        sortTripType.getText().toString(),
                        editDestination.getText().toString(),
                        editStartDate.getText().toString(),
                        editEndDate.getText().toString());
            }
        });

        AppCompatActivity app = (AppCompatActivity) getActivity();
        ActionBar ab = app.getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_home);
        ab.setTitle("MExpense");
        setHasOptionsMenu(true);

        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_trip_fragment, menu);
        menu.findItem(R.id.action_delete).setVisible(false);
        menu.findItem(R.id.action_edit).setVisible(false);
        menu.findItem(R.id.action_reset).setVisible(true);
        menu.findItem(R.id.action_upload).setVisible(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Navigation.findNavController(getView()).navigateUp();
                return true;
            case R.id.action_reset:
                resetDatabase();
                return true;
            case R.id.action_upload:
                uploadToCloud();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
                break;
            case R.id.btnSearchToggle:
                toggleSearchField();
                break;
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

    private void toggleSearchField() {
        if (isOpen) {
            binding.mainLayout.animate().translationY(-binding.sortLayout.getHeight()).setDuration(500);
            binding.tripRecyclerView.animate().translationY(0).setDuration(600);
            binding.btnSearchToggle.setImageResource(R.drawable.ic_filter);
        } else {
            binding.mainLayout.animate().translationY(0).setDuration(500);
            binding.tripRecyclerView.animate().translationY(430).setDuration(400);
            binding.btnSearchToggle.setImageResource(R.drawable.ic_cancel);
        }
        isOpen = !isOpen;
    }

    private void addTrip() {
        Bundle bundle = new Bundle();
        bundle.putInt("tripId", -1);
        Navigation.findNavController(getView()).navigate(R.id.tripFormFragment, bundle);
    }

    private void resetDatabase() {
        new AlertDialog.Builder(getContext()).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirmation").setMessage("The database will be reset and this action cannot be undone, are you sure?")
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

    private void resetNarrowDown() {
        sortTripType.setText("");
        editStartDate.setText("");
        editEndDate.setText("");
        editDestination.setText("");
        binding.layoutStartDateSort.setError(null);
        Utilities.hideInput(getActivity(), getView());
        service.getTrips(mViewModel.tripList);
    }

    private void uploadToCloud() {
        String url = getString(R.string.service_url);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, url, (Response.Listener<String>) response -> {
            try {
                JSONObject api_response = new JSONObject(response);
                Log.i("RESPONSE", "uploadToCloud: " + api_response);
                if(api_response.get("uploadResponseCode").equals("SUCCESS")){
                    new AlertDialog.Builder(getContext()).setIcon(R.drawable.ic_check_circle)
                            .setTitle("Data Uploaded to Cloud").setMessage(
                                    "Response Code: " + api_response.get("uploadResponseCode") + "\n" +
                                            "Uploaded " + api_response.get("number") + " entries for user: " + api_response.get("userid") + "\n" +
                                            "Names: " + api_response.get("names") + "\n" +
                                            "Message: " + api_response.get("message") + "\n"
                            ).setPositiveButton("Close", null).show();
                } else {
                    new AlertDialog.Builder(getContext()).setIcon(R.drawable.ic_error)
                            .setTitle("Error Uploading Data to Cloud").setMessage(
                                    "Response Code: " + api_response.get("uploadResponseCode") + "\n" + "Message: " + api_response.get("message") + "\n"
                            ).setPositiveButton("Close", null).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, (Response.ErrorListener) error -> Log.i("ERROR UPLOADING", "uploadToCloud: " + error)) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                List<Trip> trips = mViewModel.tripList.getValue();
                StringBuilder payload = new StringBuilder();
                if (trips != null && trips.size() > 0) {
                    for (Trip t : trips) {
                        payload.append(t.toJson());
                        payload.append(",");
                    }
                }
                payload.deleteCharAt(payload.length() - 1);

                params.put("jsonpayload", "{\"userId\":\"ht4872m\",\"detailList\":[" + payload + "]}");
                Log.i("POST PARAMS", "getParams: " + params);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        queue.add(jsonObjReq);
    }
}
