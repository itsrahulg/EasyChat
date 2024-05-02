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

    private val contactNumbers: HashSet<String> by lazy {
        fetchContactNumbers()
    }

    private fun fetchContactNumbers(): HashSet<String> {
        val contactNumbers = HashSet<String>()
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                null,
                null,
                null
            )
            cursor?.use {
                while (it.moveToNext()) {
                    val phoneNumberIndex =
                        it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val phoneNumber =
                        it.getString(phoneNumberIndex)?.replace("\\s".toRegex(), "") // Remove whitespace
                    phoneNumber?.let { number ->
                        val normalizedNumber = normalizePhoneNumber(number)
                        contactNumbers.add(normalizedNumber)
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
        return contactNumbers
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
        if (contactNumbers.contains(normalizedPhoneNumber)) {
            holder.textName.text = currentUser.name
            holder.textPhoneNumber.text = currentUser.phonenumber
        } else {
            // Hide view if not found in contacts
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams =
                RecyclerView.LayoutParams(0, 0)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context,ChatActivity::class.java)

            intent.putExtra("name",currentUser.name)
            intent.putExtra("uid",currentUser.uid)
            context.startActivity(intent)
        }
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName = itemView.findViewById<TextView>(R.id.txt_name)
        val textPhoneNumber = itemView.findViewById<TextView>(R.id.txt_phone_number)
    }

    companion object {
        private const val REQUEST_CONTACTS_PERMISSION = 123
    }
}







