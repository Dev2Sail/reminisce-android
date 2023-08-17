package studio.hcmc.reminisce.ui.activity.writer

import android.app.Activity
import android.view.LayoutInflater
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout.END_ICON_CLEAR_TEXT
import studio.hcmc.reminisce.databinding.DialogSelectEmojiBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog
import studio.hcmc.reminisce.util.isEmoji
import studio.hcmc.reminisce.util.string

class WriteSelectEmojiDialog(
    activity: Activity,
    delegate: Delegate
) {
    interface Delegate {
        fun onSaveClick(value: String)
    }

    init {
        val viewBinding = DialogSelectEmojiBinding.inflate(LayoutInflater.from(activity))
        val dialog = BottomSheetDialog(activity, viewBinding)

        viewBinding.dialogSelectEmojiTitle.text = "이모지 추가"
        viewBinding.dialogSelectEmojiField.placeholderText = "지도에 표시될 이모지"
        viewBinding.dialogSelectEmojiField.endIconMode = END_ICON_CLEAR_TEXT

        dialog.show()

        viewBinding.dialogSelectEmojiField.editText!!.addTextChangedListener {
            val emoji = viewBinding.dialogSelectEmojiField.string
            viewBinding.dialogSelectSave.isEnabled = emoji.isEmoji()
        }
        viewBinding.dialogSelectCancel.setOnClickListener {
            delegate.onSaveClick("")
            dialog.dismiss()
        }
        viewBinding.dialogSelectSave.setOnClickListener {
            delegate.onSaveClick(viewBinding.dialogSelectEmojiField.string)
            dialog.dismiss()
        }
    }
}