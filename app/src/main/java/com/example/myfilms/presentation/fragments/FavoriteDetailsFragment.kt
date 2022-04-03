package com.example.myfilms.presentation.fragments

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

    private var movie: MutableLiveData<Movie> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            deleteFavorite(movie.value as Movie, "")
        }

        movie.observe(viewLifecycleOwner) {

            Picasso.get().load(IMG_URL + it.backdropPath).into(binding.ivPoster)
            binding.tvTitle.text = it.title
            binding.tvOverview.text = it.overview
        }

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
            movie.value = it
        }
    }

    companion object {

        private const val IMG_URL = "https://image.tmdb.org/t/p/w500"
        const val KEY_MOVIE = "Movie"
    }
}