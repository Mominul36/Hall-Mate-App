package com.example.hallmate.Adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.hallmate.Fragments.MCurrentStudentFragment
import com.example.hallmate.Fragments.MDailyReportFragment
import com.example.hallmate.Fragments.MHallBillFragment
import com.example.hallmate.Fragments.MStudentRequestFragment

class ReportViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragmentList = listOf(
        MDailyReportFragment(),
        MHallBillFragment()
    )

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment = fragmentList[position]
}
