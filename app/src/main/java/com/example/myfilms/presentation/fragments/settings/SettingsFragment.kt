package com.example.myfilms.presentation.fragments.settings

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
        buttonListeners()
    }

    private fun init() {

        viewModel = ViewModelProvider(
            this,
            AndroidViewModelFactory.getInstance(requireActivity().application)
        )[ViewModelSettings::class.java]

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
    }

    private fun buttonListeners() {

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
            binding.btnSave.setBackgroundColor(Color.parseColor("#1A424242"))
            viewModel.loadingState.observe(viewLifecycleOwner) {
                when (it) {
                    LoadingState.IS_LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                        if (currentUri != null) {
                            val stringUri = currentUri.toString()
                            viewModel.updateUser(stringUri)
                            currentUri = null
                        } else {
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                    LoadingState.FINISHED -> {
                        Toast.makeText(
                            requireContext(),
                            "Требуется авторизация",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        binding.progressBar.visibility = View.GONE
                    }
                    LoadingState.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE
                        findNavController().popBackStack()
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            Activity.RESULT_OK -> {
                currentUri = data?.data
                binding.ivAvatar.setImageURI(currentUri)
                binding.btnSave.isEnabled = true
                binding.btnSave.setBackgroundColor(Color.parseColor("#215CF3"))
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

        private var currentUri: Uri? = null
        private const val IMG_URL = "https://image.tmdb.org/t/p/w500"
    }
}