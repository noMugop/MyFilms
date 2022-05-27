package com.example.myfilms.presentation.adapter.loading_adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.example.myfilms.databinding.LayoutLoadStateBinding

//typealias RetryAction = () -> Unit

class NewLoadingStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<LoadStateViewHolder>() {

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        return LoadStateViewHolder(
            LayoutLoadStateBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), null, null, retry
        )
    }
}