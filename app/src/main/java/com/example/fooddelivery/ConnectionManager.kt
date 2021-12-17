package com.example.fooddelivery

import android.net.ConnectivityManager
import android.content.Context
import android.net.NetworkInfo

class ConnectionManager {
    fun checkConnectivity(context: Context):Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)as ConnectivityManager//to check the info of connectivity manager weather they are connected to network
        val activeNetork:NetworkInfo?= connectivityManager.activeNetworkInfo //checking for active networks. here we are using null pointer exception as we are going to declare it later
        if (activeNetork?.isConnected!=null){
            return activeNetork.isConnected
        } else{
            return false// returning false if there is no internet or there is some issue or network is in capable(null)
        }
    }
}