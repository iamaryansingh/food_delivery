package com.example.fooddelivery

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import com.example.fooddelivery.fragment.*

class MainView : AppCompatActivity() {
    lateinit var drawer_layout: DrawerLayout
    lateinit var coordinatlayout: CoordinatorLayout
    lateinit var toolbar:android.support.v7.widget.Toolbar
    lateinit var framelayout: FrameLayout
    lateinit var navigationlayout: NavigationView
    lateinit var sharedPreferencess:SharedPreferences
    lateinit var textViewcurrentUser:TextView
    lateinit var textViewMobileNumber:TextView

    var previousMenuItem: MenuItem? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_view)
        drawer_layout=findViewById(R.id.drawerlayout)
        coordinatlayout=findViewById(R.id.coordinatlayout)
        toolbar=findViewById(R.id.toolbar)
        framelayout=findViewById(R.id.framelayout)
        navigationlayout=findViewById(R.id.navigationlayout)
        sharedPreferencess=getSharedPreferences(getString(R.string.shared_preferences),
            Context.MODE_PRIVATE)
        val headerView=navigationlayout.getHeaderView(0)
        textViewcurrentUser=headerView.findViewById(R.id.newtext)
        textViewMobileNumber=headerView.findViewById(R.id.newNumber)

        navigationlayout.menu.getItem(0).setCheckable(true)
        navigationlayout.menu.getItem(0).setChecked(true)

        openDashboard()

        setuptoolbar()

        textViewcurrentUser.text=sharedPreferencess.getString("name","abcd xyz")
        textViewMobileNumber.text="+91-"+sharedPreferencess.getString("mobile_number","1111111111")
        val actionbardrawer= ActionBarDrawerToggle(this@MainView,drawer_layout,R.string.open_drawer,R.string.close_drawer)

        drawer_layout.addDrawerListener(actionbardrawer)
        actionbardrawer.syncState()

        navigationlayout.setNavigationItemSelectedListener {

            if(previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }

            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when(it.itemId){
                R.id.dashboard -> {
                    openDashboard()

                    drawer_layout.closeDrawers()
                }
                R.id.myprofile -> {

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.framelayout, FragmentProfile(this))

                        .commit()
                    supportActionBar?.title="My profile"
                    drawer_layout.closeDrawers()

                }
                R.id.favorite -> {

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.framelayout, FavouriteFragment(this))

                        .commit()
                    supportActionBar?.title="Favourite Resturant"
                    drawer_layout.closeDrawers()

                }
                R.id.orderhistory -> {

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.framelayout, HistoryFragment())
                        //.addToBackStack("favorite")
                        .commit()
                    supportActionBar?.title="My Previous Orders"
                    drawer_layout.closeDrawers()

                }
                R.id.faqs ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.framelayout, FaqFragment())
                        .commit()
                    supportActionBar?.title="Frequently Asked Questions"
                    drawer_layout.closeDrawers()

                }
                R.id.logout ->{
                    drawer_layout.closeDrawers()
                    val alterDialog =  AlertDialog.Builder(this)

                    alterDialog.setTitle("Confirmation")
                    alterDialog.setMessage("Are you sure you want to log out?")
                    alterDialog.setPositiveButton("Yes"){text,listener->
                        sharedPreferencess.edit().putBoolean("user_logged_in",false).apply()

                        ActivityCompat.finishAffinity(this)
                    }

                    alterDialog.setNegativeButton("No"){ text,listener->

                    }
                    alterDialog.create()
                    alterDialog.show()

                }
            }
            return@setNavigationItemSelectedListener true
        }

    }
    fun setuptoolbar()
    {
        setSupportActionBar(toolbar)
        supportActionBar?.title="All Restaurant"

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item!!.itemId
        if(id == android.R.id.home){
            drawer_layout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    fun openDashboard(){
        val fragment = DashboardFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.framelayout,fragment)
        transaction.commit()
        supportActionBar?.title="All Restaurant"
        navigationlayout.setCheckedItem(R.id.dashboard)
    }
    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.framelayout)

        when(frag){
            !is DashboardFragment -> openDashboard()

            else ->super.onBackPressed()
        }
    }

}
