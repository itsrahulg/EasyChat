package com.example.easychat

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson

class Profile : AppCompatActivity() {

    private lateinit var profileImageUri: Uri
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)

        // Initialize views
        val changePhotoButton: Button = findViewById(R.id.change_photo_button)
        val profileImageView: ImageView = findViewById(R.id.profile_image)

        // Set click listener for the button
        changePhotoButton.setOnClickListener {
            launchGalleryIntent()
        }

        // Retrieve user data and fill in TextViews
        retrieveUserData()

        // Load profile image if exists
        loadProfileImage(profileImageView)
    }

    private fun launchGalleryIntent() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Get selected image URI
            profileImageUri = data.data!!
            // Now upload the image to Firebase Storage and save its URL in Firestore
            uploadImageToFirebaseStorage()

            // Update profile image view
            val profileImageView: ImageView = findViewById(R.id.profile_image)
            profileImageView.setImageURI(profileImageUri)

            // Save image URI to SharedPreferences
            saveImageUriToSharedPreferences(profileImageUri)
        }
    }

    private fun uploadImageToFirebaseStorage() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val storageRef =
            FirebaseStorage.getInstance().reference.child("profile_images").child("$userId.jpg")

        storageRef.putFile(profileImageUri)
            .addOnSuccessListener {
                // Image uploaded successfully
                // Get the download URL
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    // Save the download URL in Firestore
                    saveImageUrlToFirestore(userId!!, uri.toString())

                    // Show toast message for successful upload
                    Toast.makeText(this, "Profile photo updated", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                // Handle failed upload
                Toast.makeText(this, "Failed to upload profile photo", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveImageUriToSharedPreferences(uri: Uri) {
        val editor = sharedPreferences.edit()
        editor.putString("profileImageUri", uri.toString())
        editor.apply()
    }

    private fun saveImageUrlToFirestore(userId: String, imageUrl: String) {
        val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)
        userRef.update("profileImageUrl", imageUrl)
            .addOnSuccessListener {
                // Image URL saved successfully
            }
            .addOnFailureListener {
                // Handle failure
            }
    }


    private fun retrieveUserData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val userRef = FirebaseDatabase.getInstance().reference.child("user").child(userId!!)
        val cachedUserData = sharedPreferences.getString("userData", null)

        if (cachedUserData != null) {
            // If user data is cached, retrieve from cache and update UI
            val userData = Gson().fromJson<UserData>(cachedUserData, UserData::class.java)
            updateUI(userData)
        } else {
            // If user data is not cached, fetch from database
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val username = dataSnapshot.child("name").getValue(String::class.java)
                        val email = dataSnapshot.child("email").getValue(String::class.java)
                        val phoneNumber = dataSnapshot.child("phonenumber").getValue(String::class.java)

                        // Cache user data
                        val userData = UserData(username, email, phoneNumber)
                        val userDataJson = Gson().toJson(userData)
                        sharedPreferences.edit().putString("userData", userDataJson).apply()

                        // Update TextViews with retrieved data
                        updateUI(userData)
                    } else {
                        // User node does not exist
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle failure
                }
            })
        }
    }

    private fun updateUI(userData: UserData) {
        findViewById<TextView>(R.id.usernamevalue).text = userData.username
        findViewById<TextView>(R.id.emailidvalue).text = userData.email
        findViewById<TextView>(R.id.phonenumbervalue).text = userData.phoneNumber
    }

    data class UserData(val username: String?, val email: String?, val phoneNumber: String?)


    private fun loadProfileImage(profileImageView: ImageView) {
        val uriString = sharedPreferences.getString("profileImageUri", null)
        if (uriString != null) {
            profileImageUri = Uri.parse(uriString)
            Glide.with(this /* context */)
                .load(profileImageUri)
                .placeholder(R.drawable.profile)
                .error(R.drawable.profile)
                .into(profileImageView)
        } else {
            // Load default profile image or set default placeholder
            profileImageView.setImageResource(R.drawable.profile)
        }
    }
    override fun onResume() {
        super.onResume()
        // Reload profile image if exists
        val profileImageView: ImageView = findViewById(R.id.profile_image)
        loadProfileImage(profileImageView)
    }


    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

}








