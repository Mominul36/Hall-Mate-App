package com.example.hallmate.Adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.hallmate.Fragments.MCommittieeMemberFragment
import com.example.hallmate.Fragments.MCurrentStudentFragment
import com.example.hallmate.Fragments.MStaffFragment
import com.example.hallmate.Fragments.MStudentRequestFragment

class StaffViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragmentList = listOf(
        MStaffFragment(),
        MCommittieeMemberFragment()
    )

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment = fragmentList[position]
}
