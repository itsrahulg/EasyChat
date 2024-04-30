package com.example.easychat

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>

    val receiverRoom: String? = null
    val senderRoom: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)


        val name = intent.getStringExtra("name")
        val uid = intent.getStringExtra("uid")

        supportActionBar?.title = name

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sentButton)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this,messageList)

        sendButton.setOnClickListener{

        }
    }
}