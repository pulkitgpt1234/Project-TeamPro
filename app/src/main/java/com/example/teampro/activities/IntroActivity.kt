package com.example.teampro.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import com.example.teampro.R
import android.graphics.Typeface
import android.widget.TextView

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val tvAppNameIntro= findViewById<TextView>(R.id.tv_app_name_intro)
        val typeface: Typeface = Typeface.createFromAsset(assets, "carbon bl.ttf")
        tvAppNameIntro.typeface = typeface

        val btnSignUpIntro :Button = findViewById(R.id.btn_sign_up_intro)
        btnSignUpIntro.setOnClickListener{
            startActivity(Intent(this@IntroActivity, SignUpActivity::class.java))
        }

        val btnSignInIntro :Button = findViewById(R.id.btn_sign_in_intro)
        btnSignInIntro.setOnClickListener{
            startActivity(Intent(this@IntroActivity, SignInActivity::class.java))
        }

    }
}