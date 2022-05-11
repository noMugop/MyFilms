package com.example.myfilms.presentation

import android.R.id
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.R.id.nav_host_fragment_container
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myfilms.R
import com.example.myfilms.data.repository.Repository
import com.example.myfilms.databinding.ActivityMainBinding
import com.example.myfilms.presentation.fragments.login.LoginFragment
import com.example.myfilms.presentation.fragments.movies.MoviesFragment
import com.example.myfilms.presentation.fragments.movies.ViewModelMovie
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    private lateinit var navController: NavController
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var toolbarLayout: AppBarLayout
    private lateinit var toolbar: MaterialToolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var sideBar: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        bottomNavInit()
        sideBarInit()
        setVisibility()
    }

    private fun setVisibility() {
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.moviesFragment -> {
                    bottomNavigation.visibility = View.VISIBLE
                    toolbarLayout.visibility = View.VISIBLE
                }
                R.id.favoritesFragment -> {
                    bottomNavigation.visibility = View.VISIBLE
                    toolbarLayout.visibility = View.VISIBLE
                }
                R.id.detailsFragment -> {
                    bottomNavigation.visibility = View.GONE
                    toolbarLayout.visibility = View.GONE
                }
                R.id.loginFragment -> {
                    bottomNavigation.visibility = View.GONE
                    toolbarLayout.visibility = View.GONE
                }
            }
        }
    }

    private fun init() {

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[MainViewModel::class.java]

        navController = findNavController(R.id.main_container)
        bottomNavigation = binding.contentMain.bottomNavigation
        toolbarLayout = binding.contentMain.toolbarLayout
        toolbar = binding.contentMain.topToolbar
        drawerLayout = binding.drawerMainActivity
        sideBar = binding.sideNavigation
    }

    private fun sideBarInit() {



        setSupportActionBar(toolbar)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.movies_nav,
                R.id.favorites_nav,
                R.id.login_nav,
                R.id.settings,
                R.id.about,
                R.id.share,
                R.id.rate_us
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        sideBar.setupWithNavController(navController)

        sideBar.menu.findItem(R.id.movies_nav).isCheckable = false
        sideBar.setNavigationItemSelectedListener {

            drawerLayout.closeDrawers()
            it.isCheckable = false

            when (it.itemId) {
                R.id.movies_nav -> {
                    val current = findCurrentFragmentId()
                    if (current != R.id.moviesFragment) {
                        navController.popBackStack(R.id.loginFragment, false)
                        navController.navigate(R.id.movies_nav)
                    }
                }
                R.id.favorites_nav -> {
                    val current = findCurrentFragmentId()
                    if (current != R.id.favoritesFragment) {
                        navController.popBackStack(R.id.loginFragment, false)
                        navController.navigate(R.id.favorites_nav)
                    }
                }
                R.id.settings -> {
                    Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                }
                R.id.about -> {
                    Toast.makeText(this, "About", Toast.LENGTH_SHORT).show()
                }
                R.id.login_nav -> {
                    this.let {
                        AlertDialog
                            .Builder(it)
                            .setMessage("Выйти?")
                            .setPositiveButton("Да") { dialogInterface, i ->
                                viewModel.deleteSession()
                                navController.popBackStack(R.id.loginFragment, false)
                                navController.navigate(R.id.login_nav)
                            }
                            .setNegativeButton("Нет") { dialogInterface, i -> }
                            .create()
                            .show()
                    }
                }
                R.id.share -> {
                    Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show()
                }
                R.id.rate_us -> {
                    Toast.makeText(this, "Rate us", Toast.LENGTH_SHORT).show()
                }
                else -> throw RuntimeException("Wrong id")
            }

            return@setNavigationItemSelectedListener true
        }
    }

    private fun findCurrentFragmentId(): Int {
        return navController.currentDestination?.id as Int
    }

//    найти фрагмент по id
//    private fun findCurrentFragmentById(int: Int): Fragment {
//        return supportFragmentManager.findFragmentById(int) as Fragment
//    }

    private fun bottomNavInit() {
        bottomNavigation.labelVisibilityMode =
            NavigationBarView.LABEL_VISIBILITY_LABELED
        bottomNavigation.setupWithNavController(navController)

        bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.movies_nav -> {
                    val current = findCurrentFragmentId()
                    if (current != R.id.moviesFragment) {
                        navController.popBackStack(R.id.loginFragment, false)
                        navController.navigate(R.id.movies_nav)
                    }
                }
                R.id.favorites_nav -> {
                    val current = findCurrentFragmentId()
                    if (current != R.id.favoritesFragment) {
                        navController.popBackStack(R.id.loginFragment, false)
                        navController.navigate(R.id.favorites_nav)
                    }
                }
            }
            return@setOnItemSelectedListener true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}