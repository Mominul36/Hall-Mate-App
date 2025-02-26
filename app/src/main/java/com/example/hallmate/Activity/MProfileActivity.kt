package com.example.hallmate.Activity

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hallmate.Class.ControlImage
import com.example.hallmate.Class.DialogDismissListener
import com.example.hallmate.Class.Loading
import com.example.hallmate.Class.SuccessDialog
import com.example.hallmate.MainActivity
import com.example.hallmate.R
import com.example.hallmate.databinding.ActivityManagerHomeBinding
import com.example.hallmate.databinding.ActivityMprofileBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.database.FirebaseDatabase

class MProfileActivity : AppCompatActivity(), DialogDismissListener {

    lateinit var  name : String
    lateinit var  designation : String
    lateinit var  phone : String
    lateinit var  email : String
    lateinit var  password : String
    lateinit var  profilePic : String
    lateinit var  userType : String

    lateinit var binding: ActivityMprofileBinding
    lateinit var auth : FirebaseAuth
    var database = FirebaseDatabase.getInstance()

    var flagOldPass = false
    var flagNewPass = false

    lateinit var successDialog :SuccessDialog
    lateinit var load:Loading

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMprofileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        successDialog = SuccessDialog(this,this)
        load = Loading(this)


        setUserDetails()


        binding.back.setOnClickListener{
            finish()
        }

        binding.details.setOnClickListener {
            showDetailsLayout()
        }

        binding.password.setOnClickListener {
            showPasswordLayout()
        }
        binding.passchange.setOnClickListener{
            makeChangePassword()
        }

        binding.dialogName.setOnClickListener{
            showNameDialog()
        }

        binding.dialogPhone.setOnClickListener{
            showPhoneDialog()
        }

        binding.dialogEmail.setOnClickListener{
            showEmailDialog()
        }

        binding.pic.setOnClickListener{}


