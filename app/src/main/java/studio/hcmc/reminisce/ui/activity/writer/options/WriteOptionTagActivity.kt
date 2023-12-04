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
import studio.hcmc.reminisce.vo.user.UserVO

class WriteOptionTagActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityWriteOptionsAddTagBinding
    private lateinit var user: UserVO

    private val tags = ArrayList<TagVO>()
    private val savedTags = ArrayList<TagVO>()
    private val locationId by lazy { intent.getIntExtra("locationId", -1) }

    // 저장할 태그 body
    private val postTags = ArrayList<String>()
    // 저장돼있던 태그 중 해당 location에 저장할 tagId
    private val checkedTagIds = HashMap<Int /* tagId */, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityWriteOptionsAddTagBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
        CoroutineScope(Dispatchers.IO).launch { loadContent() }

    }

    private fun initView() {
        viewBinding.writeOptionsAddTagAppbar.appbarTitle.text = getText(R.string.write_options_add_tag_title)
        viewBinding.writeOptionsAddTagAppbar.appbarActionButton1.isEnabled = false
        viewBinding.writeOptionsAddTagAppbar.appbarBack.setOnClickListener { finish() }
        viewBinding.writeOptionsAddTagAppbar.appbarActionButton1.setOnClickListener { preparePatch() }
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

    private suspend fun prepareUser(): UserVO {
        if(!this::user.isInitialized) {
            user = UserExtension.getUser(this)
        }

        return user
    }

    private suspend fun loadContent() {
        val user = prepareUser()

        try {
            val fetched = TagIO.listByUserId(user.id)
            val saved = TagIO.listByLocationId(locationId)
            for (tag in fetched) {
                tags.add(tag)
                checkedTagIds[tag.id] = false
            }
            for (tag in saved) {
                checkedTagIds[tag.id] = true
                savedTags.add(tag)
            }
            for (tag in tags) {
                withContext(Dispatchers.Main) { addSavedChip(tag.id, tag.body) }
            }


        } catch (e: Throwable) {
            LocalLogger.e(e)
            withContext(Dispatchers.Main) { onError() }
        }
    }


    private fun onError() {
        CommonError.onMessageDialog(this, getString(R.string.dialog_error_tag_load))
    }

    private fun preparePostTags(userId: Int): TagDTO.Post {
        for (tag in checkedTagIds) {
            if (tag.value) {
                postTags.add(prepareBody(tag.key, tags))
            }
        }

        val dto = TagDTO.Post().apply {
            this.userId = userId
            this.body = this@WriteOptionTagActivity.postTags
        }
        return dto
    }

    private fun prepareDeleteTags(): List<Int> {
        val removeIds = ArrayList<Int>()
        for (id in checkedTagIds) {
            if (!id.value) {
                removeIds.add(findTagId(id.key, savedTags))
            }
        }

        return removeIds.filterNot { it == -1 }
    }

    private fun findTagId(tagId: Int, tags: List<TagVO>): Int {
        var tagIndex = -1
        for (tag in tags) {
            if (tag.id == tagId)
                tagIndex = tag.id
        }

        return tagIndex
    }

    private suspend fun deleteTag(tagId: Int) {
        LocationTagIO.delete(locationId, tagId)
    }

    private fun prepareBody(tagId: Int, tags: List<TagVO>): String {
        var name = ""
        for(tag in tags) {
            if (tag.id == tagId) {
                name = tag.body
                return name
            }
        }

        return name
    }

    private fun preparePatch() {
        val dto = preparePostTags(user.id)
        val removeIds = prepareDeleteTags()
        when {
            dto.body.isNotEmpty() && removeIds.isNotEmpty() -> {
                consumeRemoveId(removeIds)
                postContents(dto)
            }
            dto.body.isNotEmpty() && removeIds.isEmpty() -> postContents(dto)
            dto.body.isEmpty() && removeIds.isNotEmpty() -> {
                consumeRemoveId(removeIds)
                toOptions()
            }
        }
    }

    private fun consumeRemoveId(tagIds: List<Int>) {
        for (id in tagIds) {
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    deleteTag(id)
                }
            } catch (e: Throwable) {
                LocalLogger.e(e)
            }
        }
    }

    private fun postContents(dto: TagDTO.Post) = CoroutineScope(Dispatchers.IO).launch {
        runCatching { LocationTagIO.post(locationId, dto) }
            .onSuccess { toOptions() }
            .onFailure { LocalLogger.e(it) }
    }

    private fun toOptions() {
        Intent()
            .putExtra("isAdded", true)
            .putExtra("body", postTags.joinToString { it })
            .putExtra("locationId", locationId)
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
        val chip = Chip(this)
        chip.text = value
        chip.maxLines = 1
        chip.isCheckable = true
        chip.isCheckedIconVisible = true
        chip.setCheckedIconResource(R.drawable.round_check_24)
        chip.isChecked = this.checkedTagIds[id]!!

        chip.setOnCheckedChangeListener { _, _ ->
            if (!checkedTagIds[id]!!) {
                checkedTagIds[id] = true
            } else {
                checkedTagIds[id] = false
            }

            viewBinding.writeOptionsAddTagAppbar.appbarActionButton1.isEnabled = postTags.isNotEmpty() || checkedTagIds.isNotEmpty()
        }

        viewBinding.writeOptionsAddTagOld.addView(chip)
    }
}