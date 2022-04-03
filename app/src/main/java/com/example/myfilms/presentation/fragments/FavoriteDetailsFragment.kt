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
import com.example.myfilms.data.models.LoginApprove
import com.example.myfilms.data.models.Movie
import com.example.myfilms.data.models.PostMovie
import com.example.myfilms.data.models.Session
import com.example.myfilms.databinding.FragmentFavoriteDetailsBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.RuntimeException


class FavoriteDetailsFragment : Fragment() {

    private var _binding: FragmentFavoriteDetailsBinding? = null
    private val binding: FragmentFavoriteDetailsBinding
        get() = _binding ?: throw RuntimeException("FragmentFavoriteDetailsBinding is null")

    private val apiService = ApiFactory.getInstance()
    private val scope = CoroutineScope(Dispatchers.Main)

    private lateinit var movie: Movie
    private lateinit var prefSettings: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefSettings = context?.getSharedPreferences(LoginFragment.APP_SETTINGS, Context.MODE_PRIVATE) as SharedPreferences
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

        binding.ivAddFavorite.setOnClickListener {
            binding.ivAddFavorite.setImageResource(R.drawable.ic_star_grey)

            val sessionId = prefSettings.getString(LoginFragment.SESSION_ID_KEY, null) as String
            if (sessionId.isNotEmpty()) {
                deleteFavorite(movie, sessionId)
            }
        }

            Picasso.get().load(IMG_URL + movie.backdropPath).into(binding.ivPoster)
            binding.tvTitle.text = movie.title
            binding.tvOverview.text = movie.overview

    }

    private fun deleteFavorite(movie: Movie, sessionId: String) {

        scope.launch {

            val postMovie = PostMovie(media_id = movie.id, favorite = false)
            apiService.addFavorite(
                session_id = sessionId,
                postMovie = postMovie
            )
        }
    }

    private fun parseArgs() {

        requireArguments().getParcelable<Movie>(KEY_MOVIE)?.let {
            movie = it
        }
    }

    companion object {

        private const val IMG_URL = "https://image.tmdb.org/t/p/w500"
        const val KEY_MOVIE = "Movie"
    }
}