        binding.oldHiddenIcon.setImageResource(R.drawable.ic_hide)
        binding.newHiddenIcon.setImageResource(R.drawable.ic_hide)
        binding.oldHiddenIcon.setOnClickListener {
            if (flagOldPass) {
                binding.oldpass.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.oldHiddenIcon.setImageResource(R.drawable.ic_hide)
            } else {
                binding.oldpass.transformationMethod = null
                binding.oldHiddenIcon.setImageResource(R.drawable.show)
            }

            binding.oldpass.setSelection(binding.oldpass.text.length)
            flagOldPass = !flagOldPass
        }
        binding.newHiddenIcon.setOnClickListener{
            if (flagNewPass) {
                binding.newpass.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.newHiddenIcon.setImageResource(R.drawable.ic_hide)
            } else {
                binding.newpass.transformationMethod = null
                binding.newHiddenIcon.setImageResource(R.drawable.show)
            }

            binding.newpass.setSelection(binding.newpass.text.length)
            flagNewPass = !flagNewPass
        }








    }


    private fun showPhoneDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_update_management_data)

        // Get references to EditText and Button
        val editphone = dialog.findViewById<EditText>(R.id.data)
        val update = dialog.findViewById<Button>(R.id.update)
        val cross = dialog.findViewById<ImageView>(R.id.cross)

        editphone.setText(phone)

        cross.setOnClickListener{
            dialog.dismiss()
        }

        update.setOnClickListener {
            load.start()
            val phone1 = editphone.text.toString()
            if(phone==phone1){
                load.end()
                dialog.dismiss()
                Toast.makeText(this, "Phone Number updated successfully", Toast.LENGTH_SHORT).show()
            }else{
                if (phone1.isEmpty()) {
                    load.end()
                    Toast.makeText(this, "Enter Valid phone number.\n Like: +8801*********", Toast.LENGTH_SHORT).show()
                } else {
                    if(validatePhone(phone1)){
                        val databaseRef = database.getReference("Management")
                        val key  = email.substringBefore("@")

                        val nameUpdate = hashMapOf<String, Any>(
                            "phone" to phone1
                        )

                        databaseRef.child(key).updateChildren(nameUpdate)
                            .addOnSuccessListener {
                                var sharedPreferences = getSharedPreferences("HallMatePreferences", MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString("phone",phone1)
                                editor.apply()
                                phone = phone1
                                binding.phone.setText(phone)
                                load.end()
                                Toast.makeText(this, "Phone Number updated successfully", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { exception ->
                                load.end()
                                Toast.makeText(this, "Error updating Phone: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                        dialog.dismiss()



                    }else{
                        load.end()
                        Toast.makeText(this, "Enter Valid phone number.\n Like: +8801*********", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        dialog.show()
    }

    private fun showNameDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_update_management_data)

        // Get references to EditText and Button
        val editname = dialog.findViewById<EditText>(R.id.data)
        val update = dialog.findViewById<Button>(R.id.update)
        val cross = dialog.findViewById<ImageView>(R.id.cross)

        editname.setText(name)

        cross.setOnClickListener{
            dialog.dismiss()
        }

        update.setOnClickListener {
            load.start()
            val name1 = editname.text.toString()

            if (name1.isEmpty()) {
                load.end()
                Toast.makeText(this, "Enter your Name", Toast.LENGTH_SHORT).show()
            } else {

                val databaseRef = database.getReference("Management")

                val key  = email.substringBefore("@")

                val nameUpdate = hashMapOf<String, Any>(
                    "name" to name1
                )

                databaseRef.child(key).updateChildren(nameUpdate)
                    .addOnSuccessListener {
                        var sharedPreferences = getSharedPreferences("HallMatePreferences", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("name",name1)
                        editor.apply()
                        name = name1
                        binding.name.setText(name)
                        load.end()
                        Toast.makeText(this, "Name updated successfully", Toast.LENGTH_SHORT).show()


                    }
                    .addOnFailureListener { exception ->
                        load.end()
                        Toast.makeText(this, "Error updating name: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                dialog.dismiss()
            }
        }

        dialog.show()



    }

    private fun showEmailDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_update_management_email)

        val editemail = dialog.findViewById<EditText>(R.id.email)
        val editpassword = dialog.findViewById<EditText>(R.id.password)
        val update = dialog.findViewById<Button>(R.id.update)
        val cross = dialog.findViewById<ImageView>(R.id.cross)

        editemail.setText(email)

        cross.setOnClickListener {
            dialog.dismiss()
        }

        update.setOnClickListener {
            load.start()
            val email1 = editemail.text.toString()
            val password1 = editpassword.text.toString()

            if (email == email1) {
                load.end()
                dialog.dismiss()
                successDialog.show("Success","Email updated successfully",true,"")
            } else {
                if (password1.isEmpty() || email1.isEmpty()) {
                    load.end()
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                } else {
                    // Check if email already exists
                    FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email1)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val result = task.result
                                val signInMethods = result?.signInMethods

                                // If the email is already used (signInMethods is not empty), show an error
                                if (signInMethods != null && signInMethods.isNotEmpty()) {
                                    load.end()
                                    Toast.makeText(this, "Email already used.", Toast.LENGTH_SHORT).show()
                                } else {
                                    // Reauthenticate the user if the email is different
                                    val user = FirebaseAuth.getInstance().currentUser
                                    if (user != null) {
                                        val credential = EmailAuthProvider.getCredential(user.email!!, password1)

                                        user.reauthenticate(credential)
                                            .addOnSuccessListener {
                                                // Reauthentication successful, proceed to update email
                                                deleteEmailInAuth(email1,password1, dialog)
                                            }
                                            .addOnFailureListener { exception ->
                                                if (exception is FirebaseAuthInvalidCredentialsException) {
                                                    // If the exception is due to invalid credentials, show "Wrong Password"
                                                    load.end()
                                                    Toast.makeText(this, "Wrong Password", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    // Handle other errors
                                                    load.end()
                                                    Toast.makeText(this, "Reauthentication failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                    }
                                }
                            } else {
                                // Error fetching sign-in methods for email
                                load.end()
                                Toast.makeText(this, "Error checking email availability: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }

        dialog.show()
    }

    private fun createNewAccount(newEmail: String, password: String, dialog: Dialog) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(newEmail, password)
            .addOnSuccessListener { authResult ->
                // User created successfully
                val user = authResult.user
                FirebaseAuth.getInstance().signInWithEmailAndPassword(newEmail, password)
                    .addOnSuccessListener { authResult ->
                        updateEmailInDatabase(newEmail,dialog)

                    }
                    .addOnFailureListener { exception ->
                        // Handle failure during sign-in
                        load.end()
                        Toast.makeText(this, "Error : ${exception.message}", Toast.LENGTH_SHORT).show()
                    }


            }
            .addOnFailureListener { exception ->
                // Handle failure during user creation
                Toast.makeText(this, "Error Updating Email: ${exception.message}", Toast.LENGTH_SHORT).show()
            }


    }

    private fun deleteEmailInAuth(newEmail: String, password:String, dialog: Dialog) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // Update email in Firebase Authentication
            user.delete()
                .addOnSuccessListener {
                    // Email Delete successfully in Firebase Authentication
                    createNewAccount(newEmail, password,dialog)
                }
                .addOnFailureListener { exception ->
                    load.end()
                    // Handle failure when updating email in Firebase Authentication
                    Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Handle case where the user is not signed in
            load.end()
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }
    }


    private fun updateEmailInDatabase(newEmail: String, dialog: Dialog) {
        val database = FirebaseDatabase.getInstance()
        val databaseRef = database.getReference("Management")

        val emailUpdate = hashMapOf<String, Any>(
            "name" to name,
            "email" to newEmail,
            "phone" to phone,
            "designation" to designation,
            "userType" to userType,
            "profilePic" to profilePic,
        )

        var key1 = email.substringBefore("@")
        var key2 = newEmail.substringBefore("@")

        databaseRef.child(key2).setValue(emailUpdate)
            .addOnSuccessListener {
                // Successfully updated email in Realtime Database
                databaseRef.child(key1).removeValue()
                    .addOnSuccessListener {
                        email = newEmail
                        binding.email.setText(email)
                        load.end()
                        Toast.makeText(this, "Email updated successfully", Toast.LENGTH_SHORT).show()

                        dialog.dismiss()
                    }
                    .addOnFailureListener { exception ->
                        // Handle failure
                        load.end()
                        Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { exception ->
                load.end()
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
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
                        callback(false, "Email Already Used.")
                    } else {
                        callback(true, "Email is available for registration.")
                    }
                } else {
                    callback(false, "Error checking email.")
                }
            }
    }

    fun validatePhone(phone: String): Boolean {
        val regex = "^(\\+8801[3-9]\\d{8})$".toRegex()
        return phone.matches(regex)
    }

    private fun makeChangePassword() {
        val oldPass = binding.oldpass.text.toString().trim()
        val newPass = binding.newpass.text.toString().trim()

        if (oldPass.isEmpty() || newPass.isEmpty()) {
            Toast.makeText(this, "Enter both old and new passwords", Toast.LENGTH_SHORT).show()
            return
        }

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val email = user.email
        if (email.isNullOrEmpty()) {
            Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show()
            return
        }

        val credential = EmailAuthProvider.getCredential(email, oldPass)

        // Reauthenticate the user before changing the password
        user.reauthenticate(credential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                // Change the password
                user.updatePassword(newPass).addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        binding.oldpass.setText("")
                        binding.newpass.setText("")
                        Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Incorrect Old Password ", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun setUserDetails() {
        var sharedPreferences = getSharedPreferences("HallMatePreferences", MODE_PRIVATE)
        name = sharedPreferences.getString("name","").toString()
        designation = sharedPreferences.getString("designation","").toString()
        phone = sharedPreferences.getString("phone","").toString()
        email = sharedPreferences.getString("email","").toString()
        password = sharedPreferences.getString("password","").toString()
        profilePic = sharedPreferences.getString("profilePic","").toString()
        userType = sharedPreferences.getString("userType","").toString()


        binding.name.setText(name)
        binding.designation.setText(designation)
        binding.phone.setText(phone)
        binding.email.setText(email)

        if(profilePic==""){
            binding.pic.setImageResource(R.drawable.profile)
        }else{
            ControlImage(this, this.activityResultRegistry,"dsasdfd")
                .setImageByURl(profilePic, binding.pic)
        }








    }


    private fun showDetailsLayout() {
        binding.details.setBackgroundColor(ContextCompat.getColor(this, R.color.base_color))
        binding.details.setTextColor(ContextCompat.getColor(this, R.color.white))
        binding.password.setBackgroundColor(ContextCompat.getColor(this, R.color.temp_color))
        binding.password.setTextColor(ContextCompat.getColor(this, R.color.black))

        binding.mainLayout.visibility = View.VISIBLE
        binding.passwordLayout.visibility = View.GONE
    }

    private fun showPasswordLayout() {
        binding.password.setBackgroundColor(ContextCompat.getColor(this, R.color.base_color))
        binding.password.setTextColor(ContextCompat.getColor(this, R.color.white))
        binding.details.setBackgroundColor(ContextCompat.getColor(this, R.color.temp_color))
        binding.details.setTextColor(ContextCompat.getColor(this, R.color.black))

        binding.mainLayout.visibility = View.GONE
        binding.passwordLayout.visibility = View.VISIBLE
    }

    override fun onDialogDismissed(message: String) {
        TODO("Not yet implemented")
    }
}