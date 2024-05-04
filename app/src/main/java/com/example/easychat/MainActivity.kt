package com.example.easychat

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.common.reflect.TypeToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import java.lang.reflect.Type

class MainActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var originalUserList: ArrayList<User> // Store the original list of users
    private lateinit var adapter: UserAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbref: DatabaseReference
    private lateinit var searchEditText: EditText
    private lateinit var sharedPreferences: SharedPreferences

    private val requestContactsPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission is granted, notify adapter to fetch contacts
                adapter.notifyDataSetChanged()
            } else {
                // Permission is not granted, handle accordingly
                // For example, show a message or take appropriate action
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        mDbref = FirebaseDatabase.getInstance().getReference()
        sharedPreferences = getSharedPreferences("cached_contacts", Context.MODE_PRIVATE)

        userList = ArrayList()
        originalUserList = ArrayList() // Initialize originalUserList here
        adapter = UserAdapter(this, userList)
        userRecyclerView = findViewById(R.id.userRecyclerView)
        searchEditText = findViewById(R.id.searchEditText)

        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter

        // Check if READ_CONTACTS permission is granted
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is already granted, notify adapter to fetch contacts
            adapter.notifyDataSetChanged()
        } else {
            // Permission is not granted, request it
            requestContactsPermission()
        }

        // Fetch contacts from local cache if available, else fetch from Firebase database
        fetchContactsFromCacheOrDatabase()

        // Add TextChangedListener to the searchEditText for filtering
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Filter the userList based on the search query
                val filteredList = userList.filter {
                    it.name?.startsWith(s.toString(), ignoreCase = true) == true
                }
                // Update the adapter with the filtered list
                adapter.updateList(filteredList)
            }

            override fun afterTextChanged(s: Editable?) {
                // If the search query is empty, restore the original list of users
                if (s.isNullOrBlank()) {
                    adapter.updateList(originalUserList)
                }
            }
        })

    }

    private fun fetchContactsFromCacheOrDatabase() {
        // Check if contacts are cached locally
        val cachedContacts = getCachedContacts()

        if (cachedContacts.isNotEmpty()) {
            // If cached contacts are available, populate the userList and originalUserList
            userList.clear()
            originalUserList.clear()
            userList.addAll(cachedContacts)
            originalUserList.addAll(cachedContacts)
            adapter.notifyDataSetChanged()
        } else {
            // If no cached contacts are available, fetch contacts from Firebase database
            fetchContactsFromDatabase()
        }
    }

    private fun getCachedContacts(): List<User> {
        // Retrieve cached contacts from SharedPreferences
        val cachedContactsJson = sharedPreferences.getString("cached_contacts", "")
        val type: Type = object : TypeToken<List<User>>() {}.type
        return Gson().fromJson(cachedContactsJson, type) ?: emptyList()
    }

    private fun fetchContactsFromDatabase() {
        mDbref.child("user").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                originalUserList.clear()
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)

                    if (mAuth.currentUser?.uid != currentUser?.uid) {
                        userList.add(currentUser!!)
                        originalUserList.add(currentUser!!)
                    }
                }
                // Update local cache
                updateCachedContacts(userList)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled
            }
        })
    }

    private fun updateCachedContacts(contacts: List<User>) {
        // Update cached contacts in SharedPreferences
        val contactsJson = Gson().toJson(contacts)
        sharedPreferences.edit().putString("cached_contacts", contactsJson).apply()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            //logic for logout
            mAuth.signOut()
            val intent = Intent(this@MainActivity, Login::class.java)
            finish()
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun requestContactsPermission() {
        requestContactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
    }
}
