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
import android.widget.RelativeLayout
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class ForgetPassword2(val mobile_number:String) : AppCompatActivity() {


    lateinit var edtOTP:EditText
    lateinit var edtNewPassword:EditText
    lateinit var edtConfirmPasswordForgot:EditText
    lateinit var buttonSubmit:Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password2)


        edtOTP = findViewById(R.id.edtOTP)
        edtNewPassword = findViewById(R.id.edtNewPassword)
        edtConfirmPasswordForgot = findViewById(R.id.editTextConfirmPasswordForgot)
        buttonSubmit = findViewById(R.id.buttonSubmit)



        buttonSubmit.setOnClickListener {
            if (edtOTP.text.isBlank()) {
                edtOTP.setError("OTP missing")
            } else {
                if (edtNewPassword.text.isBlank()) {
                    edtNewPassword.setError("Password Missing")
                } else {
                    if (edtConfirmPasswordForgot.text.isBlank()) {
                        edtConfirmPasswordForgot.setError("Confirm Password Missing")
                    } else {
                        if ((edtNewPassword.text.toString().toInt() == edtConfirmPasswordForgot.text.toString().toInt())) {
                            if (ConnectionManager().checkConnectivity(this)) {

                                try {

                                    val loginUser = JSONObject()

                                    loginUser.put("mobile_number", mobile_number)
                                    loginUser.put("password", edtNewPassword.text.toString())
                                    loginUser.put("otp", edtOTP.text.toString())

                                    val queue = Volley.newRequestQueue(this)

                                    val url = "http://13.235.250.119/v2/reset_password/fetch_result"

                                    val jsonObjectRequest = object : JsonObjectRequest(
                                        Request.Method.POST,
                                        url,
                                        loginUser,
                                        Response.Listener {

                                            val responseJsonObjectData = it.getJSONObject("data")

                                            val success = responseJsonObjectData.getBoolean("success")

                                            if (success) {

                                                val serverMessage = responseJsonObjectData.getString("successMessage")

                                                Toast.makeText(
                                                    this,
                                                    serverMessage,
                                                    Toast.LENGTH_SHORT
                                                ).show()


                                            } else {
                                                val responseMessageServer =
                                                    responseJsonObjectData.getString("errorMessage")
                                                Toast.makeText(
                                                    this,
                                                    responseMessageServer.toString(),
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                            }
                                            finish()
                                        },
                                        Response.ErrorListener {

                                            finish()
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
                                val alterDialog = AlertDialog.Builder(this)

                                alterDialog.setTitle("No Internet")
                                alterDialog.setMessage("Internet Connection can't be establish!")
                                alterDialog.setPositiveButton("Open Settings") { text, listener ->
                                    val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                                    startActivity(settingsIntent)

                                }

                                alterDialog.setNegativeButton("Exit") { text, listener ->
                                    finish()
                                }
                                alterDialog.create()
                                alterDialog.show()
                            }

                        } else {

                            edtConfirmPasswordForgot.setError("Passwords don't match")

                        }
                    }
                }
            }
        }
    }










override fun onResume() {

    if (!ConnectionManager().checkConnectivity(this)) {

        val alterDialog = AlertDialog.Builder(this) // making dialog box
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
