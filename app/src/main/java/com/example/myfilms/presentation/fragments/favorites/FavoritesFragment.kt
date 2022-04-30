package com.example.myfilms.presentation.fragments.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.myfilms.R
import com.example.myfilms.databinding.FragmentFavoritesBinding
import com.example.myfilms.data.models.Movie
import com.example.myfilms.presentation.Utils.LoadingState
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.example.myfilms.presentation.adapter.MoviesAdapter
import com.example.myfilms.presentation.fragments.details.DetailsFragment
import java.lang.Exception
import java.lang.RuntimeException

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding: FragmentFavoritesBinding
        get() = _binding ?: throw RuntimeException("FavoritesFragment is null")

    private val adapter = MoviesAdapter()
    private lateinit var viewModel: ViewModelFavorites

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAndObserveViewModel()
        onMovieClickListener()
        onBackPressed()
    }

    private fun initAndObserveViewModel() {

        viewModel =
            ViewModelProvider(
                this,
                AndroidViewModelFactory.getInstance(requireActivity().application)
            )[ViewModelFavorites::class.java]

        viewModel.isLoading()

        viewModel.loadingState.observe(viewLifecycleOwner) {
            when (it) {
                LoadingState.IS_LOADING -> {
                    viewModel.getFavorites(PAGE)
                    binding.progressBar.visibility = View.VISIBLE
                }
                LoadingState.FINISHED -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.submitList(null)
                    binding.rvFavorites.adapter = adapter
                }
                LoadingState.SUCCESS -> viewModel.movies.observe(viewLifecycleOwner) {
                    binding.progressBar.visibility = View.GONE
                    adapter.submitList(it)
                    binding.rvFavorites.adapter = adapter
                }
                else -> throw RuntimeException("Error")
            }
        }
    }

    private fun onMovieClickListener() {
        adapter.onFilmClickListener = object : MoviesAdapter.OnFilmClickListener {
            override fun onFilmClick(movie: Movie) {
                launchDetailFragment(movie)
            }
        }
    }

    private fun launchDetailFragment(movie: Movie) {
        val args = Bundle().apply {
            putParcelable(DetailsFragment.KEY_MOVIE, movie)
        }

        findNavController().navigate(R.id.action_favoritesFragment_to_detailsFragment, args)
    }

    private fun onBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireContext().let {
                    AlertDialog
                        .Builder(it)
                        .setMessage("Выйти?")
                        .setPositiveButton("Да") { dialogInterface, i ->
                            try {
                                viewModel.deleteSession()
                                findNavController().popBackStack()
                            } catch (e: Exception) {
                                findNavController().popBackStack()
                            }
                        }
                        .setNegativeButton("Нет") { dialogInterface, i -> }
                        .create()
                        .show()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    companion object {

        private var PAGE = 1
    }
}