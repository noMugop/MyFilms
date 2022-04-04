package com.example.myfilms.presentation.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.example.myfilms.R
import com.example.myfilms.data.ApiFactory
import com.example.myfilms.databinding.FragmentFavoritesBinding
import com.example.myfilms.presentation.adapter.favorites_adapter.FavoritesAdapter
import com.example.myfilms.data.models.LoginApprove
import com.example.myfilms.data.models.Movie
import com.example.myfilms.data.models.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

class FavoritesFragment : Fragment(), CoroutineScope {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding: FragmentFavoritesBinding
        get() = _binding ?: throw RuntimeException("FavoritesFragment is null")

    override val coroutineContext: CoroutineContext = Dispatchers.Main
    private val adapter = FavoritesAdapter()
    private val apiService = ApiFactory.getInstance()

    private lateinit var prefSettings: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private var movies: MutableLiveData<List<Movie>> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        prefSettings = context?.getSharedPreferences(
            LoginFragment.APP_SETTINGS, Context.MODE_PRIVATE
        ) as SharedPreferences
        editor = prefSettings.edit()
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

        downloadData()
        onMovieClickListener()
        onBackPressed()

        movies.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.rvFavorites.adapter = adapter
        }
    }

    private fun downloadData() {

        binding.progressBar.visibility = View.VISIBLE
        try {
            sessionId = prefSettings.getString(LoginFragment.SESSION_ID_KEY, null) as String
        } catch (e: Exception) {
        }
        launch {
            try {
                movies.value = apiService.getFavorites(
                    session_id = sessionId,
                    page = PAGE
                ).movies

            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Требуется авторизация",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.progressBar.visibility = View.GONE
    }

    private fun onMovieClickListener() {

        adapter.onFilmClickListener = object : FavoritesAdapter.OnFilmClickListener {
            override fun onFilmClick(movie: Movie) {
                launchDetailFragment(movie.id)
            }
        }
    }

    private fun launchDetailFragment(movieId: Int) {
        val args = Bundle().apply {
            putInt(FavoriteDetailsFragment.KEY_MOVIE, movieId)
        }
        findNavController().navigate(R.id.action_favoritesFragment_to_favoriteDetailsFragment, args)
    }

    private fun onBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                launch {
                    try {
                        apiService.deleteSession(sessionId = Session(session_id = sessionId))
                        editor.clear().commit()
                        findNavController().popBackStack()
                    } catch (e: Exception) {
                        findNavController().popBackStack()
                    }
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    companion object {

        private var sessionId: String = ""
        private var PAGE = 1
    }
}