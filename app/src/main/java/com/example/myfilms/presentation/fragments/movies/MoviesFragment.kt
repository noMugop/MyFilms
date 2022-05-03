package com.example.myfilms.presentation.fragments.movies

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.myfilms.R
import com.example.myfilms.databinding.FragmentMoviesBinding
import com.example.myfilms.presentation.adapter.MoviesAdapter
import com.example.myfilms.data.models.Movie
import com.example.myfilms.presentation.Utils.LoadingState
import com.example.myfilms.presentation.fragments.details.DetailsFragment
import java.lang.Exception
import java.lang.RuntimeException
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory

class MoviesFragment : Fragment() {

    private var _binding: FragmentMoviesBinding? = null
    private val binding: FragmentMoviesBinding
        get() = _binding ?: throw RuntimeException("FragmentFilmsBinding is null")

    private val adapter = MoviesAdapter()
    private lateinit var viewModel: ViewModelMovie

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAndObserveViewModel()
        syncFavorites()
        onMovieClickListener()
        onBackPressed()
    }

    private fun initAndObserveViewModel() {

        viewModel = ViewModelProvider(
            this,
            AndroidViewModelFactory.getInstance(requireActivity().application)
        )[ViewModelMovie::class.java]

        viewModel.getMoviesList()

        viewModel.loadingState.observe(viewLifecycleOwner) {
            when (it) {
                LoadingState.IS_LOADING -> binding.progressBar.visibility = View.VISIBLE
                LoadingState.FINISHED -> binding.progressBar.visibility = View.GONE
                LoadingState.SUCCESS -> viewModel.movies.observe(viewLifecycleOwner) {
                    adapter.submitList(it)
                    binding.rvMovies.adapter = adapter
                }
                else -> throw RuntimeException("Error")
            }
        }
    }

    private fun syncFavorites() {
        viewModel.syncFavorites(page = PAGE)
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

        findNavController().navigate(R.id.action_moviesFragment_to_detailsFragment, args)
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

        var PAGE = 1
    }
}