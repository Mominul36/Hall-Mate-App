package com.example.hallmate.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hallmate.Adapter.StudentViewPagerAdapter
import com.example.hallmate.Class.Loading
import com.example.hallmate.Class.Loading2
import com.example.hallmate.R
import com.example.hallmate.databinding.FragmentMStudentHomeBinding
import com.example.hallmate.databinding.FragmentMStudentRequestBinding
import com.google.android.material.tabs.TabLayoutMediator


class MStudentHomeFragment : Fragment() {
    private lateinit var binding: FragmentMStudentHomeBinding

    lateinit var load : Loading


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMStudentHomeBinding.inflate(inflater, container, false)
        load = Loading(requireContext())


        val adapter = StudentViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        val tabTitles = arrayOf("Student", "Student Request")
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        binding.viewPager.isUserInputEnabled = false












return binding.root
    }


}