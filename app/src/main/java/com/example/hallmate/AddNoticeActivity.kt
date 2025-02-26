package com.example.hallmate

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.hallmate.Class.Loading
import com.example.hallmate.Model.Notice
import com.example.hallmate.databinding.ActivityAddNoticeBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddNoticeActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddNoticeBinding
    private lateinit var imageUri: Uri
    private val PICK_IMAGE_REQUEST = 71
    lateinit var load: Loading


    private lateinit var storageRef: StorageReference
    private lateinit var databaseRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        load = Loading(this)

        storageRef = FirebaseStorage.getInstance().reference

        binding.buttonChooseImage.setOnClickListener {
            selectImage()
        }

        binding.back.setOnClickListener{
            finish()
        }

        binding.buttonUpload.setOnClickListener {
            if (::imageUri.isInitialized) {
                uploadImage()
            } else {
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.data!!
            // Load the selected image into the ImageView using Glide
            Glide.with(this)
                .load(imageUri)
                .into(binding.imageView)

            val layoutParams = binding.imageView.layoutParams
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            binding.imageView.layoutParams = layoutParams
            binding.imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE


        // Assuming you have an ImageView in your layout named imageView
        }
    }

    private fun uploadImage() {
        load.start()
        databaseRef = FirebaseDatabase.getInstance().getReference("Notice")

        val fileName = System.currentTimeMillis().toString()
        val imageRef = storageRef.child("images/$fileName.jpg")

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                    val uploadId = sdf.format(Date())
                    if (uploadId != null) {

                        val notice = Notice(uploadId,imageUrl)
                        databaseRef.child(uploadId).setValue(notice)
                            .addOnCompleteListener {
                                load.end()
                                finish()
                                Toast.makeText(this, "Notice uploaded successfully", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }



}