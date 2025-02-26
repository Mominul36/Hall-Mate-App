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
import com.example.hallmate.Model.Notice

import com.example.hallmate.R

class ImageAdapter(
    private val context: Context,
    private val imageUrls: List<Notice>
) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    private var downloadId: Long = -1L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_notice, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Load the image using Picasso
        Picasso.get().load(imageUrls[position].url).into(holder.imageViewItem)

        // Handle the download icon click
        holder.downloadIcon.setOnClickListener {
            downloadImage(imageUrls[position].url!!)
        }

        // Handle image click for zooming
        holder.imageViewItem.setOnClickListener {
//            val intent = Intent(context, ZoomActivity::class.java)
//            intent.putExtra("imageUrl", imageUrls[position])
//            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return imageUrls.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewItem: ImageView = itemView.findViewById(R.id.imageViewItem)
        val downloadIcon: ImageView = itemView.findViewById(R.id.download_icon)
    }

    // Function to download image
    private fun downloadImage(imageUrl: String) {
        val request = DownloadManager.Request(Uri.parse(imageUrl))
            .setTitle("Downloading Image")
            .setDescription("Downloading the selected image")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "image_${System.currentTimeMillis()}.jpg")

        val downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadId = downloadManager.enqueue(request)

        // Register broadcast receiver for download completion
        context.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadId) {
                    Toast.makeText(context, "Download completed successfully!", Toast.LENGTH_LONG).show()
                }
            }
        }, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        Toast.makeText(context, "Download started", Toast.LENGTH_SHORT).show()
    }
}
