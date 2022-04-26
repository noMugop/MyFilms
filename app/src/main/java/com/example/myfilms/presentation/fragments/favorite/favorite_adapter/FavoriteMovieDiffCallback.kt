package com.example.myfilms.presentation.fragments.favorite.favorite_adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.myfilms.data.models.Movie

object FavoriteMovieDiffCallback: DiffUtil.ItemCallback<Movie>() {

    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem == newItem
    }
}