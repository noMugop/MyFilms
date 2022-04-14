package com.example.myfilms.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myfilms.R
import com.example.myfilms.databinding.ActivityMainBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var bottomNavigation: BottomNavigationView
    //    private lateinit var appBarConfiguration: AppBarConfiguration

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
                R.id.filmsFragment -> {
                    bottomNavigation.visibility = View.VISIBLE
                    binding.appBarMain.toolbarLayout.visibility = View.VISIBLE
                }
                R.id.favoritesFragment -> {
                    bottomNavigation.visibility = View.VISIBLE
                    binding.appBarMain.toolbarLayout.visibility = View.VISIBLE
                }
                R.id.detailsFragment -> {
                    bottomNavigation.visibility = View.GONE
                    binding.appBarMain.toolbarLayout.visibility = View.VISIBLE
                }
                R.id.loginFragment -> {
                    bottomNavigation.visibility = View.GONE
                    binding.appBarMain.toolbarLayout.visibility = View.GONE
                }
            }
        }
    }

    private fun init() {

        navController = findNavController(R.id.main_container)
//        setSupportActionBar(binding.appBarMain.topToolbar)
        bottomNavigation = binding.appBarMain.contentFragments.bottomNavigationView
    }

    private fun sideBarInit() {

        binding.sideNavigation.setupWithNavController(navController)

        binding.appBarMain.topToolbar.setNavigationOnClickListener {
            it.setOnClickListener {
                binding.drawerMainActivity.openDrawer(GravityCompat.START)
            }
        }
    }

    private fun bottomNavInit() {
        bottomNavigation.labelVisibilityMode =
            NavigationBarView.LABEL_VISIBILITY_LABELED
        bottomNavigation.setupWithNavController(navController)
    }

//    override fun onSupportNavigateUp(): Boolean {
//        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
//    }
}