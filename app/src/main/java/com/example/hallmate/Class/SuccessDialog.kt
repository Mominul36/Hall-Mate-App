package com.example.hallmate.Class

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import com.example.hallmate.R

interface DialogDismissListener {
    fun onDialogDismissed()
}

class SuccessDialog(
    private val context: Context,
    private val title: String,
    private val message: String,
    private val listener: DialogDismissListener
) {

    val dialog = Dialog(context)
    var bl:Boolean = true

    fun show() {
        dialog.setContentView(R.layout.dialog_success)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val txtTitle = dialog.findViewById<TextView>(R.id.title)
        val txtMessage = dialog.findViewById<TextView>(R.id.message)
        val okBtn = dialog.findViewById<Button>(R.id.okBtn)

        txtTitle.text = title
        txtMessage.text = message

        // Auto dismiss after 3 seconds and notify listener
        Handler(Looper.getMainLooper()).postDelayed({
            if(bl){
                dialog.dismiss()
                listener.onDialogDismissed()  // Notify when the dialog is dismissed
            }
        }, 3000)

        // Dismiss on button click and notify listener
        okBtn.setOnClickListener {
            bl = false
            dialog.dismiss()
            listener.onDialogDismissed()  // Notify when the dialog is dismissed
        }

        dialog.show()
    }
}
