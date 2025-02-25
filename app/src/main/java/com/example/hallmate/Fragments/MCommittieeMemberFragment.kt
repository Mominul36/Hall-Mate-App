package com.example.hallmate.Fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hallmate.Adapter.MCommittieeMemberAdapter
import com.example.hallmate.Adapter.MStudentAdapter
import com.example.hallmate.Class.DialogDismissListener
import com.example.hallmate.Class.Loading
import com.example.hallmate.Class.Loading2
import com.example.hallmate.Class.SuccessDialog
import com.example.hallmate.Model.Student
import com.example.hallmate.R
import com.example.hallmate.databinding.FragmentMCommittieeMemberBinding
import com.example.hallmate.databinding.FragmentMCurrentStudentBinding
import com.google.firebase.database.*
import java.io.File
import java.io.IOException
import jxl.Workbook
import jxl.write.*

class MCommittieeMemberFragment : Fragment(), DialogDismissListener {

    private lateinit var binding: FragmentMCommittieeMemberBinding
    private lateinit var mCommittieeMemberAdapter: MCommittieeMemberAdapter
    private lateinit var studentList: ArrayList<Student>
    private lateinit var databaseRef: DatabaseReference
    private lateinit var load: Loading


    lateinit var successDialog: SuccessDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMCommittieeMemberBinding.inflate(inflater, container, false)
        load = Loading(requireContext())
        successDialog = SuccessDialog(requireContext(), this)

        binding.btnAdd.setOnClickListener {
            addMember()
        }


        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        studentList = ArrayList()
        mCommittieeMemberAdapter = MCommittieeMemberAdapter(requireContext(),load,successDialog,studentList)
        binding.recyclerView.adapter = mCommittieeMemberAdapter

        databaseRef = FirebaseDatabase.getInstance().getReference("Student")
        fetchStudents("")

        return binding.root
    }

    private fun addMember() {
        load.start()
        val editAdd = binding.editAdd.text.toString()

        if (editAdd.length != 4 || !editAdd.all { it.isDigit() }) {
            load.end()
            Toast.makeText(requireContext(), "Invalid Hall ID.", Toast.LENGTH_SHORT).show()
            return
        }

        // Reference to the "Student" table
        val studentRef = FirebaseDatabase.getInstance().getReference("Student")

        // Add ValueEventListener to listen for data changes
        studentRef.orderByChild("hallId").equalTo(editAdd).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (studentSnapshot in dataSnapshot.children) {
                        val student = studentSnapshot.getValue(Student::class.java)
                        student?.let {
                            if(student.batch=="1"){
                                load.end()
                                Toast.makeText(requireContext(), "Student not found.", Toast.LENGTH_SHORT).show()
                            }else{
                                if(student.isCommitteeMember==true){
                                    load.end()
                                    Toast.makeText(requireContext(), "${student.name} is already a committee member", Toast.LENGTH_SHORT).show()
                                }else{
                                    studentSnapshot.ref.child("isCommitteeMember").setValue(true)
                                    load.end()
                                    successDialog.show("Success","${student.name} is now a committee member!",true,"")

                                }
                            }
                        }
                    }
                } else {
                    load.end()
                    Toast.makeText(requireContext(), "Student not found.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                load.end()
                Toast.makeText(requireContext(), "Database error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



    private fun fetchStudents(searchString: String) {

        load.start()

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                studentList.clear()
                for (data in snapshot.children) {
                    val student = data.getValue(Student::class.java)
                    student?.let {
                            if(it.isCommitteeMember==true){
                                studentList.add(it)
                            }
                    }
                }

                load.end()
                mCommittieeMemberAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }




    override fun onDialogDismissed(message: String) {

    }


}
