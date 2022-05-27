package com.example.myfilms.presentation.adapter.loading_adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.myfilms.databinding.LayoutErrorBinding
import com.example.myfilms.databinding.LayoutLoadStateBinding

class LoadStateViewHolder(
    private val binding: LayoutLoadStateBinding,
    private val tvError: TextView?,
    private val swipeRefresh: SwipeRefreshLayout?,
    retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.ivRetry.setOnClickListener {
            retry.invoke()
        }
    }

    fun bind(loadState: LoadState) {
        with(binding) {
            if (tvError?.text.isNullOrBlank()) {
                ivRetry.isVisible = loadState is LoadState.Error
            }
            if (swipeRefresh != null) {
                swipeRefresh.isRefreshing = loadState is LoadState.Loading
                progressBar.isVisible = false
            } else {
                progressBar.isVisible = loadState is LoadState.Loading
            }
        }
    }
}