package com.example.easychat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var signupLink: TextView

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()

        // Getting the IDs
        edtEmail = findViewById(R.id.email)
        edtPassword = findViewById(R.id.password)

        btnLogin = findViewById(R.id.login_button)
        signupLink = findViewById(R.id.signup_link)


        signupLink.setOnClickListener {
            val intent = Intent(this, Signup::class.java)
            finish()
            startActivity(intent)

        }

        // Logging in code
        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            // Check if email and password are empty
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this@Login, "Please enter login details", Toast.LENGTH_SHORT).show()
            } else {
                login(email, password)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        // Clearing the EditText fields when the activity is resumed
        edtEmail.text.clear()
        edtPassword.text.clear()
    }


    private fun login(email: String, password: String) {
        // Logic for logging in the user
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Code when sign-in is successful
                    val intent = Intent(this@Login, MainActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    // Code when sign-in is not successful
                    Toast.makeText(this@Login, "User does not exist", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
