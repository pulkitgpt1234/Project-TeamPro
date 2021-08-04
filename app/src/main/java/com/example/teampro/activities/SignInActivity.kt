package com.example.teampro.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.example.teampro.R
import com.example.teampro.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : BaseActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        auth = Firebase.auth

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()

        btn_sign_in.setOnClickListener{
            signInRegisteredUser()
        }

    }

    fun signInSuccess(user: User){
        hideProgressDialog()
        startActivity(Intent(this,MainActivity::class.java))
        finish()

    }


    private fun setupActionBar(){
        setSupportActionBar(toolbar_sign_in_activity)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        toolbar_sign_in_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun signInRegisteredUser(){
        val email:String = et_email_sign_in.text.toString().trim{it <= ' '}
        val password:String = et_password_sign_in.text.toString().trim{it <= ' '}

        if(validateForm(email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        Log.d("Sign in", "signInWithEmail:success")
                        val user = auth.currentUser
                        startActivity(Intent(this,MainActivity::class.java))
                    } else {
                        Log.w("Sign in", "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }

        }
    }

    private fun validateForm(email:String,password:String):Boolean{
        return when{
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Please enter an email.")
                false
            }
            TextUtils.isEmpty(password)->{
                showErrorSnackBar("Please enter a password.")
                false
            }
            else->{
                true
            }
        }

    }

}