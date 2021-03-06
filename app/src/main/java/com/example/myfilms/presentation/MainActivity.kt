package com.example.myfilms.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myfilms.R
import com.example.myfilms.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val navController by lazy {
        findNavController(R.id.main_container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        bottomBarVisibility()
    }

    private fun bottomBarVisibility() {
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.filmsFragment -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }
                R.id.favoritesFragment -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                }
                R.id.detailsFragment -> {
                    binding.bottomNavigationView.visibility = View.GONE
                }
            }
        }
    }

    private fun init() {
        binding.bottomNavigationView.labelVisibilityMode =
            NavigationBarView.LABEL_VISIBILITY_LABELED
        binding.bottomNavigationView.setupWithNavController(navController)
    }
}