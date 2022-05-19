package com.example.myfilms.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.example.myfilms.databinding.PartDefaultLoadStateBinding

class NewLoadingStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<NewLoadingViewHolder>() {

    override fun onBindViewHolder(holder: NewLoadingViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): NewLoadingViewHolder {
        return NewLoadingViewHolder(
            PartDefaultLoadStateBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), retry
        )
    }
}