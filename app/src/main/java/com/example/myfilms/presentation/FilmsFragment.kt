package com.example.myfilms.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.myfilms.R
import com.example.myfilms.data.ApiFactory
import com.example.myfilms.databinding.FragmentMoviesBinding
import com.example.myfilms.presentation.adapter.films_adapter.FilmsAdapter
import com.example.myfilms.presentation.models.Movie
import kotlinx.coroutines.*
import java.lang.RuntimeException

class FilmsFragment : Fragment() {

    private var _binding: FragmentMoviesBinding? = null
    private val binding: FragmentMoviesBinding
        get() = _binding ?: throw RuntimeException("FragmentFilmsBinding is null")

    private val adapter = FilmsAdapter()
    private val apiService = ApiFactory.getInstance()
    private val scope = CoroutineScope(Dispatchers.Main)
    private var list = listOf<Movie>()
    private var data: MutableLiveData<List<Movie>> = MutableLiveData()


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
                if (!recyclerView.canScrollVertically(1)) {
                    PAGE ++
                    binding.progressBar.visibility = View.VISIBLE
                    downloadData()
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
        scope.launch {
            val result = apiService.getMovies(page = PAGE)
            list += result.movies
            data.value = list
            delay(500)
            binding.progressBar.visibility = View.GONE
        }
    }

    private  fun launchDetailFragment(movie: Movie) {
        val args = Bundle().apply {
            putParcelable(DetailsFragment.KEY_MOVIE, movie)
        }
        findNavController().navigate(R.id.action_filmsFragment_to_detailsFragment, args)
    }

    companion object {

        private var PAGE = 1
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}