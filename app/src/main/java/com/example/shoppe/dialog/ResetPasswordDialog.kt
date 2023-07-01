package com.example.shoppe.dialog

import android.annotation.SuppressLint
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.shoppe.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

@SuppressLint("InflateParams")
fun Fragment.setupBottomSheetDialog(
    onSendClick: (String) -> Unit
) {
    val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
    val view = layoutInflater.inflate(R.layout.reset_password_dialog, null)
    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()

    val edEmail = view.findViewById<EditText>(R.id.ed_reset_password)
    val buttonSend = view.findViewById<Button>(R.id.button_send_reset_password)
    val buttonCancel = view.findViewById<Button>(R.id.button_cancel_reset_password)

    buttonSend.setOnClickListener {
        val email = edEmail.text.toString().trim()
        onSendClick(email)
        dialog.dismiss()
    }
    buttonCancel.setOnClickListener {
        dialog.dismiss()
    }
}