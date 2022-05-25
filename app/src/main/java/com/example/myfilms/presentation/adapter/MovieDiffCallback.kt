package com.example.myfilms.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.myfilms.data.database.model.movie.MovieDbModel

object MovieDiffCallback: DiffUtil.ItemCallback<MovieDbModel>() {

    override fun areItemsTheSame(oldItem: MovieDbModel, newItem: MovieDbModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MovieDbModel, newItem: MovieDbModel): Boolean {
        return oldItem == newItem
    }
}