package com.example.fooddelivery
import android.app.AlertDialog
import android.content.Intent
import android.provider.Settings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.fooddelivery.adapter.RestaurantMenuAdapter
import com.example.fooddelivery.model.RestaurantMenu
import org.json.JSONException

class RestaurantMenuActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var menuAdapter: RestaurantMenuAdapter
    lateinit var restaurantId:String
    lateinit var toolbar:android.support.v7.widget.Toolbar
    lateinit var restaurantName:String
    lateinit var proceedToCartLayout:RelativeLayout

    lateinit var buttonProceedToCart:Button


    var restaurantMenuList = arrayListOf<RestaurantMenu>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)
        toolbar = findViewById(R.id.descriptionToolbar)
        recyclerView = findViewById(R.id.recyclerDescription)
        buttonProceedToCart = findViewById(R.id.btnProceedToCart)
        restaurantId = intent.getStringExtra("restaurantId")
        restaurantName = intent.getStringExtra("restaurantName")
        layoutManager = LinearLayoutManager(this)
        proceedToCartLayout=findViewById(R.id.relativeLayoutProceedToCart)
        proceedToCartLayout.visibility=View.GONE


        onResume()

        setToolBar()


    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        when (id) {
            android.R.id.home -> {
                super.onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun fetchData(){


        if (ConnectionManager().checkConnectivity(this@RestaurantMenuActivity)) {

            try {

                val queue = Volley.newRequestQueue(this@RestaurantMenuActivity)



                val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId"

                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    Response.Listener {

                        val responseJsonObjectData = it.getJSONObject("data")

                        val success = responseJsonObjectData.getBoolean("success")

                        if (success) {
                            restaurantMenuList.clear()

                            val data = responseJsonObjectData.getJSONArray("data")

                            for (i in 0 until data.length()) {
                                val bookJsonObject = data.getJSONObject(i)
                                val menuObject = RestaurantMenu(
                                    bookJsonObject.getString("id"),
                                    bookJsonObject.getString("name"),
                                    bookJsonObject.getString("cost_for_one")


                                )
                                restaurantMenuList.add(menuObject)


                                menuAdapter = RestaurantMenuAdapter(
                                    this@RestaurantMenuActivity,
                                    restaurantId,
                                    restaurantName,
                                    proceedToCartLayout,
                                    buttonProceedToCart,
                                    restaurantMenuList
                                )

                                recyclerView.adapter = menuAdapter

                                recyclerView.layoutManager = layoutManager

                            }


                        }
                    },
                    Response.ErrorListener {
                        println("Error12menu is " + it)

                        Toast.makeText(
                            this,
                            "Some Error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()

                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()

                        headers["Content-type"] = "application/json"
                        headers["token"] = "8245621eb3c70c"

                        return headers
                    }
                }

                queue.add(jsonObjectRequest)

            } catch (e: JSONException) {
                Toast.makeText(
                    this,
                    "Some Unexpected error occured!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        else {
            val alterDialog =  AlertDialog.Builder(this@RestaurantMenuActivity)

            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be establish!")
            alterDialog.setPositiveButton("Open Settings"){text,listener->
                val settingsIntent= Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
            }

            alterDialog.setNegativeButton("Exit"){ text,listener->
                finishAffinity()
            }
            alterDialog.setCancelable(false)

            alterDialog.create()
            alterDialog.show()
        }

    }


    fun setToolBar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title=restaurantName
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_back)
    }

    override fun onBackPressed() {

            val alterDialog =  AlertDialog.Builder(this@RestaurantMenuActivity)
            alterDialog.setTitle("Alert!")
            alterDialog.setMessage("Going back will remove everything from cart")
            alterDialog.setPositiveButton("Okay") { text, listener ->
                super.onBackPressed()
            }
            alterDialog.setNegativeButton("No") { text, listener ->

            }
            alterDialog.show()
    }


    override fun onResume() {

        if (ConnectionManager().checkConnectivity(this)) {
            if(restaurantMenuList.isEmpty())
                fetchData()
        }else
        {

            val alterDialog =  AlertDialog.Builder(this@RestaurantMenuActivity)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be establish!")
            alterDialog.setPositiveButton("Open Settings"){text,listener->
                val settingsIntent= Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
            }

            alterDialog.setNegativeButton("Exit"){ text,listener->
                finishAffinity()
            }
            alterDialog.setCancelable(false)

            alterDialog.create()
            alterDialog.show()

        }


        super.onResume()
    }

}