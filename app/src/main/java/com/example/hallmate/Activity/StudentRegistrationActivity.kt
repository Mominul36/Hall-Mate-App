package com.example.hallmate.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hallmate.Class.DialogDismissListener
import com.example.hallmate.Class.Loading
import com.example.hallmate.Class.SuccessDialog
import com.example.hallmate.databinding.ActivityStudentRegistrationBinding
import com.example.hallmate.Model.Student
import com.example.hallmate.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.net.HttpURLConnection
import java.net.URL

class StudentRegistrationActivity : AppCompatActivity(), DialogDismissListener {

    private lateinit var binding: ActivityStudentRegistrationBinding
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    lateinit var load : Loading
    lateinit var success: SuccessDialog

    var flagPass = false
    var flagCPass = false

    var studentId :String = ""
    var name :String = ""
    var dept :String = ""
    var batch :String = ""
    var roomNo :String = ""
    var email :String = ""
    var phone :String = ""
    var password :String = ""
    var confirmPassword :String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        load = Loading(this)
        success= SuccessDialog(this,this)
        configureEditTextValidation()


        binding.goToLogin.setOnClickListener {
            startActivity(Intent(this, StudentLoginActivity::class.java))
            finish()
        }

        binding.signup.setOnClickListener {
            validateAndSaveData()
        }


        binding.next.setOnClickListener{
            validateFirstData()
        }



        binding.passHiddenIcon.setImageResource(R.drawable.ic_hide)
        binding.cPassHiddenIcon.setImageResource(R.drawable.ic_hide)
        binding.passHiddenIcon.setOnClickListener {
            if (flagPass) {
                binding.password.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.passHiddenIcon.setImageResource(R.drawable.ic_hide)
            } else {
                binding.password.transformationMethod = null
                binding.passHiddenIcon.setImageResource(R.drawable.show)
            }

            binding.password.setSelection(binding.password.text.length)
            flagPass = !flagPass
        }
        binding.cPassHiddenIcon.setOnClickListener{
            if (flagCPass) {
                binding.cPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.cPassHiddenIcon.setImageResource(R.drawable.ic_hide)
            } else {
                binding.cPassword.transformationMethod = null
                binding.cPassHiddenIcon.setImageResource(R.drawable.show)
            }

            binding.cPassword.setSelection(binding.cPassword.text.length)
            flagCPass = !flagCPass
        }


        binding.phone.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Check if the text is empty and set the prefix
                if (binding.phone.text.isNullOrEmpty()) {
                    binding.phone.setText("+8801")
                    binding.phone.setSelection(binding.phone.length())  // Move cursor to end
                }
            }
        }

