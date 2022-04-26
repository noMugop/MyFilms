package com.example.myfilms.presentation.fragments.details

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myfilms.R
import com.example.myfilms.data.repository.Repository
import com.example.myfilms.databinding.FragmentDetailsBinding
import com.example.myfilms.presentation.Utils.LoadingState
import com.example.myfilms.presentation.fragments.login.LoginFragment
import com.squareup.picasso.Picasso
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding: FragmentDetailsBinding
        get() = _binding ?: throw RuntimeException("DetailsFragment is null")

    private lateinit var viewModel: ViewModelDetails

    private lateinit var prefSettings: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefSettings = context?.getSharedPreferences(
            Repository.APP_SETTINGS, Context.MODE_PRIVATE
        ) as SharedPreferences
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
        initViewModel()
        getMovieById(movieId)
        onFavoriteClickListener()
        onTrailerClick()
    }

    private fun getSessionId() {
        try {
            sessionId = prefSettings.getString(Repository.SESSION_ID_KEY, null) as String
        } catch (e: java.lang.Exception) {
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            AndroidViewModelFactory.getInstance(requireActivity().application)
        )[ViewModelDetails::class.java]
    }

    private fun getMovieById(movieId: Int) {

        viewModel.getMovieById(movieId)
        viewModel.loadingState.observe(viewLifecycleOwner) {
            when (it) {
                LoadingState.IS_LOADING -> binding.progressBar.visibility = View.VISIBLE
                LoadingState.FINISHED -> binding.progressBar.visibility = View.GONE
                LoadingState.SUCCESS -> {
                    viewModel.movie.observe(viewLifecycleOwner) {
                        Picasso.get().load(DetailsFragment.IMG_URL + it.backdropPath)
                            .into(binding.ivPoster)
                        binding.tvTitle.text = it.title
                        binding.tvOverview.text = it.overview
                    }
                    viewModel.trailer.observe(viewLifecycleOwner) {
                        it.list.map {
                            binding.textViewNameOfVideo.text = it.name
                        }
                    }
                }
                else -> throw RuntimeException("Error")
            }
        }
    }

    private fun onTrailerClick() {

        binding.clTrailer.setOnClickListener {
            getTrailer()
        }
    }

    private fun onFavoriteClickListener() {

        binding.ivAddFavorite.setOnClickListener {

            if (binding.ivAddFavorite.tag == TAG_WHITE) {
                addFavorite(movieId, sessionId)
            } else {
                deleteFavorite(movieId, sessionId)
            }
        }
    }

    private fun deleteFavorite(movieId: Int, sessionId: String) {

        viewModel.deleteFavorites(movieId, sessionId)

        viewModel.addFavoriteState.observe(viewLifecycleOwner) {
            when (it) {
                LoadingState.SUCCESS -> {
                    binding.ivAddFavorite.setImageResource(R.drawable.ic_star_white)
                    binding.ivAddFavorite.tag = TAG_WHITE
                }
                LoadingState.FINISHED -> {
                    Toast.makeText(context, "Требуется авторизация", Toast.LENGTH_SHORT).show()
                }
                else -> Log.d("Message", "Message")
            }
        }
    }

    private fun addFavorite(movieId: Int, sessionId: String) {

        viewModel.addFavorite(movieId, sessionId)
        viewModel.addFavoriteState.observe(viewLifecycleOwner) {
            when (it) {
                LoadingState.SUCCESS -> {
                    binding.ivAddFavorite.setImageResource(R.drawable.ic_star_yellow)
                    binding.ivAddFavorite.tag = TAG_YELLOW
                }
                LoadingState.FINISHED -> {
                    Toast.makeText(context, "Требуется авторизация", Toast.LENGTH_SHORT).show()
                }
                else -> Log.d("Message", "Message")
            }
        }
    }

    private fun getTrailer() {
        viewModel.trailer.observe(viewLifecycleOwner) {
            it.list.map {
                key = it.key
            }
        }
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.data = Uri.parse(YOUTUBE_URL + key)
        startActivity(intent)
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