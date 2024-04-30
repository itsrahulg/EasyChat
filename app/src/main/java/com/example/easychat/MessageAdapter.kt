package com.example.easychat

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter(val context: Context, val messageList: ArrayList<Message>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder.javaClass == SentViewHolder::class.java){
            //do the stuff for sent view holder
            val viewHolder = holder as SentViewHolder
        }else{
            //do stuff for receive view holder
            val viewHolder = holder as ReceiveViewHolder
        }
    }



    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    }
}
