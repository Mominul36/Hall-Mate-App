package com.example.hallmate.Fragments

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
import com.example.hallmate.Adapter.MStudentAdapter
import com.example.hallmate.Class.Loading2
import com.example.hallmate.Model.Student
import com.example.hallmate.databinding.FragmentMCurrentStudentBinding
import com.google.firebase.database.*
import java.io.File
import java.io.IOException
import jxl.Workbook
import jxl.write.*

class MCurrentStudentFragment : Fragment() {

    private lateinit var binding: FragmentMCurrentStudentBinding
    private lateinit var mStudentAdapter: MStudentAdapter
    private lateinit var studentList: ArrayList<Student>
    private lateinit var databaseRef: DatabaseReference
    private lateinit var load: Loading2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMCurrentStudentBinding.inflate(inflater, container, false)
        load = Loading2(requireContext())
        load.start()

        val sortingOptions = arrayOf("Room No", "Hall Id")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sortingOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = spinnerAdapter
        binding.spinner.setSelection(sortingOptions.indexOf("Hall Id"))

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                fetchStudents(binding.editSearch.text.toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.xlxs.setOnClickListener{
            if (studentList.isNotEmpty()) {
                saveDataAsExcelJXL(studentList)
            } else {
                Toast.makeText(requireContext(), "No student data available", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSearch.setOnClickListener {
            fetchStudents(binding.editSearch.text.toString())
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        studentList = ArrayList()
        mStudentAdapter = MStudentAdapter(studentList)
        binding.recyclerView.adapter = mStudentAdapter

        databaseRef = FirebaseDatabase.getInstance().getReference("Student")
        fetchStudents("")

        return binding.root
    }

    private fun fetchStudents(searchString: String) {
        val selectedSortOption = binding.spinner.selectedItem.toString()

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                studentList.clear()
                for (data in snapshot.children) {
                    val student = data.getValue(Student::class.java)
                    student?.let {
                        if (it.studentId?.contains(searchString) == true ||
                            it.roomNo?.contains(searchString) == true ||
                            it.hallId?.contains(searchString) == true ) {
                            if(it.batch!="1"){
                                studentList.add(it)
                            }
                        }
                    }
                }
                when (selectedSortOption) {
                    "Room No" -> studentList.sortBy { it.roomNo?.toIntOrNull() ?: 0 }
                    "Hall Id" -> studentList.sortBy { it.hallId ?: "" }
                }
                load.end()
                mStudentAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun saveDataAsExcelJXL(studentList: List<Student>) {
        try {
            // Get current date
            val date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())

            // Create directories
            val parentDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "HallMate")
            val subDir = File(parentDir, "Student Data")

            if (!subDir.exists()) subDir.mkdirs()  // Create folder if not exists

            // Find the next available file name dynamically
            val baseFileName = "Student Data_$date"
            var fileName = "$baseFileName.xls"
            var file = File(subDir, fileName)

            if (file.exists()) {
                // Get all existing files that match the pattern
                val existingFiles = subDir.listFiles { dir, name ->
                    name.startsWith(baseFileName) && name.endsWith(".xls")
                }?.map { it.name } ?: emptyList()

                // Extract numbers (_2, _3, _4, ...) and find the highest
                val maxIndex = existingFiles.mapNotNull { name ->
                    Regex("${baseFileName}_(\\d+)\\.xls").find(name)?.groupValues?.get(1)?.toIntOrNull()
                }.maxOrNull() ?: 1  // Default to 1 if no numbered files exist

                fileName = "${baseFileName}_${maxIndex + 1}.xls"
                file = File(subDir, fileName)
            }

            val workbook = Workbook.createWorkbook(file)
            val sheet = workbook.createSheet("Student Data", 0)

            // Header formatting (Bold, Gray Background, Borders)
            val headerFont = WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD)
            val headerFormat = WritableCellFormat(headerFont).apply {
                setBackground(Colour.GRAY_25)
                setBorder(Border.ALL, BorderLineStyle.THIN)
            }

            // Text format for all data cells
            val textFormat = WritableCellFormat(WritableFont(WritableFont.ARIAL, 10)).apply {
                this.setAlignment(Alignment.LEFT)  // Align text to the left
                this.setBorder(Border.ALL, BorderLineStyle.THIN)
                this.wrap = true  // Enable text wrapping if needed
            }

            // Headers
            val headers = listOf("Hall ID", "Room No", "Student ID", "Name", "Dept", "Batch")
            val columnWidths = IntArray(headers.size) { headers[it].length }  // Track column widths

            for ((index, header) in headers.withIndex()) {
                sheet.addCell(Label(index, 0, header, headerFormat))
            }

            // Add student data & adjust column widths
            for ((rowIndex, student) in studentList.withIndex()) {
                val row = rowIndex + 1

                val studentData = listOf(
                    student.hallId ?: "",
                    student.roomNo ?: "",
                    student.studentId ?: "",
                    student.name ?: "",
                    student.department ?: "",
                    student.batch ?: ""
                )

                for ((colIndex, data) in studentData.withIndex()) {
                    sheet.addCell(Label(colIndex, row, data, textFormat)) // Set all cells as text
                    columnWidths[colIndex] = maxOf(columnWidths[colIndex], data.length)
                }
            }

            // Set column widths dynamically
            for (colIndex in columnWidths.indices) {
                sheet.setColumnView(colIndex, columnWidths[colIndex] + 5)  // Add extra padding
            }

            workbook.write()
            workbook.close()

            Toast.makeText(requireContext(), "Excel saved: ${file.absolutePath}", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Failed to save Excel file!", Toast.LENGTH_LONG).show()
        }
    }




}
