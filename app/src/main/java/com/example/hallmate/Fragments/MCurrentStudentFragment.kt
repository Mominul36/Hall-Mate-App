package com.example.hallmate.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hallmate.R
import com.example.hallmate.databinding.FragmentMCurrentStudentBinding


class MCurrentStudentFragment : Fragment() {
    lateinit var binding: FragmentMCurrentStudentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentMCurrentStudentBinding.inflate(inflater,container,false)















        return binding.root
    }

}