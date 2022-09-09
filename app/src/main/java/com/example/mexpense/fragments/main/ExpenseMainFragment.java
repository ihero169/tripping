package com.example.mexpense.fragments.main;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mexpense.R;
import com.example.mexpense.adapters.ExpenseAdapter;
import com.example.mexpense.databinding.FragmentExpenseMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ExpenseMainFragment extends Fragment implements View.OnClickListener {

    private ExpenseMainViewModel mViewModel;
    private FragmentExpenseMainBinding binding;
    private ExpenseAdapter adapter;

    public static ExpenseMainFragment newInstance() {
        return new ExpenseMainFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(ExpenseMainViewModel.class);
        binding = FragmentExpenseMainBinding.inflate(inflater, container, false);

        RecyclerView rv = binding.expenseRecyclerView;
        rv.setHasFixedSize(true);

        mViewModel.expenseList.observe(
                getViewLifecycleOwner(),
                expenseList -> {
                    adapter = new ExpenseAdapter(expenseList);
                    binding.expenseRecyclerView.setAdapter(adapter);
                    binding.expenseRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                }
        );

        FloatingActionButton btnAdd = binding.btnAdd;
        btnAdd.setOnClickListener(this);

        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnAdd:
                Navigation.findNavController(getView()).navigate(R.id.expenseFormFragment);
            default:
                return;
        }
    }
}