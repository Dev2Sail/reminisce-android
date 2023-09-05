package studio.hcmc.reminisce.ui.activity.writer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityWriteBinding
import studio.hcmc.reminisce.dto.tag.TagDTO
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.TagIO
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class WriteActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityWriteBinding
    private val categoryId by lazy { intent.getIntExtra("categoryId", -1) }
    private val writeOptions = HashMap<String, Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityWriteBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        viewBinding.writeAppbar.apply {
            appbarTitle.text = ""
            appbarBack.setOnClickListener { finish() }
            appbarActionButton1.setOnClickListener {

            }
        }

        viewBinding.writeVisitedAt.apply {
            writeOptionsItemBody.text = getText(R.string.write_visited_at)
            root.setOnClickListener {
                WriteSelectVisitedAtDialog(this@WriteActivity, visitedAtDelegate)
            }
        }

        viewBinding.writeMarkerEmoji.apply {
            writeOptionsItemBody.text = getText(R.string.write_emoji)
            writeOptionsItemIcon.setImageResource(R.drawable.outline_add_reaction_16)
            root.setOnClickListener {
                WriteSelectEmojiDialog(this@WriteActivity, emojiDelegate)
            }
        }

        viewBinding.writeLocation.apply {
            writeOptionsItemBody.text = getText(R.string.write_location)
            writeOptionsItemIcon.setImageResource(R.drawable.outline_add_location_alt_16)
        }

        viewBinding.writeFriendTag.apply {
            writeOptionsItemBody.text = getText(R.string.write_friend)
            writeOptionsItemIcon.setImageResource(R.drawable.outline_group_add_16)
            root.setOnClickListener {
                Intent(this@WriteActivity, WriteSelectFriendActivity::class.java).apply {
                    startActivity(this)
                }
            }
        }

        viewBinding.writeTag.apply {
            writeOptionsItemBody.text = getText(R.string.write_hashtag)
            writeOptionsItemIcon.setImageResource(R.drawable.round_tag_16)
            root.setOnClickListener {
                Intent(this@WriteActivity, WriteOptionsAddTagActivity::class.java).apply {
                    startActivity(this)
                }
            }
            val tagList = intent.getStringArrayListExtra("tags")
            val tagBuilder = StringBuilder()
            if (tagList != null) {
                for (tag in tagList) {
                    tagBuilder.append("#")
                    tagBuilder.append(tag)
                    tagBuilder.append(" ")
                }
                writeOptionsItemBody.text = tagBuilder.toString()
            }
        }
    }

    private suspend fun postContents() = coroutineScope {
        val result = runCatching {

            async {  }


        }
    }

    private fun postTags(value: String) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@WriteActivity).id
        val postDTO = TagDTO.Post().apply {
            userId = user
            body = value
        }
        runCatching { TagIO.post(postDTO) }
            .onSuccess {  }
    }

    private val visitedAtDelegate = object : WriteSelectVisitedAtDialog.Delegate {
        // use java sql date
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ko", "KR"))
        var currentDate: String = dateFormat.format(Date(System.currentTimeMillis()))

        override fun onSaveClick(value: String) {
            if (value.isNotEmpty()) {
                writeOptions["visitedAt"] = value
                viewBinding.writeVisitedAt.writeOptionsItemBody.text = value
            } else {
                writeOptions["visitedAt"] = currentDate
                viewBinding.writeVisitedAt.writeOptionsItemBody.text = currentDate
            }
        }
    }

    private val emojiDelegate = object :WriteSelectEmojiDialog.Delegate {
        override fun onSaveClick(value: String) {
            writeOptions["emoji"] = value
            viewBinding.writeMarkerEmoji.writeOptionsItemBody.text = value
        }
    }
}
/*
userId, locationId, tagId
tagService add
locationService add
location_tag add 한 트랜잭셩~~~
 */

