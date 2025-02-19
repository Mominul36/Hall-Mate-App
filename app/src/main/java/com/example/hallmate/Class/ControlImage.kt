package com.example.hallmate.Class

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class ControlImage(
    private val context: Context,
    private val activityResultRegistry: ActivityResultRegistry,
    private val key: String
) {
    var imageUri: Uri? = null
    private var imageUrl: String = ""
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    fun setImageView(imageView: ImageView) {
        imagePickerLauncher = activityResultRegistry.register(key, ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageUri = result.data?.data
                imageView.setImageURI(imageUri)
            }
        }
    }

    fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
        imagePickerLauncher.launch(intent)
    }


    fun setImageByURl(imageURL: String, imageView: ImageView) {
        Glide.with(context).load(imageURL).into(imageView)
    }

    fun uploadImageToFirebaseStorage(onComplete: (Boolean, String) -> Unit) {
        imageUri?.let { uri ->
            uploadImageToFirebase(uri, onComplete)
        } ?: onComplete(false, "Please select an image first")
    }

    private fun uploadImageToFirebase(fileUri: Uri, onComplete: (Boolean, String) -> Unit) {
        val storageReference = FirebaseStorage.getInstance().reference
        val fileName = "images/${UUID.randomUUID()}.jpg"
        val fileRef = storageReference.child(fileName)

        fileRef.putFile(fileUri)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    imageUrl = uri.toString()
                    onComplete(true, imageUrl)
                }
            }
            .addOnFailureListener { e ->
                onComplete(false, "Failed to upload image: ${e.message}")
            }
    }



    fun deleteImageFromFirebaseStorage(imageUrl: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
        storageReference.delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}
