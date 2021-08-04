package com.example.teampro.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.TextView
import com.example.teampro.R
import com.example.teampro.firebase.FireStoreClass
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val typeFace: Typeface = Typeface.createFromAsset(assets,"carbon bl.ttf")
        tv_app_name.typeface = typeFace

        Handler().postDelayed({
            val currentUserId = FireStoreClass().getCurrentUserId()
            if(currentUserId.isNotEmpty()){
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }else {
                startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
            }
            finish()
        }, 2500)


    }
}