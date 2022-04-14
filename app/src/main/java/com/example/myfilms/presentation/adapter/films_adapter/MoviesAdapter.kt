package com.example.myfilms.presentation.adapter.films_adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.myfilms.databinding.ItemMovieBinding
import com.example.myfilms.data.models.Movie
import com.squareup.picasso.Picasso

class MoviesAdapter : ListAdapter<Movie, MovieViewHolder>(MovieDiffCallback) {

    var onFilmClickListener: OnFilmClickListener? = null
    var onReachEndListener: OnReachEndListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder(
            ItemMovieBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        if (position > (itemCount - 2) && onReachEndListener != null) {
                onReachEndListener?.onReachEnd()
        }

        with(holder.binding) {
            Picasso.get().load(IMG_URL + movie.posterPath).into(ivMovie)
            movieItemID.setOnClickListener {

                onFilmClickListener?.onFilmClick(movie)
            }
        }
    }

    companion object {

        private const val IMG_URL = "https://image.tmdb.org/t/p/w500"
    }

    interface OnFilmClickListener {

        fun onFilmClick(movie: Movie)
    }

    interface OnReachEndListener {

        fun onReachEnd()
    }
}