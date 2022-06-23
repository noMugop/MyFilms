package com.example.myfilms.presentation.fragments.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myfilms.databinding.FragmentSearchBinding
import com.example.myfilms.presentation.adapter.loading_adapter.LoadStateViewHolder
import com.example.myfilms.presentation.adapter.loading_adapter.NewLoadingStateAdapter
import com.example.myfilms.presentation.adapter.movie_adapter.MoviesAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding: FragmentSearchBinding
        get() = _binding ?: throw RuntimeException("FragmentSearchBinding is null")

    private val viewModel by viewModel<SearchViewModel>()
    private val adapter = MoviesAdapter()
    private lateinit var loadStateViewHolder: LoadStateViewHolder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setObservers()
        setListeners()
    }

    private fun init() {
        binding.rvSearch.adapter = adapter
    }

    private fun setObservers() {
        lifecycleScope.launch {
            viewModel.moviesFlow.collectLatest {
                adapter.submitData(it)
            }
        }
    }

    private fun setListeners() {
        binding.searchEditText.addTextChangedListener {
            viewModel.setSearchBy(it.toString())
        }
    }
}