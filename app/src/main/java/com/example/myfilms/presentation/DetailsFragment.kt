package com.example.myfilms.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import com.example.myfilms.R
import com.example.myfilms.data.ApiFactory
import com.example.myfilms.databinding.FragmentDetailsBinding
import com.example.myfilms.databinding.FragmentFavoritesBinding
import com.example.myfilms.data.models.LoginApprove
import com.example.myfilms.data.models.Movie
import com.example.myfilms.data.models.PostMovie
import com.example.myfilms.data.models.Session
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.RuntimeException


class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding: FragmentDetailsBinding
        get() = _binding ?: throw RuntimeException("DetailsFragment is null")

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
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getSessionId()

        binding.ivFavorite.setOnClickListener {
            binding.ivFavorite.setImageResource(R.mipmap.ic_favorite)

            sessionId.observe(viewLifecycleOwner) {
                addFavorite(movie.value as Movie)
            }
        }

        movie.observe(viewLifecycleOwner) {

            Picasso.get().load(IMG_URL + it.backdropPath).into(binding.ivPoster)
            binding.tvTitle.text = it.title
            binding.tvOverview.text = it.overview
        }

    }

    private fun getSessionId() {

        scope.launch {

            val tokenNotVal = apiService.getToken()
            val loginApprove = LoginApprove(request_token = tokenNotVal.request_token)
            val tokenVal = apiService.approveToken(loginApprove = loginApprove)
            sessionId.value = apiService.createSession(token = tokenVal)
        }
    }

    private fun addFavorite(movie: Movie) {

        scope.launch {

            val session = sessionId.value as Session
            val postMovie = PostMovie(media_id = movie.id, favorite = true)
            apiService.addFavorite(
                session_id = session.session_id,
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
        private var sessionId: MutableLiveData<Session> = MutableLiveData()
    }
}