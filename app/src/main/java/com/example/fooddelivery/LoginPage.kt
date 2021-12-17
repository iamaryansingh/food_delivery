package com.example.fooddelivery

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
 class LoginPage: AppCompatActivity() {
     lateinit var txtSignup: TextView
     lateinit var txtForgetPassword: TextView
     lateinit var edtPhoneNumber: EditText
     lateinit var edtPassword: EditText


     lateinit var btnLogin: Button


     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         setContentView(R.layout.activity_login_page)
         edtPhoneNumber = findViewById(R.id.edtPhoneNumber)
         edtPassword = findViewById(R.id.edtPassword)
         txtSignup = findViewById(R.id.txtSignUp)
         txtForgetPassword = findViewById(R.id.txtForgotPassword)
         btnLogin = findViewById(R.id.btnLogin)
         val sharedPreferencess=this@LoginPage.getSharedPreferences(getString(R.string.shared_preferences),Context.MODE_PRIVATE)
         if(sharedPreferencess.getBoolean("user_logged_in",false)){
             val intent= Intent(this,MainView::class.java)
             startActivity(intent)
             finish()
         }else{
             LoginPage()
         }


         txtSignup.setOnClickListener {
             intent = Intent(this, RegisterationForm::class.java)
            startActivity(intent)

         }
         txtForgetPassword.setOnClickListener {
             intent = Intent(this, ForgetPassword1::class.java)
             startActivity(intent)

         }



         btnLogin.setOnClickListener {

             val sharedPreferencess=this@LoginPage.getSharedPreferences(getString(R.string.shared_preferences),Context.MODE_PRIVATE)


             if (ConnectionManager().checkConnectivity(this@LoginPage)) {

                 try {

                     val loginUser = JSONObject()

                     loginUser.put("mobile_number", edtPhoneNumber.text)
                     loginUser.put("password", edtPassword.text)


                     val queue = Volley.newRequestQueue(this@LoginPage)

                     val url = "http://13.235.250.119/v2/login/fetch_result"

                     val jsonObjectRequest = object : JsonObjectRequest(
                         Request.Method.POST,
                         url,
                         loginUser,
                         Response.Listener {

                             val responseJsonObjectData = it.getJSONObject("data")

                             val success = responseJsonObjectData.getBoolean("success")


                             if (success) {

                                 val data = responseJsonObjectData.getJSONObject("data")
                                 sharedPreferencess.edit().putBoolean("user_logged_in", true).commit()
                                 sharedPreferencess.edit().putString("user_id", data.getString("user_id")).commit()
                                 sharedPreferencess.edit().putString("name", data.getString("name")).apply()
                                 sharedPreferencess.edit().putString("email", data.getString("email")).apply()
                                 sharedPreferencess.edit().putString("mobile_number", data.getString("mobile_number")).apply()
                                 sharedPreferencess.edit().putString("address", data.getString("address")).apply()
                                 val intent=Intent(this@LoginPage,MainView::class.java)
                                 startActivity(intent)
                                 finish()
                                //after we get a response we call the Log the user in

                             } else {

                                 btnLogin.visibility=View.VISIBLE

                                 println(it)

                                 val responseMessageServer =
                                     responseJsonObjectData.getString("errorMessage")
                                 Toast.makeText(
                                     this@LoginPage,
                                     responseMessageServer.toString(),
                                     Toast.LENGTH_SHORT
                                 ).show()

                             }

                         },
                         Response.ErrorListener {
                             println(it)
                             btnLogin.visibility=View.VISIBLE



                             Toast.makeText(
                                 this@LoginPage,
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
                     btnLogin.visibility=View.VISIBLE

                     Toast.makeText(
                         this@LoginPage,
                         "Some unexpected error occured!!!",
                         Toast.LENGTH_SHORT
                     ).show()

                 }

             }else
             {
                 btnLogin.visibility=View.VISIBLE
                 val dialog = AlertDialog.Builder(this@LoginPage)

                 dialog.setTitle("Error")
                 dialog.setMessage("Internet Connecion is not Found")
                 dialog.setPositiveButton("Open Setting"){text,listner ->
                     val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                     startActivity(settingsIntent)
                     this@LoginPage?.finish()
                 }
                 dialog.setNegativeButton("Exit"){text,listener ->
                     ActivityCompat.finishAffinity(this@LoginPage)
                 }
                 dialog.create()
                 dialog.show()

             }

         }

     }
     }
