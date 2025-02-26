package com.example.hallmate.Adapter

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

import com.squareup.picasso.Picasso
import android.content.Context.DOWNLOAD_SERVICE
import com.example.hallmate.Model.MealForRV
import com.example.hallmate.Model.Notice

import com.example.hallmate.R

class TempAdapter(
    private val context: Context,
    private val list: List<String>
) : RecyclerView.Adapter<TempAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_notice, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {





    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    fun getPeriod(): List<String> {
        return list
    }

}
