package com.example.fooddelivery

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    lateinit var topAnim: Animation
    lateinit var bottomAnim: Animation
    lateinit var image: ImageView
    lateinit var logo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        topAnim= AnimationUtils.loadAnimation(this,R.anim.top_animation)
        bottomAnim= AnimationUtils.loadAnimation(this,R.anim.bottom_animation)
        image=findViewById(R.id.imgdinnerplate)
        logo=findViewById(R.id.mytext)

        image.animation = topAnim
        logo.animation=bottomAnim
       Handler().postDelayed({
                intent = Intent(this, LoginPage::class.java)
                startActivity(intent)
                finish()

        },2500)
    }
}
