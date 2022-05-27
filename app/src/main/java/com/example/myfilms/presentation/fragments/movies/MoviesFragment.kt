package com.example.myfilms.presentation.fragments.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.example.myfilms.R
import com.example.myfilms.data.database.model.movie.MovieDbModel
import com.example.myfilms.databinding.FragmentMoviesBinding
import com.example.myfilms.presentation.adapter.loading_adapter.LoadStateViewHolder
import com.example.myfilms.presentation.adapter.loading_adapter.NewLoadingStateAdapter
import com.example.myfilms.presentation.adapter.movie_adapter.MoviesAdapter
import com.example.myfilms.presentation.fragments.details.DetailsFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MoviesFragment : Fragment() {

    private var _binding: FragmentMoviesBinding? = null
    private val binding: FragmentMoviesBinding
        get() = _binding ?: throw RuntimeException("FragmentFilmsBinding is null")

    private val viewModel by viewModel<MovieViewModel>()
    private val adapter = MoviesAdapter()
    private lateinit var loadStateViewHolder: LoadStateViewHolder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        observe()
        setListeners()
        onBackPressed()
    }

    private fun init() {
        binding.rvMovies.adapter = adapter.withLoadStateFooter(
            footer = NewLoadingStateAdapter {
                adapter.retry()
            }
        )

        loadStateViewHolder = LoadStateViewHolder(
            binding.loadStateView,
            binding.errorView.tvError,
            binding.swipeRefresh,
        ) { adapter.retry() }
    }

    private fun observe() {
        lifecycleScope.launch {
            viewModel.moviesFlow.collectLatest {
                adapter.submitData(it)
            }
        }

        //следить за текущим LoadState в NetworkPagingSource и передавать его в LoadStateViewHolder.bind()
        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest {
                loadStateViewHolder.bind(it.refresh)
            }
        }
    }

    private fun setListeners() {
        adapter.onFilmClickListener = object : MoviesAdapter.OnFilmClickListener {
            override fun onFilmClick(movieDbModel: MovieDbModel) {
                launchDetailFragment(movieDbModel)
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.onRefresh()
        }

        adapter.addLoadStateListener { loadState ->
            binding.rvMovies.isVisible = loadState.refresh !is LoadState.Error

            with(binding.errorView) {
                llError.isVisible = loadState.refresh is LoadState.Error

                if (loadState.refresh is LoadState.Error) {
                    val error = (loadState.refresh as LoadState.Error).error.message
                    tvError.text = error
                }

                btnRetry.setOnClickListener {
                    adapter.retry()
                }
            }
        }
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
                                viewModel.deleteMainSession()
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

    private fun launchDetailFragment(movieDbModel: MovieDbModel) {
        val args = Bundle().apply {
            putParcelable(DetailsFragment.KEY_MOVIE, movieDbModel)
        }

        findNavController().navigate(R.id.action_moviesFragment_to_detailsFragment, args)
    }
}