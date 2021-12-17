package com.example.fooddelivery.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.fooddelivery.R

lateinit var textViewName: TextView
lateinit var textViewEmail:TextView
lateinit var textViewMobileNumber:TextView
lateinit var textViewAddress:TextView


class FragmentProfile(val contextParams:Context) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_profile, container, false)

        textViewName =view.findViewById(R.id.txtDetailsName)
        textViewEmail =view.findViewById(R.id.txtDetailsMail)
        textViewMobileNumber =view.findViewById(R.id.txtDetailsPhone)
        textViewAddress =view.findViewById(R.id.txtDetailsAddress)

        val sharedPreferencess=contextParams.getSharedPreferences(getString(R.string.shared_preferences),Context.MODE_PRIVATE)

        textViewName.text=sharedPreferencess.getString("name","")
        textViewEmail.text=sharedPreferencess.getString("email","")
        textViewMobileNumber.text="+91-"+sharedPreferencess.getString("mobile_number","")
        textViewAddress.text=sharedPreferencess.getString("address","")

        return view
    }


}