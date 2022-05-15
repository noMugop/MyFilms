package com.example.myfilms.presentation.fragments.settings

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
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.navigation.fragment.findNavController
import com.example.myfilms.R
import com.example.myfilms.databinding.FragmentSettingsBinding
import com.example.myfilms.presentation.utils.LoadingState
import com.github.dhaval2404.imagepicker.ImagePicker
import com.squareup.picasso.Picasso

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding
        get() = _binding ?: throw RuntimeException("FragmentSettingsBinding is null")

    private lateinit var viewModel: SettingsViewModel

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
        )[SettingsViewModel::class.java]

        viewModel.getUser()
        binding.btnSave.isEnabled = false
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

        viewModel.loadingState.observe(viewLifecycleOwner) {
            when (it) {
                LoadingState.IS_LOADING -> {
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
                        viewModel.updateUser()
                        clearData()
                    } else {
                        binding.progressBar.visibility = View.GONE
                        viewModel.updateUser()
                    }
                }
                LoadingState.FINISHED -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Требуется авторизация", Toast.LENGTH_SHORT
                    ).show()
                }
                LoadingState.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Изменения сохранены", Toast.LENGTH_SHORT
                    ).show()
                    findNavController().popBackStack()
                    viewModel.waitState()
                }
                else -> {}
            }
        }
    }

    private fun listeners() {

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

            val cameraPerm = "android.permission.CAMERA"
            val galleryPerm = "android.permission.READ_EXTERNAL_STORAGE"
            var camText = ""
            var galText = ""
            var comma = ","

            permissions.forEach {
                if (it.key == cameraPerm && !it.value) {
                    camText = " камера "
                } else if (it.key == galleryPerm && !it.value) {
                    galText = " галерея "
                } else {
                    comma = ""
                }
            }

            if (camText.isNotBlank() || galText.isNotBlank()) {
                Toast.makeText(
                    requireContext(),
                    "Требуется разрешние$camText$comma$galText",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                launchIntent()
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

    companion object {

        private const val IMG_URL = "https://image.tmdb.org/t/p/w500"
    }
}