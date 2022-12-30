package com.example.chartappwithstreamsdk.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.example.chartappwithstreamsdk.R
import com.example.chartappwithstreamsdk.databinding.FragmentLoginBinding
import com.example.chartappwithstreamsdk.ui.BindingFragment
import com.example.chartappwithstreamsdk.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LoginFragment : BindingFragment<FragmentLoginBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentLoginBinding::inflate

    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnConfirm.setOnClickListener {
            setupConnectingUiState()
            viewModel.connectUser(binding.etUsername.text.toString())
        }

        binding.etUsername.addTextChangedListener {
            binding.etUsername.error = null
        }

        subscribeToEvents()
    }

    private fun subscribeToEvents() {
        lifecycleScope.launchWhenCreated {
            viewModel.loginEvent.collect { event ->
                when(event) {
                    is LoginViewModel.LoginEvent.ErrorInputTooShort -> {
                        setupIdleUiState()
                        binding.etUsername.error = getString(R.string.error_username_too_short, Constants.MIN_USERNAME_LENGTH)
                    }
                    is LoginViewModel.LoginEvent.ErrorLogin -> {
                        setupIdleUiState()
                        Toast.makeText(
                            requireContext(),
                            event.error,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is LoginViewModel.LoginEvent.Success -> {
                        setupIdleUiState()
                        Toast.makeText(
                            requireContext(),
                            "Successfully login",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun setupConnectingUiState() {
        binding.progressBar.isVisible = true
        binding.btnConfirm.isEnabled = false
    }

    private fun setupIdleUiState() {
        binding.progressBar.isVisible = false
        binding.btnConfirm.isEnabled = true
    }
}