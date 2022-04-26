package com.example.myfilms.presentation.fragments.favorite.favorite_adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.myfilms.databinding.ItemMovieBinding
import com.example.myfilms.data.models.Movie
import com.squareup.picasso.Picasso

class FavoritesAdapter : ListAdapter<Movie, FavoriteMovieViewHolder>(FavoriteMovieDiffCallback) {

    var onFilmClickListener: OnFilmClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteMovieViewHolder {
        return FavoriteMovieViewHolder(
            ItemMovieBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holderFavorite: FavoriteMovieViewHolder, position: Int) {
        val movie = getItem(position)
        with(holderFavorite.binding) {

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
}