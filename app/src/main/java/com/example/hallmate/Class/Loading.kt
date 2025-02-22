package com.example.hallmate.Class

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.example.hallmate.R

class Loading(
    private val context: Context
) {

    val dialog = Dialog(context)

    fun start(){
        dialog.setContentView(R.layout.dialog_loading)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }


    fun end(){
        dialog.dismiss()
    }
}