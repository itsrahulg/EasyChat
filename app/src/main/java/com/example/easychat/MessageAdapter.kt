//package com.example.easychat
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.google.firebase.auth.FirebaseAuth
//
//class MessageAdapter(val context: Context, val messageList: ArrayList<Message>):
//    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//
//    val ITEM_RECEIVE = 1;
//    val ITEM_SENT = 2;
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        if(viewType == 1){
//            //inflate receive
//            val view: View =
//                LayoutInflater.from(context).inflate(R.layout.receive, parent, false)
//            return ReceiveViewHolder(view)
//        }else{
//            //inflate sent
//            val view: View =
//                LayoutInflater.from(context).inflate(R.layout.sent, parent, false)
//            return SentViewHolder(view)
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return messageList.size
//    }
//
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val currentMessage = messageList[position]
//
//        // Determine the type of ViewHolder
//        when (holder) {
//            is SentViewHolder -> {
//                // Set sent message text
//                holder.sentMessage.text = currentMessage.message
//                // Set margin top for the message layout
//                val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
//                if (position == 0) {
//                    // First message
//                    layoutParams.setMargins(layoutParams.leftMargin, 130, layoutParams.rightMargin, layoutParams.bottomMargin)
//                } else {
//                    // Subsequent messages
//                    layoutParams.setMargins(layoutParams.leftMargin, 10, layoutParams.rightMargin, layoutParams.bottomMargin)
//                }
//                holder.itemView.layoutParams = layoutParams
//            }
//            is ReceiveViewHolder -> {
//                // Set received message text
//                //holder.receiveMessage.text = currentMessage.message
//
//                holder.receiveMessage.text = currentMessage.message
//                // Set margin top for the message layout
//                val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
//                if (position == 0 || messageList[position - 1].senderId != currentMessage.senderId) {
//                    // First message or first message after sender change
//                    layoutParams.setMargins(layoutParams.leftMargin, 130, layoutParams.rightMargin, layoutParams.bottomMargin)
//                } else {
//                    // Subsequent messages
//                    layoutParams.setMargins(layoutParams.leftMargin, 10, layoutParams.rightMargin, layoutParams.bottomMargin)
//                }
//                holder.itemView.layoutParams = layoutParams
//            }
//        }
//    }
//
//
//
//
//
//    override fun getItemViewType(position: Int): Int {
//        val currentMessage = messageList[position]
//
//        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
//            return ITEM_SENT
//        }else{
//            return ITEM_RECEIVE
//        }
//    }
//
//    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
//        val sentMessage = itemView.findViewById<TextView>(R.id.txt_sent_message)
//    }
//
//    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
//        val receiveMessage = itemView.findViewById<TextView>(R.id.txt_receive_message)
//    }
//}

package com.example.easychat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(val context: Context, val messageList: ArrayList<Message>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVE = 1
    val ITEM_SENT = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_RECEIVE -> {
                // Inflate receive layout
                val view: View = LayoutInflater.from(context).inflate(R.layout.receive, parent, false)
                ReceiveViewHolder(view)
            }
            else -> {
                // Inflate sent layout
                val view: View = LayoutInflater.from(context).inflate(R.layout.sent, parent, false)
                SentViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]

        // Determine the type of ViewHolder
        when (holder) {
            is SentViewHolder -> {
                // Set sent message text
                holder.sentMessage.text = currentMessage.message
                // Set sent message timestamp
                holder.sentTimestamp.text = formatTimestamp(currentMessage.timestamp)
                // Set margin top for the message layout
                val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
                if (position == 0) {
                    // First message
                    layoutParams.setMargins(layoutParams.leftMargin, 130, layoutParams.rightMargin, layoutParams.bottomMargin)
                } else {
                    // Subsequent messages
                    layoutParams.setMargins(layoutParams.leftMargin, 10, layoutParams.rightMargin, layoutParams.bottomMargin)
                }
                holder.itemView.layoutParams = layoutParams
            }
            is ReceiveViewHolder -> {
                // Set received message text
                holder.receiveMessage.text = currentMessage.message
                // Set received message timestamp
                holder.receiveTimestamp.text = formatTimestamp(currentMessage.timestamp)
                // Set margin top for the message layout
                val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
                if (position == 0 || messageList[position - 1].senderId != currentMessage.senderId) {
                    // First message or first message after sender change
                    layoutParams.setMargins(layoutParams.leftMargin, 130, layoutParams.rightMargin, layoutParams.bottomMargin)
                } else {
                    // Subsequent messages
                    layoutParams.setMargins(layoutParams.leftMargin, 10, layoutParams.rightMargin, layoutParams.bottomMargin)
                }
                holder.itemView.layoutParams = layoutParams
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        return if (FirebaseAuth.getInstance().currentUser?.uid == currentMessage.senderId) {
            ITEM_SENT
        } else {
            ITEM_RECEIVE
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        // Format timestamp to a readable string
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentMessage: TextView = itemView.findViewById(R.id.txt_sent_message)
        val sentTimestamp: TextView = itemView.findViewById(R.id.txt_sent_timestamp)
    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiveMessage: TextView = itemView.findViewById(R.id.txt_receive_message)
        val receiveTimestamp: TextView = itemView.findViewById(R.id.txt_receive_timestamp)
    }
}
