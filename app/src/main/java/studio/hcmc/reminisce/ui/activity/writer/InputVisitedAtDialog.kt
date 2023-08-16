package studio.hcmc.reminisce.ui.activity.writer

import android.app.Activity
import android.text.Editable
import android.view.LayoutInflater
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout.END_ICON_CLEAR_TEXT
import studio.hcmc.reminisce.databinding.DialogInputVisitedAtBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.util.text
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class InputVisitedAtDialog(
    activity: Activity,
    delegate: Delegate
) {
    interface Delegate {
        fun onSaveClick(value: String)
    }

    init {
        val viewBinding = DialogInputVisitedAtBinding.inflate(LayoutInflater.from(activity))
        val dialog = BottomSheetDialog(activity, viewBinding)
        viewBinding.dialogInputTitle.text = "방문일 선택"

        dialog.show()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ko", "KR"))
        var now = dateFormat.format(Date(System.currentTimeMillis()))
        viewBinding.dialogInput.placeholderText = now
        viewBinding.dialogInput.endIconMode = END_ICON_CLEAR_TEXT

        viewBinding.dialogInputCancel.setOnClickListener {
            delegate.onSaveClick("")
            dialog.dismiss()
        }

        viewBinding.dialogInput.editText!!.addTextChangedListener {
            viewBinding.dialogInputSave.isEnabled = validateDate(formatInputDate(viewBinding.dialogInput.text))
        }

        viewBinding.dialogInputSave.setOnClickListener {
            delegate.onSaveClick(viewBinding.dialogInput.string)
            dialog.dismiss()
        }
    }

    private fun formatInputDate(value: Editable): String {
        if (value.length == 4 || value.length == 7) { value.append("-") }

        return value.toString()
    }

    private fun validateDate(value: String): Boolean {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd")
            val date = format.parse(value)
            val reverse = format.format(date)
            value == reverse
        } catch (e: Throwable) {
            false
        }
    }
}