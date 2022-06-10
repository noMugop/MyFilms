package com.example.myfilms.presentation.fragments.login

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myfilms.R
import com.example.myfilms.databinding.FragmentLoginBinding
import com.example.myfilms.presentation.utils.LoadingState
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding
        get() = _binding ?: throw RuntimeException("FragmentLoginBinding is null")

    private val viewModel by viewModel<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setListeners()
        observeLoadingState()
    }

    private fun init() {
        viewModel.setSuccess()
    }

    private fun setListeners() {
        binding.btnLogin.setOnClickListener {
            hideKeyboard(requireActivity())
            if (!binding.etUsername.text.isNullOrBlank()
                && !binding.etPassword.text.isNullOrBlank()
            ) {
                val username = binding.etUsername.text.toString().trim()
                val password = binding.etPassword.text.toString().trim()
                viewModel.login(username, password)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.no_data),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    private fun observeLoadingState() {
        viewModel.loadingState.observe(viewLifecycleOwner) {
            when (it) {
                LoadingState.LOADING -> binding.pbLoading.visibility = View.VISIBLE
                LoadingState.DONE -> viewModel.getFavorites()
                LoadingState.SUCCESS -> {
                    binding.pbLoading.visibility = View.GONE
                    cleanFields()
                    findNavController().navigate(R.id.action_loginFragment_to_settingsFragment)
                }
                LoadingState.WARNING -> {
                    binding.pbLoading.visibility = View.GONE
                    if (!binding.etUsername.text.isNullOrBlank()
                        || !binding.etPassword.text.isNullOrBlank()
                    ) {
                        Toast.makeText(
                            requireContext(),
                            LoginViewModel.errorMsg,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
                else -> {}
            }
        }
    }

    private fun cleanFields() {
        binding.etUsername.text = null
        binding.etPassword.text = null
    }

    private fun hideKeyboard(activity: Activity) {
        val inputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            activity.currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}

