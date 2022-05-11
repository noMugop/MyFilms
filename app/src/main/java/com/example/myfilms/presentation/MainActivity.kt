package com.example.myfilms.presentation

import android.R.id
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
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
import com.example.myfilms.presentation.adapter.MoviesAdapter
import com.example.myfilms.presentation.fragments.login.LoginFragment
import com.example.myfilms.presentation.fragments.movies.MoviesFragment
import com.example.myfilms.presentation.fragments.movies.ViewModelMovie
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
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
    private lateinit var userName: TextView
    private lateinit var userAvatar: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        bottomNavInit()
        sideBarInit()
        setActivity()
        observeAccountDetails()
    }

    private fun setActivity() {
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.moviesFragment -> {
                    viewModel.getUser()
                    bottomNavigation.visibility = View.VISIBLE
                    toolbarLayout.visibility = View.VISIBLE
                }
                R.id.favoritesFragment -> {
                    viewModel.getUser()
                    bottomNavigation.visibility = View.VISIBLE
                    toolbarLayout.visibility = View.VISIBLE
                }
                R.id.detailsFragment -> {
                    bottomNavigation.visibility = View.GONE
                    toolbarLayout.visibility = View.GONE
                }
                R.id.loginFragment -> {
                    drawerLayout.closeDrawers()
                    viewModel.cleanUser()
                    userAvatar.setImageResource(0)
                    bottomNavigation.visibility = View.GONE
                    toolbarLayout.visibility = View.GONE
                }
                R.id.settingsFragment -> {
                    viewModel.getUser()
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

    private fun observeAccountDetails() {
        val header = sideBar.getHeaderView(0)
        userName = header.findViewById(R.id.tvName)
        userAvatar = header.findViewById(R.id.ivAvatar)

        viewModel.user.observe(this) {
            if (!it?.name.isNullOrBlank()) {
                userName.text = it?.name
            } else {
                userName.text = it?.username
            }
            if (!it?.avatar.isNullOrBlank()) {
                Picasso.get().load(IMG_URL + it?.avatar).into(userAvatar)
            }
        }
    }

    private fun sideBarInit() {

        setSupportActionBar(toolbar)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.movies_nav,
                R.id.favorites_nav,
                R.id.settings,
                R.id.login_nav,
                R.id.about,
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
                    val current = findCurrentFragmentId()
                    if (current != R.id.settingsFragment) {
                        navController.popBackStack(current, false)
                        navController.navigate(R.id.settings_nav)
                    }
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

                R.id.about -> {
                    Toast.makeText(this, "About", Toast.LENGTH_SHORT).show()
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

    companion object {

        private const val IMG_URL = "https://image.tmdb.org/t/p/w500"
    }
}