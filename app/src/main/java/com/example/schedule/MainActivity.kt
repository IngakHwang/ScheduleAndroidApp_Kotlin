package com.example.schedule

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration : AppBarConfiguration

    lateinit var viewmodel : MainViewModel

    companion object{
        var ID : String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewmodel = ViewModelProvider(this).get(MainViewModel::class.java)

        val host = supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment

        val navController = host.navController

        appBarConfiguration = AppBarConfiguration(navController.graph)


        appBarConfiguration = AppBarConfiguration(
            supportFragmentManager.findFragmentById(R.id.myNavHostFragment)
                .let { fragment ->
                    (fragment as NavHostFragment).navController.graph
                }
        )
    }

//    fun getPref(prefName:String) = getSharedPreferences(prefName,Context.MODE_PRIVATE)
//    fun getPrefEditor(prefName:String) = getPref(prefName).edit()
}