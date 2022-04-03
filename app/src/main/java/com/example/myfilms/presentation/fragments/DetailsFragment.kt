package com.example.myfilms.presentation.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import com.example.myfilms.R
import com.example.myfilms.data.ApiFactory
import com.example.myfilms.databinding.FragmentDetailsBinding
import com.example.myfilms.data.models.Movie
import com.example.myfilms.data.models.PostMovie
import com.example.myfilms.data.models.Session
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext


class DetailsFragment : Fragment(), CoroutineScope {

    private var _binding: FragmentDetailsBinding? = null
    private val binding: FragmentDetailsBinding
        get() = _binding ?: throw RuntimeException("DetailsFragment is null")

    override val coroutineContext: CoroutineContext = Dispatchers.Main
    private val apiService = ApiFactory.getInstance()

    private lateinit var prefSettings: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefSettings = context?.getSharedPreferences(
            LoginFragment.APP_SETTINGS,
            Context.MODE_PRIVATE) as SharedPreferences
        parseArgs()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getMovieById(movieId)
        onFavoriteClickListener()
    }

    private fun onFavoriteClickListener() {

        binding.ivAddFavorite.setOnClickListener {
            binding.ivAddFavorite.setImageResource(R.drawable.ic_star_yellow)

            val sessionId = prefSettings.getString(LoginFragment.SESSION_ID_KEY, null) as String
            if (sessionId.isNotEmpty()) {
                addFavorite(movieId, sessionId)
            }
        }
    }

    private fun addFavorite(movieId: Int, sessionId: String) {

        launch {

            val postMovie = PostMovie(media_id = movieId, favorite = true)
            apiService.addFavorite(
                session_id = sessionId,
                postMovie = postMovie
            )
        }
    }

    private fun getMovieById(movieId: Int) {

        binding.progressBar.visibility = View.VISIBLE
        launch {

            val movie = apiService.getById(movieId)
            Picasso.get().load(IMG_URL + movie.backdropPath).into(binding.ivPoster)
            binding.tvTitle.text = movie.title
            binding.tvOverview.text = movie.overview
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun parseArgs() {

        requireArguments().getInt(KEY_MOVIE).apply {
            movieId = this
        }
    }

    companion object {

        private var movieId: Int = 0

        private const val IMG_URL = "https://image.tmdb.org/t/p/w500"
        const val KEY_MOVIE = "Movie_id"
    }
}