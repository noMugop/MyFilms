package com.example.myfilms.presentation.fragments.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myfilms.databinding.FragmentMoviesBinding
import com.example.myfilms.databinding.FragmentSettingsBinding
import com.example.myfilms.presentation.Utils.LoadingState
import com.example.myfilms.presentation.fragments.movies.ViewModelMovie
import java.lang.RuntimeException
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myfilms.R
import com.example.myfilms.presentation.MainActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import com.squareup.picasso.Picasso
import java.lang.Exception

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding
        get() = _binding ?: throw RuntimeException("FragmentSettingsBinding is null")

    private lateinit var viewModel: ViewModelSettings

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        observer()
        listeners()
    }

    private fun init() {

        viewModel = ViewModelProvider(
            this,
            AndroidViewModelFactory.getInstance(requireActivity().application)
        )[ViewModelSettings::class.java]

        viewModel.getUser()
        binding.btnSave.isEnabled = false
        try {
            name = viewModel.user.value?.name as String
            currentUri = Uri.parse(viewModel.user.value?.avatar_uri)
        } catch (e: Exception) {
        }
    }

    private fun observer() {

        viewModel.user.observe(viewLifecycleOwner) {
            if (!it?.avatar.isNullOrBlank() && it?.avatar_uri.isNullOrBlank()) {
                Picasso.get().load(IMG_URL + it?.avatar).into(binding.ivAvatar)
            } else if (!it?.avatar_uri.isNullOrBlank()) {
                val uri = Uri.parse(it?.avatar_uri)
                binding.ivAvatar.setImageURI(uri)
            } else {
                Picasso.get().load(R.drawable.empty_avatar).into(binding.ivAvatar)
            }
        }
    }

    private fun listeners() {

        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (!p0.isNullOrBlank()) {
                    binding.btnSave.isEnabled = true
                    binding.btnSave.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.dark_blue
                        )
                    )
                } else if (p0.isNullOrBlank() && binding.etSurname.text.isNullOrBlank()) {
                    binding.btnSave.isEnabled = false
                    binding.btnSave.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.grey
                        )
                    )
                }
            }
        })

        binding.etSurname.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (!p0.isNullOrBlank()) {
                    binding.btnSave.isEnabled = true
                    binding.btnSave.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.dark_blue
                        )
                    )
                } else if (p0.isNullOrBlank() && binding.etName.text.isNullOrBlank()) {
                    binding.btnSave.isEnabled = false
                    binding.btnSave.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.grey
                        )
                    )
                }
            }
        })

        binding.ivEdit.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }

        binding.btnSave.setOnClickListener {
            viewModel.isLoading()
            binding.btnSave.isEnabled = false
//            binding.btnSave.setBackgroundColor(Color.parseColor("#1A424242"))
            binding.btnSave.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.grey
                )
            )
            viewModel.loadingState.observe(viewLifecycleOwner) {
                when (it) {
                    LoadingState.IS_LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                        hideKeyboard(requireActivity())
                        if (currentUri != null) {
                            val stringUri = currentUri.toString()
                            if (!binding.etName.text.isNullOrBlank()
                                || !binding.etSurname.text.isNullOrBlank()
                            ) {
                                name =
                                    binding.etName.text.toString() + " " + binding.etSurname.text.toString()
                                viewModel.updateUser(name, stringUri)
                                clearData()
                            } else {
                                if (!viewModel.user.value?.name.isNullOrBlank()) {
                                    viewModel.updateUser(name, stringUri)
                                } else {
                                    viewModel.updateUser(name, stringUri)
                                }
                            }
                        } else {
                            if (!binding.etName.text.isNullOrBlank()
                                || !binding.etSurname.text.isNullOrBlank()
                            ) {
                                name =
                                    binding.etName.text.toString() + " " + binding.etSurname.text.toString()
                                viewModel.updateUser(name, currentUri.toString())
                                clearData()
                            } else {
                                binding.progressBar.visibility = View.GONE
                            }
                        }
                    }
                    LoadingState.FINISHED -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Требуется авторизация",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    LoadingState.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Изменения сохранены",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun clearData() {
        binding.etName.text = null
        binding.etSurname.text = null
    }

    private fun hideKeyboard(activity: Activity) {
        val inputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            activity.currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            Activity.RESULT_OK -> {
                currentUri = data?.data
                binding.ivAvatar.setImageURI(currentUri)
                binding.btnSave.isEnabled = true
                binding.btnSave.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.dark_blue
                    )
                )
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT)
                    .show()
            }
            else -> {
                Toast.makeText(requireContext(), "Отмена", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {

        private const val IMG_URL = "https://image.tmdb.org/t/p/w500"
        private var currentUri: Uri? = null
        private var name = ""
    }
}