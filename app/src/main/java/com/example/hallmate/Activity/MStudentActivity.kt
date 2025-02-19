package com.example.hallmate.Activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hallmate.Adapter.StudentViewPagerAdapter
import com.example.hallmate.R
import com.example.hallmate.databinding.ActivityMprofileBinding
import com.example.hallmate.databinding.ActivityMstudentBinding
import com.google.android.material.tabs.TabLayoutMediator

class MStudentActivity : AppCompatActivity() {
    lateinit var binding: ActivityMstudentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMstudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = StudentViewPagerAdapter(this)
        binding.viewPager.adapter = adapter
        val tabTitles = arrayOf("Student Request", "Student List")
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        binding.viewPager.isUserInputEnabled = true


        binding.back.setOnClickListener{
            finish()
        }


    }
}