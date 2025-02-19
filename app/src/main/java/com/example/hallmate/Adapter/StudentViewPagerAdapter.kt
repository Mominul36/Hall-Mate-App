package com.example.hallmate.Adapter


import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.hallmate.Activity.MStudentActivity
import com.example.hallmate.Fragments.MCurrentStudentFragment
import com.example.hallmate.Fragments.MStudentRequestFragment

class StudentViewPagerAdapter(activity: MStudentActivity) : FragmentStateAdapter(activity) {

    private val fragmentList = listOf(
        MStudentRequestFragment(),
        MCurrentStudentFragment()
    )
    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment = fragmentList[position]
}
