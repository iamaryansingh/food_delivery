package com.example.fooddelivery

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toolbar
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.Volley
import com.example.fooddelivery.fragment.DashboardFragment
import kotlinx.android.synthetic.main.activity_resgisteration.*
import org.json.JSONException
import org.json.JSONObject

class RegisterationForm :AppCompatActivity() {

    lateinit var editTextName:EditText
    lateinit var editTextEmail:EditText
    lateinit var editTextMobileNumber:EditText
    lateinit var editTextDeliveryAddress:EditText
    lateinit var editTextPassword:EditText
    lateinit var editTextConfirmPassword:EditText
    lateinit var buttonRegister:Button





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resgisteration)

        editTextName=findViewById(R.id.edtName)
        editTextEmail=findViewById(R.id.edtEmailAdress)
        editTextMobileNumber=findViewById(R.id.edtMobileNumber)
        editTextDeliveryAddress=findViewById(R.id.edtDeliveryAdress)
        editTextPassword=findViewById(R.id.edtPassword)
        editTextConfirmPassword=findViewById(R.id.edtConfirmPassword)
        buttonRegister=findViewById(R.id.btnRegister)





        buttonRegister.setOnClickListener {
            registerUserFun()
        }

    }


    fun userSuccessfullyRegistered(){
        openDashBoard()
    }

    fun openDashBoard() {

        val intent=Intent(this, MainView::class.java)
        startActivity(intent)
        finish()

    }


    fun registerUserFun(){


        val sharedPreferencess=this.getSharedPreferences(getString(R.string.shared_preferences),Context.MODE_PRIVATE)


        sharedPreferencess.edit().putBoolean("user_logged_in", false).commit()

        if (ConnectionManager().checkConnectivity(this)) {


            if (checkForErrors()){

                try {

                    val registerUser = JSONObject()
                    registerUser.put("name", editTextName.text)
                    registerUser.put("mobile_number", editTextMobileNumber.text)
                    registerUser.put("password", editTextPassword.text)
                    registerUser.put("address", editTextDeliveryAddress.text)
                    registerUser.put("email", editTextEmail.text)


                    val queue = Volley.newRequestQueue(this)

                    val url = "http://13.235.250.119/v2/register/fetch_result"

                    val jsonObjectRequest = object : JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        registerUser,
                        Response.Listener {
                            println("Response12 is " + it)

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


                                Toast.makeText(
                                    this,
                                    "Registered sucessfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                                userSuccessfullyRegistered()//after we get a response we call the login


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
                            println("Error12 is " + it)
                            println(it)
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
                        "Some unexpected error occured!!!",
                        Toast.LENGTH_SHORT
                    ).show()

                }

            }

        }else
        {
            val alterDialog=AlertDialog.Builder(this)

            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be establish!")
            alterDialog.setPositiveButton("Open Settings"){text,listener->
                val settingsIntent= Intent(Settings.ACTION_SETTINGS)//open wifi settings
                startActivity(settingsIntent)

            }

            alterDialog.setNegativeButton("Exit"){ text,listener->
                ActivityCompat.finishAffinity(this)//closes all the instances of the app and the app closes completely
            }
            alterDialog.create()
            alterDialog.show()

        }
    }

    fun checkForErrors():Boolean {
        var errorPassCount = 0
        if (editTextName.text.isBlank()) {

            editTextName.setError("Field Missing!")
        } else {
            errorPassCount++
        }

        if (editTextMobileNumber.text.isBlank()) {
            editTextMobileNumber.setError("Field Missing!")
        } else {
            errorPassCount++
        }

        if (editTextEmail.text.isBlank()) {
            editTextEmail.setError("Field Missing!")
        } else {
            errorPassCount++
        }

        if (editTextDeliveryAddress.text.isBlank()) {
            editTextDeliveryAddress.setError("Field Missing!")
        } else {
            errorPassCount++
        }

        if (editTextConfirmPassword.text.isBlank()) {
            editTextConfirmPassword.setError("Field Missing!")
        } else {
            errorPassCount++
        }

        if (editTextPassword.text.isBlank()) {
            editTextPassword.setError("Field Missing!")
        } else {
            errorPassCount++
        }

        if (editTextPassword.text.isNotBlank() && editTextConfirmPassword.text.isNotBlank())
        {   if (editTextPassword.text.toString().toInt() == editTextConfirmPassword.text.toString().toInt()) {
            errorPassCount++
        } else {
            editTextConfirmPassword.setError("Password don't match")
        }
        }

        return errorPassCount==7

    }



    override fun onResume() {

        if (!ConnectionManager().checkConnectivity(this)) {

            val alterDialog=AlertDialog.Builder(this)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be establish!")
            alterDialog.setPositiveButton("Open Settings"){text,listener->
                val settingsIntent= Intent(Settings.ACTION_SETTINGS)//open wifi settings
                startActivity(settingsIntent)
            }

            alterDialog.setNegativeButton("Exit"){ text,listener->
                ActivityCompat.finishAffinity(this)//closes all the instances of the app and the app closes completely
            }
            alterDialog.setCancelable(false)

            alterDialog.create()
            alterDialog.show()

        }

        super.onResume()
    }



}