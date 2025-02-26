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
    fun onDialogDismissed(message: String)

}

class SuccessDialog(
    private val context: Context,
    private val listener: DialogDismissListener
) {

    val dialog = Dialog(context)
    var isAutoCancel:Boolean = true
    var ins = ""

    fun show(title: String, message: String,autoCancel :Boolean,ins:String) {

        isAutoCancel = autoCancel
        dialog.setContentView(R.layout.dialog_success)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        val txtTitle = dialog.findViewById<TextView>(R.id.title)
        val txtMessage = dialog.findViewById<TextView>(R.id.message)
        val okBtn = dialog.findViewById<Button>(R.id.okBtn)

        txtTitle.text = title
        txtMessage.text = message

        // Auto dismiss after 3 seconds and notify listener
        Handler(Looper.getMainLooper()).postDelayed({
            if(isAutoCancel){
                dialog.dismiss()
                listener.onDialogDismissed(ins)  // Notify when the dialog is dismissed
            }
        }, 3000)

        okBtn.setOnClickListener {
            isAutoCancel = false
            dialog.dismiss()
            listener.onDialogDismissed(ins)  // Notify when the dialog is dismissed
        }

        dialog.show()
    }
}
