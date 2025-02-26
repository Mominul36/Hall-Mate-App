package com.example.hallmate.Fragments

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment
import androidx.cardview.widget.CardView
import com.example.hallmate.databinding.FragmentSHallCardBinding
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.layout.element.Image
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.method.PasswordTransformationMethod
import android.util.TypedValue
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import com.example.hallmate.Class.DialogDismissListener
import com.example.hallmate.Class.Loading
import com.example.hallmate.Class.SuccessDialog
import com.example.hallmate.R
import com.google.firebase.database.FirebaseDatabase
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.util.UUID

class SHallCardFragment : Fragment(), DialogDismissListener {


    lateinit var hallId :String
    lateinit var studentId :String
    lateinit var name :String
    lateinit var email:String
    lateinit var phone :String
    lateinit var department :String
    lateinit var batch :String
    lateinit var roomNo :String
    var isCommitteeMember : Boolean?=null
    lateinit var key :String
    lateinit var password :String
    var isMutton :Boolean = false
    var isLock :Boolean = false

    var flagPass = false
    lateinit var load:Loading
    lateinit var success: SuccessDialog

    private lateinit var binding: FragmentSHallCardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSHallCardBinding.inflate(inflater, container, false)

        load = Loading(requireContext())
        success = SuccessDialog(requireContext(),this)
        binding.hallCard.visibility = View.GONE
        binding.change.visibility = View.GONE
        binding.second.visibility = View.VISIBLE

        setUserDetails()
        setQrCode()

        binding.show.setOnClickListener{
            checkForPassword()
        }

        binding.passHiddenIcon.setImageResource(R.drawable.ic_hide)
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

        binding.change.setOnClickListener{
            showConfirmationDialog()
        }









        return binding.root
    }
    private fun showConfirmationDialog() {

        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_confirmation)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        val textTitle = dialog.findViewById<TextView>(R.id.title)
        val textMessage = dialog.findViewById<TextView>(R.id.message)
        val btnPositive = dialog.findViewById<TextView>(R.id.positiveBtn)
        val btnNegative = dialog.findViewById<TextView>(R.id.negativeBtn)

        textTitle.setText("Are you sure?")
        textMessage.setText("Your previous meal card will no longer work after this change.")
        btnPositive.setText("Change")

        btnNegative.setOnClickListener{
            dialog.dismiss()
        }
        btnPositive.setOnClickListener{
            dialog.dismiss()
            load.start()
            changeCard()
        }
        dialog.show()
    }


    private fun changeCard() {
        val newKey = UUID.randomUUID().toString() // Replace with actual student ID or get it dynamically

        val databaseRef = FirebaseDatabase.getInstance().getReference("Student").child(hallId)

        val updates = mapOf("key" to newKey)
        key = newKey

        databaseRef.updateChildren(updates)
            .addOnSuccessListener {
                setQrCode()
                load.end()
               // success.show("Success","Hall card updated successfully!",true,"")

                Toast.makeText(context, "Hall card updated successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                load.end()
                Toast.makeText(context, "Failed to update hall card: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun checkForPassword() {
      var pass = binding.password.text.toString()
        if(pass.isEmpty()){
            Toast.makeText(requireContext(),"Enter your password",Toast.LENGTH_SHORT).show()
            return
        }

        if(pass!=password){
            Toast.makeText(requireContext(),"Wrong Password",Toast.LENGTH_SHORT).show()
            return
        }

        binding.second.visibility = View.GONE
        binding.hallCard.visibility = View.VISIBLE
        binding.change.visibility = View.VISIBLE

    }


    private fun setQrCode() {
        val stringForQrCode = hallId + key  // Concatenating hallId and key

        // Convert 150dp to pixels
        val sizeInPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 150f, binding.qr.context.resources.displayMetrics
        ).toInt()

        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitMatrix: BitMatrix = barcodeEncoder.encode(stringForQrCode, BarcodeFormat.QR_CODE, sizeInPx, sizeInPx)

            // Convert BitMatrix to Bitmap (WITHOUT PADDING)
            val bitmap = createTrimmedBitmap(bitMatrix)

            binding.qr.setImageBitmap(bitmap) // Set QR Code in ImageView

        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    // Function to trim extra white space from QR Code
    private fun createTrimmedBitmap(matrix: BitMatrix): Bitmap {
        val width = matrix.width
        val height = matrix.height
        var minX = width
        var minY = height
        var maxX = 0
        var maxY = 0

        // Find QR Code boundaries (ignore extra white space)
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (matrix[x, y]) {
                    if (x < minX) minX = x
                    if (y < minY) minY = y
                    if (x > maxX) maxX = x
                    if (y > maxY) maxY = y
                }
            }
        }

        val newWidth = maxX - minX + 1
        val newHeight = maxY - minY + 1

        val trimmedBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)

        for (x in 0 until newWidth) {
            for (y in 0 until newHeight) {
                trimmedBitmap.setPixel(x, y, if (matrix[x + minX, y + minY]) Color.BLACK else Color.WHITE)
            }
        }

        return trimmedBitmap
    }




    private fun setUserDetails() {
        var sharedPreferences = requireContext().getSharedPreferences("HallMatePreferences", MODE_PRIVATE)

        hallId = sharedPreferences.getString("hallId", "").toString()
        studentId = sharedPreferences.getString("studentId", "").toString()
        name = sharedPreferences.getString("name", "").toString()

        roomNo = sharedPreferences.getString("roomNo", "").toString()
        key = sharedPreferences.getString("key", "").toString()
        password = sharedPreferences.getString("password", "").toString()

        binding.name.setText("Name:   "+name)
        binding.hallId.setText(hallId)
        binding.studentId.setText("S.Id:      "+studentId)
        binding.room.setText("R. No:   "+roomNo)


    }

    override fun onDialogDismissed(message: String) {
        TODO("Not yet implemented")
    }


}
