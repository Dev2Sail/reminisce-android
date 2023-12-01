package studio.hcmc.reminisce.ui.activity.writer.options

import android.app.Activity
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
import studio.hcmc.reminisce.dto.tag.TagDTO
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.LocationTagIO
import studio.hcmc.reminisce.io.ktor_client.TagIO
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.setActivity
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.util.text
import studio.hcmc.reminisce.vo.tag.TagVO

class WriteOptionTagActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityWriteOptionsAddTagBinding
    private lateinit var tags: List<TagVO>

    private val locationId by lazy { intent.getIntExtra("locationId", -1) }

    // 저장할 태그 body
    private val postTags = ArrayList<String>()
    // 저장돼있던 태그 중 해당 location에 저장할 tagId
    private val isChecked = HashSet<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityWriteOptionsAddTagBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
        prepareTags()
    }

    // TODO 기존에 저장된 태그들 조회해서 newChip에 붙이기


    private fun initView() {
        viewBinding.writeOptionsAddTagAppbar.apply {
            appbarTitle.text = getText(R.string.write_options_add_tag_title)
            appbarActionButton1.isEnabled = false
            appbarBack.setOnClickListener { finish() }
            appbarActionButton1.setOnClickListener {
                LocalLogger.v("tag body : $postTags")
//                LocalLogger.v("dto : ${preparePostTags(30).body}")
                postContents()
            }
        }

        viewBinding.writeOptionsAddTagField.editText!!.setOnEditorActionListener { _, actionId, event ->
            val inputtedValue = viewBinding.writeOptionsAddTagField
            if (actionId == EditorInfo.IME_ACTION_DONE && inputtedValue.text.length <= 10 ||
                event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN &&
                viewBinding.writeOptionsAddTagField.text.length <= 10
                ) {
                if (!postTags.contains(inputtedValue.string)) {
                    addNewChip(inputtedValue.string)
                }

                return@setOnEditorActionListener true
            }

            false
        }
    }

    private fun prepareTags() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@WriteOptionTagActivity)
        runCatching { TagIO.listByUserId(user.id) }
            .onSuccess {
                tags = it
                for (item in it) {
                    withContext(Dispatchers.Main) { addSavedChip(item.id, item.body) }
                }
            }.onFailure {
                LocalLogger.e(it)
                onError()
            }
    }

    private fun onError() {
        CommonError.onMessageDialog(this, getString(R.string.dialog_error_tag_load))
    }

    private fun preparePostTags(userId: Int): TagDTO.Post {
        for (tag in tags) {
            for (selectedId in isChecked) {
                if (tag.id == selectedId) {
                    postTags.add(tag.body)
                }
            }
        }

        val dto = TagDTO.Post().apply {
            this.userId = userId
            this.body = this@WriteOptionTagActivity.postTags
        }
        return dto
    }

    private fun postContents() = CoroutineScope(Dispatchers.IO).launch {
        val user  = UserExtension.getUser(this@WriteOptionTagActivity)
        val dto = preparePostTags(user.id)
        runCatching { LocationTagIO.post(locationId, dto) }
            .onSuccess { toOptions() }
            .onFailure { LocalLogger.e(it) }
    }

    private fun toOptions() {
        Intent()
            .putExtra("isAdded", true)
            .putExtra("body", postTags.joinToString { it })
            .setActivity(this, Activity.RESULT_OK)
        finish()
    }

    private fun addNewChip(value: String) {
        postTags.add(value)
        val field = viewBinding.writeOptionsAddTagNew
        val newChip = Chip(this).apply {
            text = value
            maxLines = 1
            isCloseIconVisible = true
            setCloseIconResource(R.drawable.round_close_12)
        }
        newChip.setOnCloseIconClickListener {
            field.removeView(it)
            postTags.remove(value)

            viewBinding.writeOptionsAddTagAppbar.apply {
                appbarActionButton1.isEnabled = field.childCount != 0
            }
        }

        field.addView(newChip)
        viewBinding.writeOptionsAddTagAppbar.appbarActionButton1.isEnabled = field.childCount != 0
    }

    private fun addSavedChip(id: Int, value: String) {
        val chip = Chip(this).apply {
            text = value
            maxLines = 1
            isCheckable = true
            isCheckedIconVisible = true
            setCheckedIconResource(R.drawable.round_check_24)
        }

        chip.setOnCheckedChangeListener { _, _ ->
            if (!isChecked.add(id)) {
                isChecked.remove(id)
            }

            viewBinding.writeOptionsAddTagAppbar.appbarActionButton1.isEnabled = postTags.isNotEmpty() || isChecked.isNotEmpty()
        }

        viewBinding.writeOptionsAddTagOld.addView(chip)
    }
}