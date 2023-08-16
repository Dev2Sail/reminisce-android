package studio.hcmc.reminisce.ui.activity.writer

import android.app.Activity
import android.view.LayoutInflater
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import studio.hcmc.reminisce.databinding.DialogInputEmojiBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog
import studio.hcmc.reminisce.util.isEmoji
import studio.hcmc.reminisce.util.string

class InputEmojiDialog(
    activity: Activity,
    delegate: Delegate
) {
    interface Delegate {
        fun onSaveClick(value: String)
    }

    init {
        val viewBinding = DialogInputEmojiBinding.inflate(LayoutInflater.from(activity))
        val dialog = BottomSheetDialog(activity, viewBinding)
        viewBinding.dialogInputTitle.text = "이모지 추가"
        viewBinding.dialogInput.placeholderText = "지도에 표시될 이모티콘"
        viewBinding.dialogInput.endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT

        dialog.show()

        viewBinding.dialogInputCancel.setOnClickListener {
            delegate.onSaveClick("")
            dialog.dismiss()
        }
        viewBinding.dialogInput.editText!!.addTextChangedListener {
            val emoji = viewBinding.dialogInput.string
            viewBinding.dialogInputSave.isEnabled = emoji.isEmoji()
        }
        viewBinding.dialogInputSave.setOnClickListener {
            delegate.onSaveClick(viewBinding.dialogInput.string)
            dialog.dismiss()
        }
    }
}