package com.example.myfilms.presentation.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.example.myfilms.R
import com.example.myfilms.data.ApiFactory
import com.example.myfilms.databinding.FragmentFavoritesBinding
import com.example.myfilms.presentation.adapter.favorites_adapter.FavoritesAdapter
import com.example.myfilms.data.models.LoginApprove
import com.example.myfilms.data.models.Movie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.RuntimeException

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding: FragmentFavoritesBinding
        get() = _binding ?: throw RuntimeException("FavoritesFragment is null")

    private val adapter = FavoritesAdapter()
    private val apiService = ApiFactory.getInstance()
    private val scope = CoroutineScope(Dispatchers.Main)

    private lateinit var prefSettings: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        prefSettings = context?.getSharedPreferences(LoginFragment.APP_SETTINGS, Context.MODE_PRIVATE) as SharedPreferences
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBar.visibility = View.VISIBLE

        val sessionId = prefSettings.getString(LoginFragment.SESSION_ID_KEY, null) as String

        if (sessionId.isNotEmpty()) {
            downloadData(sessionId)
        }

        data.observe(viewLifecycleOwner) {

            adapter.submitList(it)
        }

        adapter.onFilmClickListener = object : FavoritesAdapter.OnFilmClickListener {
            override fun onFilmClick(movie: Movie) {
                launchDetailFragment(movie)
            }
        }

        binding.rvFavorites.adapter = adapter
    }

    private fun downloadData(sessionId: String) {

        scope.launch {

            data.value = apiService.getFavorites(
                session_id = sessionId,
                page = PAGE
            ).movies
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun launchDetailFragment(movie: Movie) {
        val args = Bundle().apply {
            putParcelable(FavoriteDetailsFragment.KEY_MOVIE, movie)
        }
        findNavController().navigate(R.id.action_favoritesFragment_to_favoriteDetailsFragment, args)
    }

    companion object {

        private var PAGE = 1
        private var data: MutableLiveData<List<Movie>> = MutableLiveData()
    }
}