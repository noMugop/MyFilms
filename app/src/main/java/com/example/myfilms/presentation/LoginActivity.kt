package com.example.myfilms.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.TokenWatcher
import android.util.Log
import android.util.Log.d
import android.util.Log.i
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.startup.StartupLogger.i
import com.example.myfilms.R
import com.example.myfilms.data.ApiFactory
import com.example.myfilms.data.models.LoginApprove
import com.example.myfilms.data.models.Movie
import com.example.myfilms.data.models.Session
import com.example.myfilms.data.models.Token
import com.example.myfilms.databinding.ActivityLoginBinding
import com.example.myfilms.databinding.ActivityMainBinding
import com.example.myfilms.presentation.fragments.FilmsFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class LoginActivity : AppCompatActivity(), CoroutineScope {

    lateinit var binding: ActivityLoginBinding
    private val apiService = ApiFactory.getInstance()
    override val coroutineContext: CoroutineContext = Dispatchers.Main
    private var sessionId: MutableLiveData<String> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            hideKeyboard(this)
            if (!binding.etUsername.text.isNullOrBlank() && !binding.etPassword.text.isNullOrBlank()) {
                val data = LoginApprove(
                    username = binding.etUsername.text.toString(),
                    password = binding.etPassword.text.toString(),
                    request_token = "")
                login(data)
                sessionId.observe(this) {
                    binding.pbLoading.visibility = View.GONE
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra(FilmsFragment.SESSION_ID_KEY, sessionId.value)
                    startActivity(intent)
                }
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
                Toast.makeText(applicationContext, "Неверные данные", Toast.LENGTH_SHORT).show()
                return@launch
            }
            if (tokenVal.request_token != "") {
                val session = apiService.createSession(token = tokenVal)
                sessionId.value = session.session_id
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
}