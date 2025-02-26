package com.example.hallmate.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hallmate.Adapter.ImageAdapter
import com.example.hallmate.AddNoticeActivity
import com.example.hallmate.Class.Loading
import com.example.hallmate.Model.Notice

import com.example.hallmate.databinding.ActivityMnoticeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MNoticeActivity : AppCompatActivity() {

    lateinit var binding: ActivityMnoticeBinding


    private lateinit var storageRef: StorageReference
    private lateinit var databaseRef: DatabaseReference
    private lateinit var adapter: ImageAdapter
    private val imageList = ArrayList<Notice>()
    lateinit var load : Loading

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMnoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        load = Loading(this)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)


        storageRef = FirebaseStorage.getInstance().reference

        adapter = ImageAdapter(this, imageList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter


        binding.back.setOnClickListener{
            finish()
        }

        binding.add.setOnClickListener{
        startActivity(Intent(this,AddNoticeActivity::class.java))
        }

        showPicture()






    }

    private fun showPicture() {
        load.start()
        databaseRef = FirebaseDatabase.getInstance().getReference("Notice")

        databaseRef.limitToLast(10).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                imageList.clear()
                if(snapshot.exists()){
                    for (imageSnapshot in snapshot.children) {
                        val notice = imageSnapshot.getValue(Notice::class.java)
                        if (notice != null) {

                            imageList.add(notice)
                        }
                    }
                    imageList.sortByDescending { it.url }
                    adapter.notifyDataSetChanged()

                }else{

                }


            }

            override fun onCancelled(error: DatabaseError) {
                load.end()
                Toast.makeText(this@MNoticeActivity, "Failed to load images", Toast.LENGTH_SHORT).show()
            }
        })
        load.end()

    }





}