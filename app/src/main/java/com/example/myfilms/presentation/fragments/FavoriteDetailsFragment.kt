package com.example.myfilms.presentation.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.myfilms.R
import com.example.myfilms.data.ApiFactory
import com.example.myfilms.data.models.LoginApprove
import com.example.myfilms.data.models.Movie
import com.example.myfilms.data.models.PostMovie
import com.example.myfilms.data.models.Session
import com.example.myfilms.databinding.FragmentFavoriteDetailsBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext


class FavoriteDetailsFragment : Fragment(), CoroutineScope {

    private var _binding: FragmentFavoriteDetailsBinding? = null
    private val binding: FragmentFavoriteDetailsBinding
        get() = _binding ?: throw RuntimeException("FragmentFavoriteDetailsBinding is null")

    override val coroutineContext: CoroutineContext = Dispatchers.Main
    private val apiService = ApiFactory.getInstance()

    private lateinit var prefSettings: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefSettings = context?.getSharedPreferences(
            LoginFragment.APP_SETTINGS, Context.MODE_PRIVATE) as SharedPreferences
        parseArgs()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getMovieById(movieId)
        onFavoriteClickListener()
        onTrailerClick()

    }

    private fun onTrailerClick() {

        binding.clTrailer.setOnClickListener {
            getTrailer()
        }
    }

    private fun onFavoriteClickListener() {

        binding.ivAddFavorite.setOnClickListener {

            if (binding.ivAddFavorite.tag == TAG_YELLOW) {
                addFavorite(movieId, sessionId)

                if (sessionId != "") {
                    binding.ivAddFavorite.setImageResource(R.drawable.ic_star_white)
                    binding.ivAddFavorite.tag = TAG_WHITE
                }
            } else {
                deleteFavorite(movieId, sessionId)

                if (sessionId != "") {
                    binding.ivAddFavorite.setImageResource(R.drawable.ic_star_yellow)
                    binding.ivAddFavorite.tag = TAG_YELLOW
                }
            }
        }
    }

    private fun deleteFavorite(movieId: Int, sessionId: String) {

        launch {

            try {
                val postMovie = PostMovie(media_id = movieId, favorite = false)
                apiService.addFavorite(
                    session_id = sessionId,
                    postMovie = postMovie
                )
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Требуется авторизация",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun addFavorite(movieId: Int, sessionId: String) {

        launch {

            try {
                val postMovie = PostMovie(media_id = movieId, favorite = true)
                apiService.addFavorite(
                    session_id = sessionId,
                    postMovie = postMovie
                )
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Требуется авторизация",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getMovieById(movieId: Int) {

        binding.progressBar.visibility = View.VISIBLE
        launch {

            try {
                sessionId = prefSettings.getString(LoginFragment.SESSION_ID_KEY, null) as String
            } catch (e: Exception) {
            }
            val movie = apiService.getById(movieId)
            Picasso.get().load(IMG_URL + movie.backdropPath).into(binding.ivPoster)
            binding.tvTitle.text = movie.title
            binding.tvOverview.text = movie.overview
            val video = apiService.getVideos(movieId)
            video.list.map {
                binding.textViewNameOfVideo.text = it.name
                key = it.key
            }
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun getTrailer() {

        launch {
            val video = apiService.getVideos(movieId)
            key = video.list.first().key
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.parse(YOUTUBE_URL + key)
            startActivity(intent)
        }
    }

    private fun parseArgs() {

        requireArguments().getInt(KEY_MOVIE).apply {
            movieId = this
        }
    }

    companion object {

        private var movieId: Int = 0

        private var sessionId: String = ""
        private var key: String = ""
        private const val YOUTUBE_URL = "https://www.youtube.com/watch?v="
        private const val TAG_WHITE = "white"
        private const val TAG_YELLOW = "yellow"
        private const val IMG_URL = "https://image.tmdb.org/t/p/w500"
        const val KEY_MOVIE = "Movie_id"
    }
}