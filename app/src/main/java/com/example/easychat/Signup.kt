package com.example.easychat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Signup : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtNumber:EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()

        edtName = findViewById(R.id.username)
        edtEmail = findViewById(R.id.email)
        edtNumber = findViewById(R.id.phonenumber)
        edtPassword = findViewById(R.id.password)
        btnSignUp = findViewById(R.id.signup_button)

        btnSignUp.setOnClickListener {
            val name = edtName.text.toString()
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()
            val phonenumber = edtNumber.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                signUp(name,email,password,phonenumber)
            } else {
                Toast.makeText(this, "Please enter credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signUp(name: String, email: String, password: String,phonenumber:String) {
        mAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    addUserToDatabase(name,email,phonenumber,mAuth.currentUser?.uid!!)
                    val intent = Intent(this@Signup, Login::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val error = task.exception?.message
                    Toast.makeText(this@Signup, "$error", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun addUserToDatabase(name:String, email:String,phonenumber:String,uid:String){
        mDbRef = FirebaseDatabase.getInstance().getReference()

        mDbRef.child("user").child(uid).setValue(User(name,email,phonenumber,uid))
    }
}
