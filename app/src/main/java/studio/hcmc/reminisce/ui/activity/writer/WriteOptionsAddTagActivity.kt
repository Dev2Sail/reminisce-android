package studio.hcmc.reminisce.ui.activity.writer

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityWriteOptionsAddTagBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.TagIO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.util.text
import studio.hcmc.reminisce.vo.tag.TagVO

class WriteOptionsAddTagActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityWriteOptionsAddTagBinding
    private lateinit var tags: List<TagVO>
    private val newTags = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityWriteOptionsAddTagBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
        prepareTags()
    }

    private fun initView() {
        viewBinding.writeOptionsAddTagAppbar.apply {
            appbarTitle.text = getText(R.string.write_options_add_tag_title)
            appbarActionButton1.isEnabled = false
            appbarBack.setOnClickListener { finish() }
            appbarActionButton1.setOnClickListener {
                Intent(this@WriteOptionsAddTagActivity, WriteActivity::class.java).apply {
                    putStringArrayListExtra("tags", newTags)
                    startActivity(this)
                }
            }
        }

        viewBinding.writeOptionsAddTagField.editText!!.setOnEditorActionListener { _, actionId, event ->
            val inputtedValue = viewBinding.writeOptionsAddTagField
            if (actionId == EditorInfo.IME_ACTION_DONE && inputtedValue.text.length <= 10 ||
                event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN &&
                viewBinding.writeOptionsAddTagField.text.length <= 10
                ) {
                if (!newTags.contains(inputtedValue.string)) {
                    addNewChip(inputtedValue.string)
                }

                return@setOnEditorActionListener true
            }

            false
        }
    }

    private fun prepareTags() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@WriteOptionsAddTagActivity)
        val userId = UserIO.getByEmail(user.email).id
        runCatching { TagIO.listByUserId(userId) }
            .onSuccess {
                tags = it
                for (item in it) {
                    withContext(Dispatchers.Main) { addSavedChip(item.body) }
                }
            }
            .onFailure {
                CommonError.debugError(it)
                CommonError.onDialog(this@WriteOptionsAddTagActivity)
            }
    }

    private fun addNewChip(value: String) {
        newTags.add(value)
        val field = viewBinding.writeOptionsAddTagNew
        val newChip = Chip(this).apply {
            text = value
            maxLines = 1
            isCloseIconVisible = true
            setCloseIconResource(R.drawable.round_close_12)
        }

        newChip.setOnCloseIconClickListener {
            field.removeView(it)
            newTags.remove(value)

            viewBinding.writeOptionsAddTagAppbar.apply {
                appbarActionButton1.isEnabled = field.childCount != 0
            }
        }

        field.addView(newChip)
        viewBinding.writeOptionsAddTagAppbar.apply {
            appbarActionButton1.isEnabled = field.childCount != 0
        }
    }

    private fun addSavedChip(value: String) {
        val chip = Chip(this).apply {
            text = value
            maxLines = 1
            isCheckable = true
            isCheckedIconVisible = true
            setCheckedIconResource(R.drawable.round_check_24)
        }

        chip.setOnCheckedChangeListener { _, _ ->
            if (!newTags.contains(value)) {
                newTags.add(value)
                viewBinding.writeOptionsAddTagAppbar.apply {
                    appbarActionButton1.isEnabled = newTags.isNotEmpty()
                }

            } else {
                newTags.remove(value)
                viewBinding.writeOptionsAddTagAppbar.apply {
                    appbarActionButton1.isEnabled = newTags.isNotEmpty()
                }
            }
        }

        viewBinding.writeOptionsAddTagOld.addView(chip)
    }
}