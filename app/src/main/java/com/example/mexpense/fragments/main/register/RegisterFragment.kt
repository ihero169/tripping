package com.example.mexpense.fragments.main.register

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.mexpense.R
import com.example.mexpense.base.BaseMVVMFragment
import com.example.mexpense.databinding.FragmentRegisterBinding

class RegisterFragment : BaseMVVMFragment<FragmentRegisterBinding,RegisterViewModel>() {
    override fun getViewModelClass(): Class<RegisterViewModel> {
        return RegisterViewModel::class.java
    }

    override fun getLayoutBinding(): FragmentRegisterBinding {
        return FragmentRegisterBinding.inflate(layoutInflater)
    }

    override fun initialize() {
        getViewBinding().login.setOnClickListener {
            navControl.navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
        }
    }

    override fun registerViewEvent() {
        TODO("Not yet implemented")
    }

    override fun registerViewModelObs() {
        TODO("Not yet implemented")
    }

}