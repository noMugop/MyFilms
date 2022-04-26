package com.example.myfilms.presentation.fragments.login

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.myfilms.R
import com.example.myfilms.data.models.LoginApprove
import com.example.myfilms.databinding.FragmentLoginBinding
import com.example.myfilms.presentation.Utils.LoadingState
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.example.myfilms.presentation.fragments.favorite.FavoritesFragment
import java.lang.Exception
import java.lang.RuntimeException

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding
        get() = _binding ?: throw RuntimeException("FragmentLoginBinding is null")

    private lateinit var viewModel: ViewModelLogin

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        onLoginClick()
        onGuestClick()
        onBackPressed()
    }

    private fun initViewModel() {
        viewModel =
            ViewModelProvider(
                this,
                AndroidViewModelFactory.getInstance(requireActivity().application)
            )[ViewModelLogin::class.java]
    }

    private fun onLoginClick() {
        binding.btnLogin.setOnClickListener {
            hideKeyboard(requireActivity())
            if (!binding.etUsername.text.isNullOrBlank() && !binding.etPassword.text.isNullOrBlank()) {
                val data = LoginApprove(
                    username = binding.etUsername.text.toString().trim(),
                    password = binding.etPassword.text.toString().trim(),
                    request_token = ""
                )
                viewModel.login(data)
                observeLoadingState()
            } else {
                Toast.makeText(requireContext(), "Введите данные", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onGuestClick() {
        binding.btnGuest.setOnClickListener {
            hideKeyboard(requireActivity())
            try {
                viewModel.deleteSession()
                findNavController().navigate(R.id.movies_nav)
            } catch (e: Exception) {
                findNavController().navigate(R.id.movies_nav)
            }
        }
    }

    private fun observeLoadingState() {
        viewModel.loadingState.observe(viewLifecycleOwner) {
            when (it) {
                LoadingState.IS_LOADING -> binding.pbLoading.visibility = View.VISIBLE
                LoadingState.FINISHED -> binding.pbLoading.visibility = View.GONE
                LoadingState.SUCCESS -> {
                    viewModel.sessionId.observe(viewLifecycleOwner) {
                        binding.etUsername.text = null
                        binding.etPassword.text = null
                        try {
                            findNavController().navigate(R.id.movies_nav)
                        } catch (e: Exception) {
                        }
                    }
                }
                else -> Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hideKeyboard(activity: Activity) {
        val inputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            activity.currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    private fun onBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}

