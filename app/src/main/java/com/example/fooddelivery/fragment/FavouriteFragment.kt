package com.example.fooddelivery.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.room.Room

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.fooddelivery.ConnectionManager
import com.example.fooddelivery.adapter.FavouriteRecyclerAdapter
import com.example.fooddelivery.R
import com.example.fooddelivery.database.RestaurantDatabase
import com.example.fooddelivery.database.RestaurantEntity
import com.example.fooddelivery.model.Restaurant
import org.json.JSONException



class FavouriteFragment(val contextParam:Context) : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    val newList = arrayListOf<Restaurant>()
    lateinit var recyclerAdapter: FavouriteRecyclerAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)

        layoutManager = LinearLayoutManager(activity as Context)

        recyclerView = view.findViewById(R.id.recyclerDashboard)

        return view
    }


    fun fetchData(){


        if (ConnectionManager().checkConnectivity(activity as Context)) {

            try {

                val queue = Volley.newRequestQueue(activity as Context)

                val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    Response.Listener {
                        println("Response12 is " + it)

                        val responseJsonObjectData = it.getJSONObject("data")

                        val success = responseJsonObjectData.getBoolean("success")

                        if (success) {

                            newList


                            val data = responseJsonObjectData.getJSONArray("data")

                            for (i in 0 until data.length()) {
                                val restaurantJsonObject = data.getJSONObject(i)

                                val restaurantEntity=RestaurantEntity(
                                    restaurantJsonObject.getString("id"),
                                    restaurantJsonObject.getString("name")
                                )

                                if(DBAsynTask(contextParam,restaurantEntity,1).execute().get())
                                {

                                    val restaurantObject = Restaurant(
                                        restaurantJsonObject.getString("id"),
                                        restaurantJsonObject.getString("name"),
                                        restaurantJsonObject.getString("rating"),
                                        restaurantJsonObject.getString("cost_for_one"),
                                        restaurantJsonObject.getString("image_url")
                                    )

                                    newList.add(restaurantObject)


                                    recyclerAdapter = FavouriteRecyclerAdapter(
                                        activity as Context,
                                        newList
                                    )

                                    recyclerView.adapter = recyclerAdapter

                                    recyclerView.layoutManager = layoutManager

                                }
                            }
                            if(newList.size==0){
                                Toast.makeText(
                                    activity as Context,
                                    "Nothing is added to Favourites",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        }
                    },
                    Response.ErrorListener {
                        println("Error12 is " + it)

                        Toast.makeText(
                            activity as Context,
                            "mSome Error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()

                    })

                {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers=HashMap<String,String>()

                        headers["Content-type"]="application/json"
                        headers["token"]="8245621eb3c70c"

                        return headers
                    }
                }

                queue.add(jsonObjectRequest)

            }catch (e: JSONException){
                Toast.makeText(activity as Context,"Some Unexpected error occured!!!", Toast.LENGTH_SHORT).show()
            }

        }else
        {

            val alterDialog =  AlertDialog.Builder(activity as Context)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be establish!")
            alterDialog.setPositiveButton("Open Settings"){text,listener->
                val settingsIntent= Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
            }

            alterDialog.setNegativeButton("Exit"){ text,listener->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            alterDialog.setCancelable(false)

            alterDialog.create()
            alterDialog.show()
        }

    }



    class DBAsynTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {



            when (mode) {
                1 -> {
                    val restaurant: RestaurantEntity? = db.restaurantDao()
                        .getRestaurantById(restaurantEntity.restaurant_Id)
                    db.close()
                    return restaurant != null
                }
                else->return false

            }

        }


    }

    override fun onResume() {

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            fetchData()//if internet is available fetch data
        }else
        {

            val alterDialog =  AlertDialog.Builder(activity as Context)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be establish!")
            alterDialog.setPositiveButton("Open Settings"){text,listener->
                val settingsIntent= Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
            }

            alterDialog.setNegativeButton("Exit"){ text,listener->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            alterDialog.setCancelable(false)

            alterDialog.create()
            alterDialog.show()

        }

        super.onResume()
    }


}