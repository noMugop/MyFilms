package com.example.myfilms.presentation

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myfilms.R
import com.example.myfilms.data.network.ApiService
import com.example.myfilms.databinding.ActivityMainBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityRetainedScope
import org.koin.androidx.scope.activityScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModel<MainViewModel>()

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
                    if (viewModel.getSession().isNotBlank()) {
                        viewModel.getUser()
                    } else {
                        deleteAll()
                    }
                    bottomNavigation.visibility = View.VISIBLE
                    toolbarLayout.visibility = View.VISIBLE
                }
                R.id.favoritesFragment -> {
                    if (viewModel.getSession().isNotBlank()) {
                        viewModel.getUser()
                    } else {
                        deleteAll()
                    }
                    bottomNavigation.visibility = View.VISIBLE
                    toolbarLayout.visibility = View.VISIBLE
                }
                R.id.detailsFragment -> {
                    bottomNavigation.visibility = View.GONE
                    toolbarLayout.visibility = View.GONE
                }
                R.id.loginFragment -> {
                    bottomNavigation.visibility = View.VISIBLE
                    toolbarLayout.visibility = View.GONE
                }
                R.id.settingsFragment -> {
                    bottomNavigation.visibility = View.GONE
                    toolbarLayout.visibility = View.GONE
                }
            }
        }
    }

    private fun init() {

        navController = findNavController(R.id.main_container)
        bottomNavigation = binding.contentMain.bottomNavigation
        toolbarLayout = binding.contentMain.toolbarLayout
        toolbar = binding.contentMain.topToolbar
        drawerLayout = binding.drawerMainActivity
        sideBar = binding.sideNavigation
        //получить доступ к side_nav_header.xml
        val header = sideBar.getHeaderView(0)
        userName = header.findViewById(R.id.tvName)
        userAvatar = header.findViewById(R.id.iv_avatar)
    }

    private fun observeAccountDetails() {

        viewModel.user.observe(this) {
            if (!it?.name.isNullOrBlank()) {
                userName.text = it?.name
            } else {
                userName.text = it?.username
            }

            if (!it?.avatar.isNullOrBlank() && it?.avatar_uri.isNullOrBlank()) {
                Picasso.get().load(IMG_URL + it?.avatar).into(userAvatar)
            } else if (!it?.avatar_uri.isNullOrBlank()) {
                val uri = Uri.parse(it?.avatar_uri)
                userAvatar.setImageURI(uri)
            } else {
                Picasso.get().load(R.drawable.empty_avatar).into(userAvatar)
            }
        }
    }

    private fun sideBarInit() {

        setSupportActionBar(toolbar)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.movies_nav,
                R.id.favorites_nav,
                R.id.login_nav,
                R.id.exit,
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

            val currentFragment = findCurrentFragmentId()

            when (it.itemId) {
                R.id.movies_nav -> {
                    if (currentFragment != R.id.moviesFragment) {
                        navController.popBackStack(R.id.moviesFragment, false)
                    }
                }
                R.id.favorites_nav -> {
                    if (currentFragment != R.id.favoritesFragment) {
                        navController.popBackStack(R.id.moviesFragment, false)
                        navController.navigate(R.id.favorites_nav)
                    }
                }
                R.id.login_nav -> {
                    if (currentFragment != R.id.loginFragment) {
                        navController.popBackStack(R.id.moviesFragment, false)
                        navController.navigate(R.id.login_nav)
                    }
                }
                R.id.exit -> {
                    this.let {
                        AlertDialog
                            .Builder(it)
                            .setMessage("Выйти?")
                            .setPositiveButton("Да") { dialogInterface, i ->
                                viewModel.deleteMainSession()
                                finish()
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

            val currentFragment = findCurrentFragmentId()

            when (it.itemId) {
                R.id.movies_nav -> {
                    if (currentFragment != R.id.moviesFragment) {
                        navController.popBackStack(R.id.moviesFragment, false)
                    }
                }
                R.id.favorites_nav -> {
                    if (currentFragment != R.id.favoritesFragment) {
                        navController.popBackStack(R.id.moviesFragment, false)
                        navController.navigate(R.id.favorites_nav)
                    }
                }
                R.id.login_nav -> {
                    if (currentFragment != R.id.loginFragment) {
                        navController.popBackStack(R.id.moviesFragment, false)
                        navController.navigate(R.id.login_nav)
                    }
                }
            }
            return@setOnItemSelectedListener true
        }
    }

    private fun deleteAll() {
        viewModel.cleanUser()
        userAvatar.setImageResource(R.drawable.empty_avatar)
//        Picasso.get().load(R.drawable.empty_avatar).into(userAvatar)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    companion object {

        private const val IMG_URL = "https://image.tmdb.org/t/p/w500"
    }
}