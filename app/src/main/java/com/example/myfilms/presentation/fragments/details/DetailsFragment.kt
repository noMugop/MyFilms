package com.example.myfilms.presentation.fragments.details

import android.content.Intent
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
import com.example.myfilms.databinding.FragmentDetailsBinding
import com.example.myfilms.presentation.utils.LoadingState
import com.squareup.picasso.Picasso
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.example.myfilms.data.models.movie.Movie
import com.example.myfilms.presentation.fragments.favorites.FavoritesViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding: FragmentDetailsBinding
        get() = _binding ?: throw RuntimeException("DetailsFragment is null")

    private val viewModel by viewModel<DetailsViewModel>()

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

        getMovieById(movie.id as Int)
        onFavoriteClickListener()
        onTrailerClickListener()
    }

    private fun getMovieById(movieId: Int) {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.getMovieById(movieId)
        viewModel.loadingState.observe(viewLifecycleOwner) {
            when (it) {
                LoadingState.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    viewModel.movie.observe(viewLifecycleOwner) {
                        if (it != null) {
                            Picasso.get().load(IMG_URL + it.backdropPath)
                                .into(binding.ivPoster)
                            binding.tvTitle.text = it.title
                            binding.tvOverview.text = it.overview
                            binding.tvRate.text =
                                getString(R.string.rate, it.voteAverage.toString())
                            binding.ivAddFavorite.setImageResource(R.drawable.ic_star_yellow)
                            binding.ivAddFavorite.tag = TAG_YELLOW
                        } else {
                            Picasso.get().load(IMG_URL + movie.backdropPath)
                                .into(binding.ivPoster)
                            binding.tvTitle.text = movie.title
                            binding.tvOverview.text = movie.overview
                            binding.tvRate.text =
                                getString(R.string.rate, movie.voteAverage.toString())
                            binding.ivAddFavorite.setImageResource(R.drawable.ic_star_white)
                            binding.ivAddFavorite.tag = TAG_WHITE
                        }
                        viewModel.trailer.observe(viewLifecycleOwner) {
                            it.list?.map { binding.textViewNameOfVideo.text = it.name }
                        }
                    }
                }
                else -> {}
            }
        }
    }

    private fun onTrailerClickListener() {
        binding.clTrailer.setOnClickListener {
            getTrailer()
        }
    }

    private fun onFavoriteClickListener() {
        binding.ivAddFavorite.setOnClickListener {
            if (binding.ivAddFavorite.tag == TAG_WHITE) {
                addFavorite()
            } else {
                deleteFavorite()
            }
        }
    }

    private fun deleteFavorite() {
        viewModel.deleteFavorites(movie)
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

    private fun addFavorite() {
        viewModel.addFavorite(movie)
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
            it.list?.map {
                key = it.key
            }
        }
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.data = Uri.parse(YOUTUBE_URL + key)
        startActivity(intent)
    }

    private fun parseArgs() {
        requireArguments().getParcelable<Movie>(KEY_MOVIE).apply {
            movie = this as Movie
        }
    }

    companion object {

        private const val FAVORITE = true
        private var movie = Movie()
        private var key: String = ""
        private const val YOUTUBE_URL = "https://www.youtube.com/watch?v="
        private const val TAG_WHITE = "white"
        private const val TAG_YELLOW = "yellow"
        private const val IMG_URL = "https://image.tmdb.org/t/p/w500"
        const val KEY_MOVIE = "Movie details"
    }
}