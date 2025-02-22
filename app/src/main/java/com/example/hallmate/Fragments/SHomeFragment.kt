package com.example.hallmate.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hallmate.Class.Loading2
import com.example.hallmate.R
import com.example.hallmate.databinding.FragmentMStudentRequestBinding
import com.example.hallmate.databinding.FragmentSHomeBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SHomeFragment : Fragment() {

    private lateinit var binding: FragmentSHomeBinding
    private var database = FirebaseDatabase.getInstance()

    lateinit var load : Loading2


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSHomeBinding.inflate(inflater, container, false)
        load = Loading2(requireContext())

















        return binding.root

    }


}