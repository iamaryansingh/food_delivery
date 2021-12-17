package com.example.fooddelivery

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.fooddelivery.adapter.CartAdapter
import com.example.fooddelivery.model.CartItem
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

lateinit var textViewOrderingFrom:TextView
lateinit var buttonPlaceOrder: Button
lateinit var recyclerView: RecyclerView
lateinit var layoutManager: RecyclerView.LayoutManager
lateinit var restaurantId:String
lateinit var toolbar: android.support.v7.widget.Toolbar
lateinit var menuAdapter: CartAdapter
lateinit var restaurantName:String
lateinit var selectedItemsId:ArrayList<String>
var cartListItem = arrayListOf<CartItem>()
var totalAmount=0

class MyCart : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mycart)
        recyclerView = findViewById(R.id.newmycartRecycler)
        toolbar = findViewById(R.id.MycartToolbar)
        textViewOrderingFrom = findViewById(R.id.txtResturantName)
        buttonPlaceOrder = findViewById(R.id.btnPlaceOrder)
        restaurantId = intent.getStringExtra("restaurantId")
        restaurantName = intent.getStringExtra("restaurantName")
        selectedItemsId = intent.getStringArrayListExtra("selectedItemsId")
        setToolBar()
        textViewOrderingFrom.text = restaurantName

        buttonPlaceOrder.setOnClickListener(View.OnClickListener {

            val sharedPreferencess =
                this.getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE)

            if (ConnectionManager().checkConnectivity(this)) {


                try {

                    val foodJsonArray = JSONArray()


                    for (foodItem in selectedItemsId) {
                        val singleItemObject = JSONObject()
                        singleItemObject.put("food_item_id", foodItem)
                        foodJsonArray.put(singleItemObject)

                    }

                    val sendOrder = JSONObject()

                    sendOrder.put("user_id", sharedPreferencess.getString("user_id", "0"))
                    sendOrder.put("restaurant_id", restaurantId)
                    sendOrder.put("total_cost", totalAmount)
                    sendOrder.put("food", foodJsonArray)

                    val queue = Volley.newRequestQueue(this)

                    val url = "http://13.235.250.119/v2/place_order/fetch_result/"

                    val jsonObjectRequest = object : JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        sendOrder,
                        Response.Listener {

                            val responseJsonObjectData = it.getJSONObject("data")

                            val success = responseJsonObjectData.getBoolean("success")

                            if (success) {

                                Toast.makeText(
                                    this,
                                    "Order Placed",
                                    Toast.LENGTH_SHORT
                                ).show()



                                val intent = Intent(this, OrderPlace::class.java)

                                startActivity(intent)

                                finishAffinity()


                            } else {
                                val responseMessageServer =
                                    responseJsonObjectData.getString("errorMessage")
                                Toast.makeText(
                                    this,
                                    responseMessageServer.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        },
                        Response.ErrorListener {

                            println("ssssss" + it)

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
                    jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
                        15000,
                        1,
                        1f
                    )

                    queue.add(jsonObjectRequest)

                } catch (e: JSONException) {
                    Toast.makeText(
                        this,
                        "Some unexpected error occured!!!",
                        Toast.LENGTH_SHORT
                    ).show()

                }

            } else {

                val alterDialog = AlertDialog.Builder(this)
                alterDialog.setTitle("No Internet")
                alterDialog.setMessage("Internet Connection can't be establish!")
                alterDialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                    startActivity(settingsIntent)
                }

                alterDialog.setNegativeButton("Exit") { text, listener ->
                    finishAffinity()
                }
                alterDialog.setCancelable(false)

                alterDialog.create()
                alterDialog.show()
            }
        })


    }

    fun fetchData() {

        if (ConnectionManager().checkConnectivity(this)) {


            try {

                val queue = Volley.newRequestQueue(this)

                val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId"

                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    Response.Listener {

                        val responseJsonObjectData = it.getJSONObject("data")

                        val success = responseJsonObjectData.getBoolean("success")

                        if (success) {

                            val data = responseJsonObjectData.getJSONArray("data")

                            cartListItem.clear()

                            totalAmount = 0

                            for (i in 0 until data.length()) {
                                val cartItemJsonObject = data.getJSONObject(i)

                                if (selectedItemsId.contains(cartItemJsonObject.getString("id")))
                                {

                                    val menuObject = CartItem(
                                        cartItemJsonObject.getString("id"),
                                        cartItemJsonObject.getString("name"),
                                        cartItemJsonObject.getString("cost_for_one"),
                                        cartItemJsonObject.getString("restaurant_id")
                                    )




                                    cartListItem.add(menuObject)

                                }

                                menuAdapter = CartAdapter(this, cartListItem)

                                recyclerView.adapter = menuAdapter

                                recyclerView.layoutManager = layoutManager

                            }



                        }
                    },
                    Response.ErrorListener {

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
                    "Some Unexpected error occurred!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {

            val alterDialog = AlertDialog.Builder(this)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be establish!")
            alterDialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
            }

            alterDialog.setNegativeButton("Exit") { text, listener ->
                finishAffinity()
            }
            alterDialog.setCancelable(false)

            alterDialog.create()
            alterDialog.show()

        }

    }

    fun setToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_back)
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


    override fun onResume() {

        if (ConnectionManager().checkConnectivity(this)) {
            fetchData()
        } else {

            val alterDialog = AlertDialog.Builder(this)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be establish!")
            alterDialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
            }

            alterDialog.setNegativeButton("Exit") { text, listener ->
                finishAffinity()
            }
            alterDialog.setCancelable(false)

            alterDialog.create()
            alterDialog.show()

        }

        super.onResume()
    }

}