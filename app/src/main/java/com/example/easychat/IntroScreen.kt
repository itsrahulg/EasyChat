package com.example.easychat

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class IntroScreen : AppCompatActivity() {
    private val delayMillis: Long = 2000
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_screen)

        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()

        Handler().postDelayed({
            // Check if the user is already logged in
            if (mAuth.currentUser != null) {
                // User is logged in, redirect to MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                // User is not logged in, redirect to Login screen
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
            }
            finish()
        }, delayMillis)
    }
}
