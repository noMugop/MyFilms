package com.example.myfilms.presentation.adapter

import android.view.View
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.myfilms.databinding.PartDefaultLoadStateBinding

class NewLoadingViewHolder(
    private val binding: PartDefaultLoadStateBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.ivRetry.setOnClickListener {
            retry.invoke()
        }
    }

    fun bind(loadState: LoadState) {
        binding.ll.apply {
            with(binding) {
                progressBar.isVisible = loadState is LoadState.Loading
                ivRetry.isVisible = loadState !is LoadState.Loading
            }
        }
    }
}