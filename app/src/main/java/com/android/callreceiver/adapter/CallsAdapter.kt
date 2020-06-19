package com.android.callreceiver.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.callreceiver.R
import com.android.callreceiver.adapter.CallsAdapter.ViewHolder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CallsAdapter(private val items: ArrayList<CallItem>) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.default_call, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val call = items[position]
        holder.phone.text = call.phoneNumber
        holder.date.text = call.date
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val phone = view.findViewById<TextView>(R.id.phone)!!
        val date = view.findViewById<TextView>(R.id.date)!!
    }

    class CallItem(val phoneNumber: String, date: Date){
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(date)!!
    }
}