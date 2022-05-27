package com.example.myfilms.presentation.adapter.movie_adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.example.myfilms.databinding.ItemMovieBinding
import com.example.myfilms.data.database.model.movie.MovieDbModel
import com.squareup.picasso.Picasso

class MoviesAdapter : PagingDataAdapter<MovieDbModel, MovieViewHolder>(MovieDiffCallback) {

    var onFilmClickListener: OnFilmClickListener? = null

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
        val movie = getItem(position) ?: return
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

        fun onFilmClick(movieDbModel: MovieDbModel)
    }
}