package com.example.myfilms.presentation.fragments.movies

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.myfilms.R
import com.example.myfilms.databinding.FragmentMoviesBinding
import com.example.myfilms.presentation.adapter.MoviesAdapter
import com.example.myfilms.data.models.movie.Movie
import com.example.myfilms.presentation.utils.LoadingState
import com.example.myfilms.presentation.fragments.details.DetailsFragment
import java.lang.Exception
import java.lang.RuntimeException
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.lifecycleScope
import androidx.paging.flatMap
import androidx.paging.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MoviesFragment : Fragment() {

    private var _binding: FragmentMoviesBinding? = null
    private val binding: FragmentMoviesBinding
        get() = _binding ?: throw RuntimeException("FragmentFilmsBinding is null")

    private lateinit var viewModel: MovieViewModel
    private val adapter = MoviesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBar.visibility = View.VISIBLE
        init()
        observe()
        onMovieClickListener()
        onBackPressed()
    }

    private fun init() {

        viewModel = ViewModelProvider(
            this,
            AndroidViewModelFactory.getInstance(requireActivity().application)
        )[MovieViewModel::class.java]

        binding.rvMovies.adapter = adapter
    }

    private fun observe() {
        lifecycleScope.launch {
            viewModel.moviesFlow.collectLatest {
                binding.progressBar.visibility = View.GONE
                adapter.submitData(it)
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
}