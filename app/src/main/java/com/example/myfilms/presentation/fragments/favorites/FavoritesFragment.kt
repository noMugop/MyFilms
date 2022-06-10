package com.example.myfilms.presentation.fragments.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myfilms.R
import com.example.myfilms.databinding.FragmentFavoritesBinding
import com.example.myfilms.data.database.model.movie.MovieDbModel
import androidx.lifecycle.lifecycleScope
import com.example.myfilms.presentation.adapter.movie_adapter.MoviesAdapter
import com.example.myfilms.presentation.fragments.details.DetailsFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.Exception
import java.lang.RuntimeException

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding: FragmentFavoritesBinding
        get() = _binding ?: throw RuntimeException("FavoritesFragment is null")

    private val viewModel by viewModel<FavoritesViewModel>()
    private val adapter = MoviesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setObservers()
        onMovieClickListener()
    }

    private fun init() {
        if (viewModel.checkSession().isBlank()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.authorization_required),
                Toast.LENGTH_SHORT
            )
                .show()
        }
        binding.rvFavorites.adapter = adapter
    }

    private fun setObservers() {
        lifecycleScope.launch {
            viewModel.favoritesFlow.collectLatest {
                adapter.submitData(it)
            }
        }
    }

    private fun onMovieClickListener() {
        adapter.onFilmClickListener = object : MoviesAdapter.OnFilmClickListener {
            override fun onFilmClick(movieDbModel: MovieDbModel) {
                launchDetailFragment(movieDbModel)
            }
        }
    }

    private fun launchDetailFragment(movieDbModel: MovieDbModel) {
        val args = Bundle().apply {
            putParcelable(DetailsFragment.KEY_MOVIE, movieDbModel)
        }
        findNavController().navigate(R.id.action_favoritesFragment_to_detailsFragment, args)
    }
}