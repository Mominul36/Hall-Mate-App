package com.example.hallmate.Activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hallmate.databinding.ActivityStudentRegistrationBinding
import com.example.hallmate.Model.Student
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.net.HttpURLConnection
import java.net.URL

class StudentRegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentRegistrationBinding
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureEditTextValidation()
        binding.progressBar.visibility = View.GONE
        binding.main.visibility = View.VISIBLE

        binding.goToLogin.setOnClickListener {
            startActivity(Intent(this, StudentLoginActivity::class.java))
            finish()
        }

        binding.signup.setOnClickListener {
            validateAndSaveData()
        }


        binding.password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()
                binding.passTypeLayout.visibility = if (password.isEmpty()) View.GONE else View.VISIBLE

                val strength = getPasswordStrength(password)
                binding.passwordStrengthText.text = strength.first
                binding.passwordStrengthText.setTextColor(strength.second)
            }
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
        })



    }

    private fun validateAndSaveData() {
        val studentId = binding.studentId.text.toString().trim()
        val name = binding.name.text.toString().trim()
        val dept = binding.dept.text.toString().trim()
        val batch = binding.batch.text.toString().trim()
        val roomNo = binding.roomNo.text.toString().trim()
        val email = binding.email.text.toString().trim()
        val phone = binding.phone.text.toString().trim()
        val password = binding.password.text.toString().trim()
        val confirmPassword = binding.cPassword.text.toString().trim()

        // Validate fields
        if (studentId.isEmpty() || name.isEmpty() || dept.isEmpty() || batch.isEmpty() ||
            roomNo.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
            return
        }

        if(!validatePhone(phone)){
            Toast.makeText(this, "Enter a valid phone number.", Toast.LENGTH_SHORT).show()
            return
        }

        if(!validateBatch(batch)){
            Toast.makeText(this, "Enter your Correct Batch No", Toast.LENGTH_SHORT).show()
            return
        }

        if(!validateRoomNo(roomNo)){
            Toast.makeText(this, "Enter your Correct Room No.", Toast.LENGTH_SHORT).show()
            return
        }
        
        val (isValid, message) = validatepassword(password,confirmPassword)
        if(!isValid){
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.main.visibility = View.GONE

        validateStudentId(studentId) { result ->
            val (isValid, message) = result
            if (!isValid) {
                binding.progressBar.visibility = View.GONE
                binding.main.visibility = View.VISIBLE
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }else{

                validateEmail(email) { isValid2, message2 ->
                    if (!isValid2) {
                        binding.progressBar.visibility = View.GONE
                        binding.main.visibility = View.VISIBLE
                        Toast.makeText(this, message2, Toast.LENGTH_SHORT).show()
                    } else {

                        auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
                            saveStudentData("",studentId,name,email,phone,dept.uppercase(),batch,roomNo,false,0.0,"","",password)
                        }.addOnFailureListener { exception ->
                            binding.progressBar.visibility = View.GONE
                            binding.main.visibility = View.VISIBLE
                            Toast.makeText(this, "${exception.message}", Toast.LENGTH_SHORT).show()
                        }





                        
                    }
                }
            }
        }
    }

    fun saveStudentData(
        hallId: String, studentId: String, name: String, email: String, phone: String,
        dept: String, batch: String, roomNo: String, isCommitteeMember: Boolean,
        dueAmount: Double, key: String, profilePic: String,password:String
    ) {
        val studentRef = database.getReference("Student_Request").child(studentId)

        val student = Student(
            hallId = hallId,
            studentId = studentId,
            name = name,
            email = email,
            phone = phone,
            dept = dept,
            batch = batch,
            roomNo = roomNo,
            isCommitteeMember = isCommitteeMember,
            dueAmount = dueAmount,
            key = studentId, //
            profilePic = profilePic,
            password = password
        )

        studentRef.setValue(student)
            .addOnSuccessListener {
                binding.progressBar.visibility = View.GONE
                binding.main.visibility = View.VISIBLE
                Toast.makeText(this, "Student Data Successfully Saved!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun validateEmail(email: String, callback: (Boolean, String) -> Unit) {
        // Step 1: Check Email Format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            callback(false, "Invalid email format.")
            return
        }

        // Step 2: Check if email exists in Firebase Authentication
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods ?: emptyList()

                    if (signInMethods.isNotEmpty()) {
                        callback(false, "This email used in another account.")
                    } else {
                        callback(true, "Email is available for registration.")
                    }
                } else {
                    callback(false, "Error checking email.")
                }
            }
    }


    private fun getPasswordStrength(password: String): Pair<String, Int> {
        // Check for strong password: Must have lowercase, uppercase, digit, and special character
        return when {
            // Strong password: Contains lowercase letter, uppercase letter, digit, and special character
            password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\\$%^&\\*\\(\\)_\\+\\-\\=\\[\\]\\{\\};:'\\\",<>\\./?\\\\|])[A-Za-z\\d!@#\\$%^&\\*\\(\\)_\\+\\-\\=\\[\\]\\{\\};:'\\\",<>\\./?\\\\|]{6,}$".toRegex()) -> {
                Pair("Strong Password", Color.GREEN) // Green for strong
            }
            // Medium password: Must have at least two character types, e.g., letters & digits or digits & special characters
            password.matches("^(?=.*[a-zA-Z])(?=.*\\d)[A-Za-z\\d]{6,}$".toRegex()) ||
                    password.matches("^(?=.*[a-zA-Z])(?=.*[!@#\\$%^&\\*\\(\\)_\\+\\-\\=\\[\\]\\{\\};:'\\\",<>\\./?\\\\|])[A-Za-z!@#\\$%^&\\*\\(\\)_\\+\\-\\=\\[\\]\\{\\};:'\\\",<>\\./?\\\\|]{6,}$".toRegex()) ||
                    password.matches("^(?=.*\\d)(?=.*[!@#\\$%^&\\*\\(\\)_\\+\\-\\=\\[\\]\\{\\};:'\\\",<>\\./?\\\\|])[A-Za-z\\d!@#\\$%^&\\*\\(\\)_\\+\\-\\=\\[\\]\\{\\};:'\\\",<>\\./?\\\\|]{6,}$".toRegex()) -> {
                Pair("Medium Password", Color.parseColor("#FFA500"))
            }
            // Weak password: Contains only letters or only digits or only special characters
            password.matches("^[a-zA-Z]{6,}$".toRegex()) || // Only letters
                    password.matches("^[0-9]{6,}$".toRegex()) || // Only digits
                    password.matches("^[!@#\\$%^&\\*\\(\\)_\\+\\-\\=\\[\\]\\{\\};:'\\\",<>\\./?\\\\|]{6,}$".toRegex()) -> { // Only special characters
                Pair("Weak Password", Color.RED) // Red for weak
            }
            // Invalid password: Doesn't meet any of the above criteria
            password.length < 6 -> {
                Pair("Weak Password", Color.RED) // Red for weak
            }
            else -> {
                Pair("Invalid Password", Color.GRAY) // Default invalid password
            }
        }
    }

    private fun validatepassword(password: String, confirmPassword: String): Pair<Boolean,String> {
        if(password.matches("^(?=.*[a-zA-Z])(?=.*\\d|(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};:'\",.<>?/\\\\|]))[A-Za-z\\d!@#\$%^&*()_+\\-=\\[\\]{};:'\",.<>?/\\\\|]{6,}$".toRegex())){
            if (password != confirmPassword) {
                return Pair(false,"Passwords do not match!")
            }
            return Pair(true,"Valid password")
        }else{
            return Pair(false,"Enter a Strong Password")
        }
    }
    
    private fun validateBatch(batch: String): Boolean {
        if(batch.toInt()<10 || batch.toInt()>25)
            return false
        return true
    }

    private fun validateRoomNo(roomNo: String): Boolean {
        if(roomNo.toInt()>999)
            return false
        return true
    }

    fun validatePhone(phone: String): Boolean {
        val regex = "^01[3-9]\\d{8}$".toRegex()
        return phone.matches(regex)
    }

    private fun validateStudentId(studentId: String, callback: (Pair<Boolean, String>) -> Unit) {
        val studentIdRef = database.getReference("Student_Id_List")
        val studentRequestRef = database.getReference("Student_Request")

        studentIdRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Check if the studentId exists in Student_Id_List
                if (snapshot.hasChild(studentId)) {
                    callback(Pair(false, "You already have an Account."))
                    return
                }
                // Query Old_Student_Id_List
                studentRequestRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // Check if the studentId exists in Old_Student_Id_List
                        if (snapshot.hasChild(studentId)) {
                            callback(Pair(false, "Someone has registered using your id. Please contact with Hall Manager."))
                            return
                        }

                        // If the studentId doesn't exist in either list, it's valid
                        callback(Pair(true, "Student ID is valid."))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        callback(Pair(false, "Error: ${error.message}"))
                    }
                })
            }
            override fun onCancelled(error: DatabaseError) {
                callback(Pair(false, "Error: ${error.message}"))
            }
        })
    }
    
    private fun configureEditTextValidation() {
        val lf_name = InputFilter { source, start, end, dest, dstart, dend ->
            if (source.matches("^[ a-zA-Z.-]+".toRegex())) {
                null
            } else {
                ""
            }
        }
        binding.name.filters = arrayOf(lf_name)

        val lf_digit_only = InputFilter { source, start, end, dest, dstart, dend ->
            if (source.matches("^[0-9]".toRegex())) {
                null
            } else {
                ""
            }
        }
        binding.studentId.filters = arrayOf(lf_digit_only)
        binding.roomNo.filters = arrayOf(lf_digit_only)
        binding.phone.filters = arrayOf(lf_digit_only)
        binding.batch.filters = arrayOf(lf_digit_only)

        val lf_dept = InputFilter { source, start, end, dest, dstart, dend ->
            if (source.matches("^[a-zA-Z]+".toRegex())) {
                null
            } else {
                ""
            }
        }
        binding.dept.filters = arrayOf(lf_dept)
    }
    
}
