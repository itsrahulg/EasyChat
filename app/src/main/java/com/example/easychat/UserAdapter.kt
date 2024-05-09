package com.example.easychat

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class UserAdapter(val context: Context, val userList: ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private val contactsMap: HashMap<String, String> by lazy {
        fetchContacts()
    }

    private fun fetchContacts(): HashMap<String, String> {
        val contactsMap = HashMap<String, String>()
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                ),
                null,
                null,
                null
            )
            cursor?.use {
                while (it.moveToNext()) {
                    val phoneNumberIndex =
                        it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val displayNameIndex =
                        it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    val phoneNumber =
                        it.getString(phoneNumberIndex)?.replace("\\s".toRegex(), "") // Remove whitespace
                    val displayName = it.getString(displayNameIndex)
                    phoneNumber?.let { number ->
                        val normalizedNumber = normalizePhoneNumber(number)
                        contactsMap[normalizedNumber] = displayName
                    }
                }
            }
        } else {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_CONTACTS_PERMISSION
            )
        }
        return contactsMap
    }

    // Function to normalize a phone number
    private fun normalizePhoneNumber(phoneNumber: String): String {
        // Remove any non-numeric characters
        return phoneNumber.replace("[^0-9]".toRegex(), "")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.layout, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]

        // Normalize the phone number for comparison
        val normalizedPhoneNumber = normalizePhoneNumber(currentUser.phonenumber ?: "")
        if (contactsMap.containsKey(normalizedPhoneNumber)) {
            val displayName = contactsMap[normalizedPhoneNumber]
            holder.textName.text = displayName

        } else {
            // Hide view if not found in contacts
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams =
                RecyclerView.LayoutParams(0, 0)
            return
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)

            // Retrieve the display name again as it may be null in some cases
            val displayName = contactsMap[normalizedPhoneNumber]
            intent.putExtra("name", displayName)
            intent.putExtra("uid", currentUser.uid)
            context.startActivity(intent)
        }


    }
    fun updateList(newList: List<User>) {
        userList.clear()
        userList.addAll(newList)
        notifyDataSetChanged()
    }


    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName = itemView.findViewById<TextView>(R.id.txt_name)

    }

    companion object {
        private const val REQUEST_CONTACTS_PERMISSION = 123
    }
}









