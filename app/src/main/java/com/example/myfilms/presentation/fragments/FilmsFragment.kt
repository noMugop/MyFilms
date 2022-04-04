package com.example.myfilms.presentation.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfilms.R
import com.example.myfilms.data.ApiFactory
import com.example.myfilms.databinding.FragmentMoviesBinding
import com.example.myfilms.presentation.adapter.films_adapter.FilmsAdapter
import com.example.myfilms.data.models.Movie
import com.example.myfilms.data.models.Session
import com.google.gson.Gson
import com.google.gson.JsonObject
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
    private lateinit var prefSettings: SharedPreferences

    private var oldList = mutableListOf<Movie>()
    private var newList = mutableListOf<Movie>()
    private var movies: MutableLiveData<List<Movie>> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefSettings = context?.getSharedPreferences(
            LoginFragment.APP_SETTINGS,
            Context.MODE_PRIVATE
        ) as SharedPreferences
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        downloadData()
        onScrollListener()
        onBackPressed()
        onMovieClickListener()

        movies.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        binding.rvMovies.adapter = adapter
    }

    private fun downloadData() {

        binding.progressBar.visibility = View.VISIBLE
        launch {
            val result = apiService.getMovies(page = PAGE).movies

            newList.clear()
            for (movie in result) {
                newList.add(movie)
            }

            oldList += newList

            movies.postValue(oldList.toList())

            binding.progressBar.visibility = View.GONE
        }
    }

    private fun onMovieClickListener() {

        adapter.onFilmClickListener = object : FilmsAdapter.OnFilmClickListener {
            override fun onFilmClick(movie: Movie) {
                launchDetailFragment(movie.id)
            }
        }
    }

    private fun launchDetailFragment(movieId: Int) {
        val args = Bundle().apply {
            putInt(DetailsFragment.KEY_MOVIE, movieId)
        }
        findNavController().navigate(R.id.action_filmsFragment_to_detailsFragment, args)
    }

    private fun onBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                launch {
                    val sessionId =
                        prefSettings.getString(LoginFragment.SESSION_ID_KEY, null) as String
                    apiService.deleteSession(sessionId = Session(session_id = sessionId))
                    findNavController().popBackStack()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun onScrollListener() {

        adapter.onReachEndListener = object : FilmsAdapter.OnReachEndListener {
            override fun onReachEnd() {
                PAGE++
                downloadData()
            }
        }
    }

    companion object {

        var PAGE = 1
    }
}