package com.example.hallmate.Class

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import com.example.hallmate.R

class Loading2(private val context: Context) {

    private val dialog = Dialog(context)

    fun start() {
        dialog.setContentView(R.layout.dialog_loading_with_logo)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val logoImage = dialog.findViewById<ImageView>(R.id.logoImg)

        // Rotate animation
        val rotate = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotate.duration = 1000  // Rotation speed (1 second per full rotation)
        rotate.repeatCount = Animation.INFINITE // Infinite rotation

        logoImage.startAnimation(rotate) // Start animation

        dialog.show()
    }

    fun end() {
        dialog.dismiss()
    }
}
