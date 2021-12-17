package com.example.fooddelivery

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class ForgetPassword1 : AppCompatActivity() {
    lateinit var btnNext: Button
    lateinit var edtTextMobileNumber: EditText
    lateinit var edtTextEmail: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password1)
        btnNext = findViewById(R.id.btnNext)
        edtTextMobileNumber = findViewById(R.id.edtMobileNumber)
        edtTextEmail = findViewById(R.id.edtEmailAdress)


        btnNext.setOnClickListener {

            if (edtTextMobileNumber.text.isBlank()) {
                edtTextMobileNumber.setError("Mobile Number Missing")
            } else {
                if (edtTextEmail.text.isBlank()) {
                    edtTextEmail.setError("Email Missing")
                } else {

                    if (ConnectionManager().checkConnectivity(this)) {

                        try {

                            val loginUser = JSONObject()

                            loginUser.put("mobile_number", edtTextMobileNumber.text)
                            loginUser.put("email", edtTextEmail.text)

                            println(loginUser.getString("mobile_number"))
                            println(loginUser.getString("email"))


                            val queue = Volley.newRequestQueue(this)

                            val url = "http://13.235.250.119/v2/forgot_password/fetch_result"


                            val jsonObjectRequest = object : JsonObjectRequest(
                                Request.Method.POST,
                                url,
                                loginUser,
                                Response.Listener {

                                    val responseJsonObjectData = it.getJSONObject("data")

                                    val success = responseJsonObjectData.getBoolean("success")

                                    if (success) {

                                        val first_try = responseJsonObjectData.getBoolean("first_try")

                                        if (first_try == true) {
                                            Toast.makeText(
                                                this,
                                                "OTP sent",
                                                Toast.LENGTH_SHORT

                                            ).show()

                                            //after we get a response we call the Log the user in
                                        } else {
                                            Toast.makeText(
                                                this,
                                                "OTP sent already",
                                                Toast.LENGTH_SHORT
                                            ).show()


                                        }
                                        intent = Intent(this, ForgetPassword2::class.java)
                                        startActivity(intent)

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
                                    println(it)
                                    Toast.makeText(
                                        this,
                                        "mSome Error occurred!!!",
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
                    } else {
                        val alterDialog = AlertDialog.Builder(this) // making dialog box

                        alterDialog.setTitle("No Internet")
                        alterDialog.setMessage("Internet Connection can't be establish!")
                        alterDialog.setPositiveButton("Open Settings") { text, listener ->
                            val settingsIntent = Intent(Settings.ACTION_SETTINGS)//open wifi settings
                            startActivity(settingsIntent)

                        }

                        alterDialog.setNegativeButton("Exit") { text, listener ->
                            ActivityCompat.finishAffinity(this)//closes all the instances of the app and the app closes completely
                        }
                        alterDialog.create()
                        alterDialog.show()
                    }
                }
            }
        }
    }
}







    /*override fun onResume() {

        if (!ConnectionManager().checkConnectivity(this)) {

            val alterDialog =  AlertDialog.Builder(this) // making dialog box
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
}*/


