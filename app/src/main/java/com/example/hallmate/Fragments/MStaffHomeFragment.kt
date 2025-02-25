package com.example.hallmate.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hallmate.Adapter.StaffViewPagerAdapter
import com.example.hallmate.Adapter.StudentViewPagerAdapter
import com.example.hallmate.Class.Loading
import com.example.hallmate.R
import com.example.hallmate.databinding.FragmentMStaffHomeBinding
import com.example.hallmate.databinding.FragmentMStudentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator


class MStaffHomeFragment : Fragment() {

    private lateinit var binding: FragmentMStaffHomeBinding






    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMStaffHomeBinding.inflate(inflater, container, false)

        val adapter = StaffViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        val tabTitles = arrayOf("Staff", "Food Com.")
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        binding.viewPager.isUserInputEnabled = false







        return binding.root
    }


}