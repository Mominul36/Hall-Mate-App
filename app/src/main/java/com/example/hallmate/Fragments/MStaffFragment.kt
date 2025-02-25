package com.example.hallmate.Fragments

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hallmate.Activity.AddStaffActivity
import com.example.hallmate.Adapter.MStudentAdapter
import com.example.hallmate.Adapter.StaffAdapter

import com.example.hallmate.Class.Loading2
import com.example.hallmate.Model.Student
import com.example.hallmate.databinding.FragmentMCurrentStudentBinding
import com.example.hallmate.databinding.FragmentMStaffBinding
import com.google.firebase.database.*
import java.io.File
import java.io.IOException
import jxl.Workbook
import jxl.write.*

class MStaffFragment : Fragment() {

    private lateinit var binding: FragmentMStaffBinding
    private lateinit var staffAdapter: StaffAdapter
    private lateinit var staffList: ArrayList<Student>
    private lateinit var databaseRef: DatabaseReference
    private lateinit var load: Loading2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMStaffBinding.inflate(inflater, container, false)
        load = Loading2(requireContext())
        load.start()




        binding.add.setOnClickListener {
           startActivity(Intent(requireContext(),AddStaffActivity::class.java))
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        staffList = ArrayList()
        staffAdapter = StaffAdapter(staffList)
        binding.recyclerView.adapter = staffAdapter

        databaseRef = FirebaseDatabase.getInstance().getReference("Student")
        fetchStudents()

        return binding.root
    }

    private fun fetchStudents() {
        load.start()

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                staffList.clear()
                for (data in snapshot.children) {
                    val student = data.getValue(Student::class.java)
                    student?.let {
                            if(it.batch=="1"){
                                staffList.add(it)
                            }
                    }
                }

                load.end()
                staffAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }






}
