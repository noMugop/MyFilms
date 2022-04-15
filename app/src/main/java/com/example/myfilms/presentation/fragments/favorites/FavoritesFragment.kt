package com.example.myfilms.presentation.fragments.favorites

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.myfilms.R
import com.example.myfilms.databinding.FragmentFavoritesBinding
import com.example.myfilms.presentation.adapter.favorites_adapter.FavoritesAdapter
import com.example.myfilms.data.models.Movie
import com.example.myfilms.presentation.Utils.LoadingState
import com.example.myfilms.presentation.fragments.detailsTrue.FavoriteDetailsFragment
import com.example.myfilms.presentation.fragments.login.LoginFragment
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding: FragmentFavoritesBinding
        get() = _binding ?: throw RuntimeException("FavoritesFragment is null")

    private val adapter = FavoritesAdapter()
    private lateinit var viewModel: ViewModelFavorites

    private lateinit var prefSettings: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

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

        getSessionId()
        initAndObserveViewModel()
        onMovieClickListener()
        onBackPressed()

    }

    //создаем ViewModel и устанвливаем observe на наши LiveData
    private fun initAndObserveViewModel() {

        //создаем ViewModel текущего фрагмента
        viewModel =
            ViewModelProvider(this)[ViewModelFavorites(requireActivity().application)::class.java]

        //один раз прогружаем данные (это можно сделать внутри ViewModel через init(), но в моем случае так не получистя)
        viewModel.downloadData(sessionId, PAGE)

        //следим за loadingState внутри ViewModel
        viewModel.loadingState.observe(viewLifecycleOwner) {
            when (it) {
                LoadingState.IS_LOADING -> binding.progressBar.visibility = View.VISIBLE
                LoadingState.FINISHED -> binding.progressBar.visibility = View.GONE
                //если loadingState == SUCCESS, то устанавливаем слежку за movies
                LoadingState.SUCCESS -> viewModel.movies.observe(viewLifecycleOwner) {
                    //если данные в movie изменились, то отправляем их в adapter, чтобы обновить RecyclerView
                    adapter.submitList(it)
                    binding.rvFavorites.adapter = adapter
                }
                else -> throw RuntimeException("Error")
            }
        }
    }

    //получить session id из SharedPreference
    private fun getSessionId() {
        try {
            sessionId = prefSettings.getString(LoginFragment.SESSION_ID_KEY, null) as String
        } catch (e: Exception) {
        }
    }

    //следим за нажатием на фильм
    private fun onMovieClickListener() {
        adapter.onFilmClickListener = object : FavoritesAdapter.OnFilmClickListener {
            override fun onFilmClick(movie: Movie) {
                launchDetailFragment(movie.id)
            }
        }
    }

    //перейти в детали, если сработал onMovieClickListener()
    private fun launchDetailFragment(movieId: Int) {
        val args = Bundle().apply {
            putInt(FavoriteDetailsFragment.KEY_MOVIE, movieId)
        }
        findNavController().navigate(
            R.id.action_favorites_fragment_to_favorite_details_fragment,
            args
        )
    }

    //переопределяем нажатие на кнопку назад
    //при нажатии на кнопку назад удаляем session id и переходим на экран авторизации
    private fun onBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                try {
                    viewModel.deleteSession(sessionId)
                    editor.clear().commit()
                    findNavController().popBackStack()
                } catch (e: Exception) {
                    findNavController().popBackStack()
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