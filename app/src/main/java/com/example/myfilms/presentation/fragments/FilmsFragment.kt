package com.example.myfilms.presentation.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.myfilms.R
import com.example.myfilms.data.ApiFactory
import com.example.myfilms.databinding.FragmentMoviesBinding
import com.example.myfilms.presentation.adapter.films_adapter.FilmsAdapter
import com.example.myfilms.data.models.Movie
import kotlinx.coroutines.*
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

class FilmsFragment : Fragment(), CoroutineScope {

    private var _binding: FragmentMoviesBinding? = null
    private val binding: FragmentMoviesBinding
        get() = _binding ?: throw RuntimeException("FragmentFilmsBinding is null")

    override val coroutineContext: CoroutineContext = Dispatchers.Main

    private val adapter = FilmsAdapter()
    private val apiService = ApiFactory.getInstance()

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

        downloadData()

        binding.rvMovies.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)
                    && recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE
                    && !isLoaded
                ) {
                    isLoaded = true
                    binding.progressBar.visibility = View.VISIBLE
                    oldList = newList
                    PAGE++
                    downloadData()
                } else {
                    recyclerView.stopNestedScroll()
                }
            }
        })

        data.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        binding.rvMovies.adapter = adapter

        adapter.onFilmClickListener = object : FilmsAdapter.OnFilmClickListener {
            override fun onFilmClick(movie: Movie) {
                launchDetailFragment(movie)
            }
        }
    }

    private fun downloadData() {
        launch {
            val result = apiService.getMovies(page = PAGE)
            newList = result.movies as MutableList<Movie>
            val list = oldList + newList
            newList = list as MutableList<Movie>
            data.value = newList
            binding.progressBar.visibility = View.GONE
            isLoaded = false
        }
    }

    private fun launchDetailFragment(movie: Movie) {
        val args = Bundle().apply {
            putParcelable(DetailsFragment.KEY_MOVIE, movie)
        }
        findNavController().navigate(R.id.action_filmsFragment_to_detailsFragment, args)
    }

    companion object {

        private var PAGE = 1
        private var isLoaded = false
        const val SESSION_ID_KEY = "SESSION_ID"

        private var oldList = mutableListOf<Movie>()
        private var newList = mutableListOf<Movie>()
        private var data: MutableLiveData<List<Movie>> = MutableLiveData()
    }
}