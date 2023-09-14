package studio.hcmc.reminisce.ui.activity.writer

import android.app.Activity
import android.text.Editable
import android.view.LayoutInflater
import androidx.core.widget.addTextChangedListener
import studio.hcmc.reminisce.databinding.DialogSelectVisitedAtBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.util.text
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class WriteSelectVisitedAtDialog(
    activity: Activity,
    delegate: Delegate
) {
    interface Delegate {
        fun onSaveClick(value: String)
    }

    init {
        val viewBinding = DialogSelectVisitedAtBinding.inflate(LayoutInflater.from(activity))
        val dialog = BottomSheetDialog(activity, viewBinding)

        // simpleFormat 말고 그냥 format으로 변환
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ko", "KR"))
        var now = dateFormat.format(Date(System.currentTimeMillis()))
        viewBinding.dialogSelectVisitedAtField.placeholderText = now

        dialog.show()

        viewBinding.dialogSelectCancel.setOnClickListener {
            delegate.onSaveClick("")
            dialog.dismiss()
        }

        viewBinding.dialogSelectVisitedAtField.editText!!.addTextChangedListener {
            viewBinding.dialogSelectSave.isEnabled = validateDate(formatInputDate(viewBinding.dialogSelectVisitedAtField.text))
        }

        viewBinding.dialogSelectSave.setOnClickListener {
            delegate.onSaveClick(viewBinding.dialogSelectVisitedAtField.string)
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