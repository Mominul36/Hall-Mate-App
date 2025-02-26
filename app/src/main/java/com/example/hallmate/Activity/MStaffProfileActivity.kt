package com.example.hallmate.Activity

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.hallmate.Class.DialogDismissListener
import com.example.hallmate.Class.Loading
import com.example.hallmate.Class.Loading2
import com.example.hallmate.Class.SuccessDialog
import com.example.hallmate.Model.Hall
import com.example.hallmate.Model.HallIdEmail
import com.example.hallmate.Model.Meal
import com.example.hallmate.Model.Mutton
import com.example.hallmate.Model.Student
import com.example.hallmate.R
import com.example.hallmate.databinding.ActivityMstaffProfileBinding
import com.example.hallmate.databinding.ActivityMstudentProfileBinding
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.UUID

class MStaffProfileActivity : AppCompatActivity(), DialogDismissListener {
    lateinit var binding : ActivityMstaffProfileBinding
    lateinit var auth: FirebaseAuth
    private var database = FirebaseDatabase.getInstance()
    lateinit var load :Loading
    lateinit var successDialog: SuccessDialog

    lateinit var hallId :String
    lateinit var name :String
    lateinit var department :String

    lateinit var key :String
    var isMutton :Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMstaffProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        load = Loading(this)
        successDialog = SuccessDialog(this, this)

        getAllData()
        setAllData()


        binding.back.setOnClickListener{
            finish()
        }



        binding.save.setOnClickListener{
            load.start()
            updateStudent()
        }

        binding.delete.setOnClickListener{
            showConfirmationDialog2()
        }













        binding.name.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.name.setBackgroundResource(R.drawable.edit_focus_background)
                binding.name.setPadding(20,10,20,10)
            } else {
                binding.name.setBackgroundResource(R.drawable.edit_nonfocus_background)
                binding.name.setPadding(0,0,0,0)
            }
        }

        binding.position.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.position.setBackgroundResource(R.drawable.edit_focus_background)
                binding.position.setPadding(20,10,20,10)
            } else {
                binding.position.setBackgroundResource(R.drawable.edit_nonfocus_background)
                binding.position.setPadding(0,0,0,0)
            }
        }











    }

    private fun updateStudent() {
        val studentRef = database.getReference("Student")

        // Prepare updated fields
        val updates = mutableMapOf<String, Any?>()
        updates["name"] = binding.name.text.toString()
        updates["department"] = binding.position.text.toString()
        updates["isMutton"] = binding.mutton.isChecked

        studentRef.child(hallId).updateChildren(updates).addOnSuccessListener {

            if(isMutton==true && binding.mutton.isChecked==false){
                deleteFromMutton()

            }else if(isMutton==false && binding.mutton.isChecked==true){
                saveToMutton()
            }

            load.end()
            successDialog.show("Updated","Successfully updated student data.",true,"")
        }.addOnFailureListener { error ->
            load.end()
            Toast.makeText(this, "Failed to update: ${error.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveToMutton() {
        val muttonRef = FirebaseDatabase.getInstance().getReference("Mutton")

        // Check if hallId already exists in Mutton table
        muttonRef.child(hallId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    // hallId does not exist, so add it
                    val mutton = Mutton(hallId)
                    muttonRef.child(hallId).setValue(mutton)
                        .addOnSuccessListener {
                          //  Toast.makeText(this@MStaffProfileActivity, "Added to Mutton successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this@MStaffProfileActivity, "Failed to add to Mutton: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                   // Toast.makeText(this@MStaffProfileActivity, "Hall ID already exists in Mutton", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MStaffProfileActivity, "Error checking Mutton table: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteFromMutton() {
        val muttonRef = FirebaseDatabase.getInstance().getReference("Mutton")

        // Check if hallId exists before deleting
        muttonRef.child(hallId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // hallId exists, so delete it
                    muttonRef.child(hallId).removeValue()
                        .addOnSuccessListener {
                           // Toast.makeText(this@MStaffProfileActivity, "Deleted from Mutton successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this@MStaffProfileActivity, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                   // Toast.makeText(this@MStaffProfileActivity, "Hall ID does not exist in Mutton", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MStaffProfileActivity, "Error checking Mutton table: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



    private fun showConfirmationDialog2() {

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_confirmation)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        val textTitle = dialog.findViewById<TextView>(R.id.title)
        val textMessage = dialog.findViewById<TextView>(R.id.message)
        val btnPositive = dialog.findViewById<TextView>(R.id.positiveBtn)
        val btnNegative = dialog.findViewById<TextView>(R.id.negativeBtn)

        textTitle.setText("Delete Staff?")
        textMessage.setText("Are you sure? This cannot be undone.")
        btnPositive.setText("Delete")

        btnNegative.setOnClickListener{
            dialog.dismiss()
        }
        btnPositive.setOnClickListener{
            dialog.dismiss()
            load.start()
            deleteStudent()
        }
        dialog.show()
    }

    private fun deleteStudent() {
                    val studentRef = FirebaseDatabase.getInstance().getReference("Student")
                    val hallIdEmailRef = FirebaseDatabase.getInstance().getReference("HallIdEmail")

                    studentRef.child(hallId).removeValue().addOnSuccessListener {
                        // After deleting from the Student table, try deleting from HallIdEmail
                        hallIdEmailRef.child(hallId).removeValue().addOnSuccessListener {
                            // After deleting from HallIdEmail table, check the Mutton table
                            val muttonRef = FirebaseDatabase.getInstance().getReference("Mutton")
                            muttonRef.child(hallId).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(muttonSnapshot: DataSnapshot) {
                                    if (muttonSnapshot.exists()) {
                                        // Step 3: Delete from Mutton table if it exists
                                        muttonRef.child(hallId).removeValue().addOnSuccessListener {
                                            // All deletions successful
                                            load.end()
                                            successDialog.show("Deleted","Student deleted successfully",true,"")
                                        }.addOnFailureListener {
                                            // Failed to delete from Mutton table, still proceed with other deletions
                                            load.end()
                                            successDialog.show("Deleted","Student deleted successfully",true,"")
                                        }
                                    } else {
                                        // If Mutton table doesn't exist for this hallId, skip deletion in Mutton
                                        load.end()
                                        successDialog.show("Deleted","Student deleted successfully",true,"")
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    load.end()
                                    // Handle error in Mutton table check
                                    Toast.makeText(this@MStaffProfileActivity, "Failed to check Mutton table for hallId.", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }.addOnFailureListener {
                            load.end()
                            // Failed to delete from HallIdEmail table, show error message
                            Toast.makeText(this@MStaffProfileActivity, "Failed to delete from HallIdEmail table.", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        load.end()
                        // Failed to delete from Student table, show error message
                        Toast.makeText(this@MStaffProfileActivity, "Failed to delete from Student table.", Toast.LENGTH_SHORT).show()
                    }

    }//////



    override fun onDialogDismissed(ins:String) {
        finish()
    }



    private fun setAllData() {
        binding.name.setText(name)
        binding.hallId.setText("Id: "+hallId)
        binding.position.setText(department)
        binding.mutton.isChecked = isMutton

    }

    private fun getAllData() {
        hallId = intent.getStringExtra("hallId").toString()
        name = intent.getStringExtra("name").toString()
        department = intent.getStringExtra("department").toString()
        key = intent.getStringExtra("key").toString()
        isMutton = intent.getBooleanExtra("isMutton",false)




    }
}