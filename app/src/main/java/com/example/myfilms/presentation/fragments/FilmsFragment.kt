package com.example.myfilms.presentation.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
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

        binding.progressBar.visibility = View.VISIBLE

        downloadData()
        onBackPressed()

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

    private fun onBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                launch {
                    val sessionId = prefSettings.getString(LoginFragment.SESSION_ID_KEY, null) as String
                    apiService.deleteSession(session_id = Session(session_id = sessionId))
                    findNavController().popBackStack()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    companion object {

        private var PAGE = 1
        private var isLoaded = false

        private var oldList = mutableListOf<Movie>()
        private var newList = mutableListOf<Movie>()
        private var data: MutableLiveData<List<Movie>> = MutableLiveData()
    }
}