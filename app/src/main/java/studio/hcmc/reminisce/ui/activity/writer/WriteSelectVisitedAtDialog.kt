package studio.hcmc.reminisce.ui.activity.writer

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import androidx.core.widget.addTextChangedListener
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.DialogSelectVisitedAtBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.util.text
import java.sql.Date

class WriteSelectVisitedAtDialog(
    activity: Activity,
    delegate: Delegate
) {
    interface Delegate {
        fun onSaveClick(date: String)
    }
    private val viewBinding: DialogSelectVisitedAtBinding
    private var watcher: TextWatcher? = null
    init {
        viewBinding = DialogSelectVisitedAtBinding.inflate(LayoutInflater.from(activity))
        val dialog = BottomSheetDialog(activity, viewBinding)
        val now = Date(System.currentTimeMillis())
        viewBinding.dialogSelectVisitedAtEditText.hint = now.toString()
        watcher = viewBinding.dialogSelectVisitedAtField.editText!!.addTextChangedListener {
            dateInputFormatter(it!!)
            val input = viewBinding.dialogSelectVisitedAtField.string
            if (input.length == 10) {
                if (validateDate(input)) {
                    viewBinding.dialogSelectVisitedAtField.error = null
                    viewBinding.dialogSelectSave.isEnabled = true
                } else {
                    viewBinding.dialogSelectVisitedAtField.error = activity.getString(R.string.error_visited_at_miss)
                }
            }
        }
        viewBinding.dialogSelectCancel.setOnClickListener {
            delegate.onSaveClick(now.toString())
            dialog.dismiss()
        }
        viewBinding.dialogSelectSave.setOnClickListener {
            delegate.onSaveClick(viewBinding.dialogSelectVisitedAtField.string)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun dateInputFormatter(value: Editable){
        viewBinding.dialogSelectVisitedAtField.editText!!.removeTextChangedListener(watcher)
        val plain = value.toString().replace("-", "")
        if (plain.length < 5) {
            viewBinding.dialogSelectVisitedAtField.editText!!.setText(plain)
        } else if (plain.length < 7) {
            viewBinding.dialogSelectVisitedAtField.editText!!.setText(plain.substring(0, 4) + "-" + plain.substring(4, plain.length))
        } else {
            viewBinding.dialogSelectVisitedAtField.editText!!.setText(plain.substring(0, 4) + "-" + plain.substring(4, 6) + "-" + plain.substring(6, plain.length))
        }
        viewBinding.dialogSelectVisitedAtField.editText!!.setSelection(viewBinding.dialogSelectVisitedAtField.text.length)
        viewBinding.dialogSelectVisitedAtField.editText!!.addTextChangedListener(watcher)
    }

    private fun validateDate(value: String): Boolean {
        return try {
            // 얘가 value랑 동일해야 함
            value == Date.valueOf(value).toString()
        } catch (e: Throwable) {
            LocalLogger.e(e)
            false
        }
    }
}