package com.example.myfilms.presentation.fragments.login

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myfilms.R
import com.example.myfilms.databinding.FragmentLoginBinding
import com.example.myfilms.presentation.utils.LoadingState
import com.github.dhaval2404.imagepicker.ImagePicker
import com.squareup.picasso.Picasso
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
        setObservers()
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

        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                if (!p0.isNullOrBlank()) {
                    binding.btnSave.isEnabled = true
                    binding.btnSave.setBackgroundColor(
                        ContextCompat.getColor(requireContext(), R.color.dark_blue)
                    )
                } else if (p0.isNullOrBlank() && binding.etSurname.text.isNullOrBlank()) {
                    binding.btnSave.isEnabled = false
                    binding.btnSave.setBackgroundColor(
                        ContextCompat.getColor(requireContext(), R.color.grey)
                    )
                }
            }
        })

        binding.etSurname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                if (!p0.isNullOrBlank()) {
                    binding.btnSave.isEnabled = true
                    binding.btnSave.setBackgroundColor(
                        ContextCompat.getColor(requireContext(), R.color.dark_blue)
                    )
                } else if (p0.isNullOrBlank() && binding.etName.text.isNullOrBlank()) {
                    binding.btnSave.isEnabled = false
                    binding.btnSave.setBackgroundColor(
                        ContextCompat.getColor(requireContext(), R.color.grey)
                    )
                }
            }
        })

        binding.ivEdit.setOnClickListener {
            val permissions = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )

            if (!checkPermissions(requireContext(), permissions)) {
                requestMultiplePermissions.launch(permissions)
            } else {
                launchIntent()
            }
        }

        binding.btnSave.setOnClickListener {
            viewModel.isLoadingState()
            binding.btnSave.isEnabled = false
//            binding.btnSave.setBackgroundColor(Color.parseColor("#1A424242"))
            binding.btnSave.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.grey)
            )
        }

        binding.btnLogout.setOnClickListener {
            requireContext().let {
                AlertDialog
                    .Builder(it)
                    .setMessage(R.string.quit_question)
                    .setPositiveButton(R.string.yes) { _, _ ->
                        try {
                            viewModel.deleteAll()
                            binding.clLogin.visibility = View.VISIBLE
                            binding.clSettings.visibility = View.GONE
                        } catch (e: Exception) {
                        }
                    }
                    .setNegativeButton(R.string.no) { _, _ -> }
                    .create()
                    .show()
            }
        }
    }

    private fun setObservers() {
        viewModel.loginLoadingState.observe(viewLifecycleOwner) {
            when (it) {
                LoadingState.LOADING -> binding.pbLoading.visibility = View.VISIBLE
                LoadingState.DONE -> viewModel.getFavorites()
                LoadingState.SUCCESS -> {
                    binding.pbLoading.visibility = View.GONE
                    cleanLoginFields()
                    binding.clLogin.visibility = View.GONE
                    binding.clSettings.visibility = View.VISIBLE
                    viewModel.getUser()
                    binding.btnSave.isEnabled = false
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

        viewModel.settingsLoadingState.observe(viewLifecycleOwner) {
            when (it) {
                LoadingState.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                    hideKeyboard(requireActivity())
                    if (!binding.etName.text.isNullOrBlank()
                        || !binding.etSurname.text.isNullOrBlank()
                    ) {
                        binding.progressBar.visibility = View.GONE
                        viewModel.updateName(
                            binding.etName.text.toString() + " "
                                    + binding.etSurname.text.toString()
                        )
                        cleanSettingFields()
                        viewModel.updateUser()
                    } else {
                        binding.progressBar.visibility = View.GONE
                        viewModel.updateUser()
                    }
                }
                LoadingState.WARNING -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.authorization_required),
                        Toast.LENGTH_SHORT
                    ).show()
                    viewModel.doneState()
                }
                LoadingState.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.changes_saved),
                        Toast.LENGTH_SHORT
                    ).show()
                    viewModel.doneState()
                }
                else -> {}
            }
        }
    }

    private fun checkPermissions(context: Context, permissions: Array<String>): Boolean {
        return permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun launchIntent() {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .createIntent { intent ->
                startForProfileImageResult.launch(intent)
            }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    val currentUri = data?.data
                    binding.ivAvatar.setImageURI(currentUri)
                    viewModel.updateUri(currentUri.toString())
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
                }
            }
        }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            val cameraPerm = CAMERA_PERMISSION
            val galleryPerm = GALLERY_PERMISSION
            var camText = ""
            var galText = ""
            var comma = ","

            permissions.forEach {
                if (it.key == cameraPerm && !it.value) {
                    camText = getString(R.string.camera)
                } else if (it.key == galleryPerm && !it.value) {
                    galText = getString(R.string.gallery)
                } else {
                    comma = ""
                }
            }

            if (camText.isNotBlank() || galText.isNotBlank()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.no_permission, camText, comma, galText),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                launchIntent()
            }
        }

    private fun cleanSettingFields() {
        binding.etName.text = null
        binding.etSurname.text = null
    }

    private fun cleanLoginFields() {
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

        private const val IMG_URL = "https://image.tmdb.org/t/p/w500"
        private const val CAMERA_PERMISSION = "android.permission.CAMERA"
        private const val GALLERY_PERMISSION = "android.permission.READ_EXTERNAL_STORAGE"
    }
}

