package com.example.myfilms.presentation.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.example.myfilms.R
import com.example.myfilms.data.ApiFactory
import com.example.myfilms.data.models.LoginApprove
import com.example.myfilms.data.models.Session
import com.example.myfilms.data.models.Token
import com.example.myfilms.databinding.FragmentLoginBinding
import com.example.myfilms.databinding.FragmentMoviesBinding
import com.example.myfilms.presentation.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

class LoginFragment : Fragment(), CoroutineScope {

    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding
        get() = _binding ?: throw RuntimeException("FragmentLoginBinding is null")

    private val apiService = ApiFactory.getInstance()
    override val coroutineContext: CoroutineContext = Dispatchers.Main

    private lateinit var prefSettings: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        prefSettings = context?.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE) as SharedPreferences
        editor = prefSettings.edit()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            hideKeyboard(requireActivity())
            if (!binding.etUsername.text.isNullOrBlank() && !binding.etPassword.text.isNullOrBlank()) {
                val data = LoginApprove(
                    username = binding.etUsername.text.toString().trim(),
                    password = binding.etPassword.text.toString().trim(),
                    request_token = ""
                )
                login(data)
            } else {
                Toast.makeText(requireContext(), "Введите данные", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun login(data: LoginApprove) {

        launch {

            var tokenVal = Token("")
            binding.pbLoading.visibility = View.VISIBLE
            val tokenNotVal = apiService.getToken()
            val loginApprove = LoginApprove(
                username = data.username,
                password = data.password,
                request_token = tokenNotVal.request_token
            )
            try {
                tokenVal = apiService.approveToken(loginApprove = loginApprove)
            } catch (e: Exception) {
                binding.pbLoading.visibility = View.GONE
                Toast.makeText(requireContext(), "Неверные данные", Toast.LENGTH_SHORT).show()
                return@launch
            }
            if (tokenVal.request_token != "") {
                val session = apiService.createSession(token = tokenVal)
                val sessionId = session.session_id
                println("SESSION_ID $sessionId")
                putDataIntoPref(sessionId)

                findNavController().navigate(R.id.action_loginFragment_to_films_fragment)
            }
        }
    }

    private fun putDataIntoPref(string: String) {
        binding.pbLoading.visibility = View.GONE
        editor.putString(SESSION_ID_KEY, string)
        editor.commit()
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

    companion object {

        const val APP_SETTINGS = "Settings"
        const val SESSION_ID_KEY = "SESSION_ID"
    }
}

