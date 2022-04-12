package com.example.myfilms.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myfilms.R
import com.example.myfilms.databinding.ActivityMainBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = findNavController(R.id.main_container)
        bottomBarInit()
        sideBarInit()
        setVisibility()
    }

    private fun setVisibility() {
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.filmsFragment -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                    binding.sideNavigation.visibility = View.VISIBLE
                }
                R.id.favoritesFragment -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                    binding.sideNavigation.visibility = View.VISIBLE
                }
                R.id.detailsFragment -> {
                    binding.bottomNavigationView.visibility = View.GONE
                    binding.sideNavigation.visibility = View.GONE
                }
                R.id.loginFragment -> {
                    binding.bottomNavigationView.visibility = View.GONE
                    binding.sideNavigation.visibility = View.GONE
                }
            }
        }
    }

    private fun sideBarInit() {

        binding.sideNavigation.setNavigationItemSelectedListener(this)

        binding.topAppbar.setNavigationOnClickListener { object : View.OnClickListener {
            override fun onClick(p0: View?) {
                binding.drawerMainActivity.openDrawer(GravityCompat.START)
            }
        } }
//        binding.sideNavigation.setupWithNavController(navController)
    }

    private fun bottomBarInit() {
        binding.bottomNavigationView.labelVisibilityMode =
            NavigationBarView.LABEL_VISIBILITY_LABELED
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.side_films -> Toast.makeText(applicationContext, "Home", Toast.LENGTH_SHORT).show()
            R.id.side_favorite -> Toast.makeText(applicationContext, "Favorites", Toast.LENGTH_SHORT).show()
            R.id.settings -> Toast.makeText(applicationContext, "Settings", Toast.LENGTH_SHORT).show()
            R.id.about -> Toast.makeText(applicationContext, "About", Toast.LENGTH_SHORT).show()
            R.id.exit -> Toast.makeText(applicationContext, "Exit", Toast.LENGTH_SHORT).show()
            R.id.share -> Toast.makeText(applicationContext, "Share", Toast.LENGTH_SHORT).show()
            R.id.rate_us -> Toast.makeText(applicationContext, "Rate_us", Toast.LENGTH_SHORT).show()
            else -> Toast.makeText(applicationContext, "Nothing Clicked", Toast.LENGTH_SHORT).show()
        }
        return true
    }
}