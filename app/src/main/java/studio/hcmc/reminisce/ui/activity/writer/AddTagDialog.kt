package studio.hcmc.reminisce.ui.activity.writer

import android.app.Activity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import com.google.android.material.chip.Chip
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.DialogAddTagBinding
import studio.hcmc.reminisce.ui.view.BottomSheetDialog
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.util.text
import studio.hcmc.reminisce.vo.tag.TagVO

class AddTagDialog(
    activity: Activity,
    delegate: Delegate
) {
    private val viewBinding by lazy { DialogAddTagBinding.inflate(LayoutInflater.from(activity)) }
    private val tagList = ArrayList<String>()
    private val savedTagList = ArrayList<String>()

    interface Delegate {
        val dbTagList: List<TagVO>

        fun onSaveClick(content: ArrayList<String>)
    }

    init {
        val dialog = BottomSheetDialog(activity, viewBinding)

        viewBinding.dialogAddTagInputContainer.editText!!.setOnEditorActionListener { _, actionId, event ->
            val inputtedTag = viewBinding.dialogAddTagInputContainer
            if (inputtedTag.text.length <= 10 && (actionId == EditorInfo.IME_ACTION_DONE ||
                event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                addChip(inputtedTag.string)
                tagList.add(inputtedTag.string)

                return@setOnEditorActionListener true
            }
            false
        }
        dialog.show()

        viewBinding.dialogAddTagSave.setOnClickListener {
            // WriterSettingActivity로 intent
            dialog.dismiss()
            delegate.onSaveClick(tagList)
        }
        viewBinding.dialogAddTagCancel.setOnClickListener {
            dialog.dismiss()
        }

        savedTagList.add("tag 2")
        savedTagList.add("tag 2")
        savedTagList.add("tag 2")
        savedTagList.add("tag 2")
        savedTagList.add("tag 2")
        savedTagList.add("tag 2")
        savedTagList.add("tag 2")
        savedTagList.add("tag 2")
        savedTagList.add("tag 2")
        savedTagList.add("tag 2")
        savedTagList.add("tag 2")

        if (savedTagList.isNotEmpty()) {
            for (item in savedTagList) {
                addSavedChip(item)
            }
        } else {
            viewBinding.dialogAddSavedTagContainer.isVisible = false
        }
    }

    private fun addChip(value: String) {
        val newChip = Chip(viewBinding.root.context)
        newChip.text = value
        newChip.maxLines = 1
        newChip.isCloseIconVisible = true
        newChip.setCloseIconResource(R.drawable.round_close_12)
        newChip.setOnCloseIconClickListener {
            viewBinding.dialogAddInputtedTags.removeView(it)
            tagList.remove(value)
            viewBinding.dialogAddTagSave.isEnabled = viewBinding.dialogAddInputtedTags.childCount != 0
        }

        viewBinding.dialogAddInputtedTags.addView(newChip)
        viewBinding.dialogAddTagSave.isEnabled = viewBinding.dialogAddInputtedTags.childCount != 0
    }

    private fun addSavedChip(value: String) {
        val savedChip = Chip(viewBinding.root.context)
        savedChip.text = value
        savedChip.maxLines = 1
        savedChip.isCheckable = true
        savedChip.isCheckedIconVisible = true

        savedChip.setOnCheckedChangeListener { _, _ ->
            if (!tagList.contains(value)) {
                tagList.add(value)
                addChip(value)
            } else {
                tagList.remove(value)
            }
        }

        viewBinding.dialogAddSavedTags.addView(savedChip)
    }
}