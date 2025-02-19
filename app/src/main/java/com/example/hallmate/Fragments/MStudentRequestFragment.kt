package com.example.hallmate.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hallmate.Adapter.StudentRequestAdapter
import com.example.hallmate.Class.Loading
import com.example.hallmate.Class.Loading2
import com.example.hallmate.Model.Student
import com.example.hallmate.databinding.FragmentMStudentRequestBinding
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.database.*

class MStudentRequestFragment : Fragment() {
    private lateinit var binding: FragmentMStudentRequestBinding
    private lateinit var studentRequestAdapter: StudentRequestAdapter
    private lateinit var studentList: ArrayList<Student>
    private lateinit var databaseRef: DatabaseReference


   lateinit var load : Loading2



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMStudentRequestBinding.inflate(inflater, container, false)
        load = Loading2(requireContext())
        load.start()

        binding.btnSearch.setOnClickListener{
            fetchStudents(binding.editSearch.text.toString())
        }









        // RecyclerView setup with vertical scrolling
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        studentList = ArrayList()
        studentRequestAdapter = StudentRequestAdapter(studentList,"student_request")
        binding.recyclerView.adapter = studentRequestAdapter

        // Fetch data from Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference("Student_Request")
        fetchStudents("")

        return binding.root
    }

    private fun fetchStudents(searchString: String) {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                studentList.clear()
                for (data in snapshot.children) {
                    val student = data.getValue(Student::class.java)
                    student?.let {
                        // Check if the studentId or roomNo contains the search string
                        if (it.studentId?.contains(searchString)==true  ||
                            it.roomNo?.contains(searchString) == true) {
                            studentList.add(it)
                        }
                    }
                }
                studentList.sortBy { it.roomNo?.toIntOrNull() ?: 0 }
                load.end()
                studentRequestAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors here
            }
        })
    }

}


