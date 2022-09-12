package com.example.mexpense.fragments.main.expense;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.mexpense.R;
import com.example.mexpense.databinding.FragmentExpenseDetailBinding;
import com.example.mexpense.services.ExpenseService;

public class ExpenseDetailFragment extends Fragment implements View.OnClickListener {

    private ExpenseDetailViewModel mViewModel;
    private FragmentExpenseDetailBinding binding;
    private ExpenseService service;
    int expenseId;

    public static ExpenseDetailFragment newInstance() {
        return new ExpenseDetailFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(ExpenseDetailViewModel.class);
        binding = FragmentExpenseDetailBinding.inflate(inflater, container, false);
        service = new ExpenseService(getContext());
        expenseId = getArguments().getInt("expenseId");

        Button editButton = binding.btnEdit;
        Button deleteButton = binding.btnDeleteExpense;

        editButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);

        mViewModel.expense.observe(
                getViewLifecycleOwner(),
                expense -> {
                    binding.textExpenseCategory.setText(expense.getCategory());
                    binding.textExpenseDate.setText(expense.getDate());
                    binding.textExpenseCost.setText("$" + expense.getCost());
                }
        );
        service.getExpenseById(mViewModel.expense, expenseId);

        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.btnEdit):
                EditExpense();
                return;
            case (R.id.btnDeleteExpense):
                DeleteExpense();
                return;
            default:
                return;
        }
    }

    public void DeleteExpense() {
        new AlertDialog.Builder(getContext()).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirmation").setMessage("Are you sure?")
                .setPositiveButton("Yes", (arg0, arg1) -> {
                    service.deleteExpense(expenseId);
                    Navigation.findNavController(getView()).navigate(R.id.expenseMainFragment);
                }).setNegativeButton("No", null).show();
    }

    public void EditExpense() {
        Bundle bundle = new Bundle();
        bundle.putInt("expenseId", expenseId);
        Log.d("Click Event", "Id: " + expenseId);
        Navigation.findNavController(getView()).navigate(R.id.expenseFormFragment, bundle);
    }

}