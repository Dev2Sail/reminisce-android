package studio.hcmc.reminisce.ui.activity.writer

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityWriteOptionsAddTagBinding
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.util.text

class WriteOptionsAddTagActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityWriteOptionsAddTagBinding
    private val newTagList = ArrayList<String>()
    private val oldTagList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityWriteOptionsAddTagBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val appBar = viewBinding.writeOptionsAddTagAppbar
        appBar.appbarTitle.text = "해시태그 추가"
        appBar.appbarActionButton1.isEnabled = false
        appBar.appbarBack.setOnClickListener {
            finish()
        }
        appBar.appbarActionButton1.setOnClickListener {
            Intent(this, WriteActivity::class.java).apply {
                startActivity(this)
            }
        }

        viewBinding.writeOptionsAddTagField.editText!!.setOnEditorActionListener { _, actionId, event ->
            val inputtedValue = viewBinding.writeOptionsAddTagField.string
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN &&
                (viewBinding.writeOptionsAddTagField.text.length <= 10)) {
                addNewChip(inputtedValue)
                newTagList.add(inputtedValue)

                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun addNewChip(value: String) {
        val field = viewBinding.writeOptionsAddTagNew
        val newChip = Chip(this)
        newChip.text = value
        newChip.maxLines = 1
        newChip.isCloseIconVisible = true
        newChip.setCloseIconResource(R.drawable.round_close_12)
        newChip.setOnCloseIconClickListener {
            field.removeView(it)
            newTagList.remove(value)
            viewBinding.writeOptionsAddTagAppbar.appbarActionButton1.isEnabled = field.childCount != 0
        }
        field.addView(newChip)
        viewBinding.writeOptionsAddTagAppbar.appbarActionButton1.isEnabled = field.childCount != 0
    }

    private fun addOldChip(value: String) {
        val oldChip = Chip(this)
        oldChip.text = value
        oldChip.maxLines = 1
        oldChip.isCheckable = true
        oldChip.isCheckedIconVisible = true
        oldChip.setOnCheckedChangeListener { _, _ ->
            if (!newTagList.contains(value)) {
                newTagList.add(value)
            } else {
                newTagList.remove(value)
            }
        }
        viewBinding.writeOptionsAddTagOld.addView(oldChip)
    }
}