// Prevent user from removing the prefix "+880"
        binding.phone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                if (charSequence?.startsWith("+8801") == false) {
                    binding.phone.setText("+8801")
                    binding.phone.setSelection(binding.phone.length())  // Move cursor to end
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })












    }

    private fun validateFirstData() {

        studentId = binding.studentId.text.toString().trim()
        name = binding.name.text.toString().trim()
        dept = binding.dept.text.toString().trim()
        batch = binding.batch.text.toString().trim()
        roomNo = binding.roomNo.text.toString().trim()

        if (studentId.isEmpty() || name.isEmpty() || dept.isEmpty() || batch.isEmpty() ||
            roomNo.isEmpty() ) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
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

        load.start()
        validateStudentId(studentId) { result ->
            val (isValid, message) = result
            if (!isValid) {
                load.end()
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }else{
                load.end()
                binding.firstLayout.visibility = View.GONE
                binding.secondLayout.visibility = View.VISIBLE

            }
        }




    }

    private fun validateAndSaveData() {


        val email = binding.email.text.toString().trim()
        val phone = binding.phone.text.toString().trim()
        val password = binding.password.text.toString().trim()
        val confirmPassword = binding.cPassword.text.toString().trim()

        // Validate fields
        if (email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
            return
        }

        if(!validatePhone(phone)){
            Toast.makeText(this, "Enter a valid phone number.", Toast.LENGTH_SHORT).show()
            return
        }

        val (isValid, message) = validatepassword(password,confirmPassword)
        if(!isValid){
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            return
        }

        load.start()
        validateEmail(email) { isValid2, message2 ->
            if (!isValid2) {
                load.end()
                Toast.makeText(this, message2, Toast.LENGTH_SHORT).show()
            } else {
                saveStudentData("",studentId,name,email,phone,dept.uppercase(),batch,roomNo,false,0.0,"","",password,"",false,false)
            }
        }









    }

    fun saveStudentData(
        hallId: String, studentId: String, name: String, email: String, phone: String,
        department: String, batch: String, roomNo: String, isCommitteeMember: Boolean,
        dueAmount: Double, key: String, profilePic: String,password:String,mealCode:String,
        isLock:Boolean,isMutton:Boolean
    ) {
        val studentRef = database.getReference("Student_Request").child(studentId)

        val student = Student(
            hallId = hallId,
            studentId = studentId,
            name = name,
            email = email,
            phone = phone,
            department = department,
            batch = batch,
            roomNo = roomNo,
            isCommitteeMember = isCommitteeMember,
            dueAmount = dueAmount,
            key = studentId, //
            profilePic = profilePic,
            password = password,
            mealCode = mealCode,
            isLock = isLock,
            isMutton = isMutton
        )

        studentRef.setValue(student)
            .addOnSuccessListener {

                load.end()
                success.show("Success","Registration Successful.\nCollect your Hall Id from Hall Manager",false,"")
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    @SuppressLint("SuspiciousIndentation")
    private fun validateEmail(email: String, callback: (Boolean, String) -> Unit) {
        // Step 1: Check Email Format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            callback(false, "Invalid email format.")
            return
        }

              val databaseRef = database.getReference("Student_Request")
              val databaseRef2 = database.getReference("Student")

                        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    var bl :Boolean = false
                                    for (requestSnapshot in snapshot.children) {
                                        val studentRequest = requestSnapshot.getValue(Student::class.java)

                                        studentRequest?.let {
                                            if(studentRequest.email==email){
                                                bl = true
                                            }
                                        }
                                    }

                                    if(bl){
                                        callback(false, "This email used in another account.")
                                    }else{

                                        databaseRef2.orderByChild("email").equalTo(email)
                                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    if (snapshot.exists()) {
                                                        callback(false, "This email used in another account.")
                                                    } else {
                                                        callback(true, "Email is available for registration.")
                                                    }
                                                }
                                                override fun onCancelled(error: DatabaseError) {
                                                    callback(false, "Error: ${error.message}")
                                                }
                                            })
                                    }

                                } else {
                                    databaseRef2.orderByChild("email").equalTo(email)
                                        .addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                if (snapshot.exists()) {
                                                    callback(false, "This email used in another account.")
                                                } else {
                                                    callback(true, "Email is available for registration.")
                                                }
                                            }
                                            override fun onCancelled(error: DatabaseError) {
                                                callback(false, "Error: ${error.message}")
                                            }
                                        })
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                callback(false, "Error: ${error.message}")
                            }
                        })


    }//


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
        val regex = "^\\+8801[3-9]\\d{8}$".toRegex()
        return phone.matches(regex)
    }

    private fun validateStudentId(studentId: String, callback: (Pair<Boolean, String>) -> Unit) {
        val studentRef = database.getReference("Student")
        val studentRequestRef = database.getReference("Student_Request")


                // Query Old_Student_Id_List
                studentRequestRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // Check if the studentId exists in Old_Student_Id_List
                        if (snapshot.hasChild(studentId)) {
                            callback(Pair(false, "Someone has registered using your id. Please contact with Hall Manager."))
                            return
                        }


                        studentRef.orderByChild("studentId").equalTo(studentId)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        callback(Pair(false, "You have already an account."))
                                    } else {
                                        callback(Pair(true, "Student ID is valid."))
                                    }
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

        binding.batch.filters = arrayOf(lf_digit_only)


//        val lf_phone = InputFilter { source, start, end, dest, dstart, dend ->
//            if (source.toString().matches("^[0-9+]*$".toRegex())) {
//                null
//            } else {
//                ""
//            }
//        }
//        binding.phone.filters = arrayOf(lf_phone)

        val lf_dept = InputFilter { source, start, end, dest, dstart, dend ->
            if (source.matches("^[a-zA-Z]+".toRegex())) {
                null
            } else {
                ""
            }
        }
        binding.dept.filters = arrayOf(lf_dept)
    }

    override fun onDialogDismissed(message: String) {
    finish()
    }

}
