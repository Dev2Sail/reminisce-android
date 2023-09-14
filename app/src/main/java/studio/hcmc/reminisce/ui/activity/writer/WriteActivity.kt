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
import studio.hcmc.reminisce.dto.location.LocationDTO
import studio.hcmc.reminisce.dto.tag.TagDTO
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.TagIO
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class WriteActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityWriteBinding
    private val currentCategoryId by lazy { intent.getIntExtra("categoryId", -1) }
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
                // contents post
                // location Post.dto
                // location insert 성공 후 write_options intent
                Intent(this@WriteActivity, WriteOptionsActivity::class.java).apply {
                    // locationId, currentCategoryId
                    startActivity(this)
                }

            }
        }

        viewBinding.writeVisitedAt.rootView.setOnClickListener {
            WriteSelectVisitedAtDialog(this@WriteActivity, visitedAtDelegate)
        }

        viewBinding.writeMarkerEmoji.rootView.setOnClickListener {
            WriteSelectEmojiDialog(this@WriteActivity, emojiDelegate)
        }

//        viewBinding.writeFriendTag.apply {

//            root.setOnClickListener {
//                Intent(this@WriteActivity, WriteSelectFriendActivity::class.java).apply {
//                    startActivity(this)
//                }
//            }
//            val friendNicknameList = intent.getStringArrayListExtra("selectedFriendNicknameList")
//            val friendIdList = intent.getIntegerArrayListExtra("selectedFriendIdList")
//            val friendNicknames = StringBuilder()
//
//            if (!friendNicknameList.isNullOrEmpty()) {
//                for (s in 0 until friendNicknameList.size) {
//                    friendNicknames.append(friendNicknameList[s])
//                    if (s <= friendNicknameList.size - 2) {
//                        friendNicknames.append(", ")
//                    }
//                }
//                writeOptionsItemBody.text = friendNicknames.toString()
//            }
//            if (!friendIdList.isNullOrEmpty()) {
//                writeOptions["selectedFriendIds"] = friendIdList
//            }
//        }
//
//        viewBinding.writeTag.apply {
//            writeOptionsItemBody.text = getText(R.string.write_hashtag)
//            writeOptionsItemIcon.setImageResource(R.drawable.round_tag_16)
//            root.setOnClickListener {
//                Intent(this@WriteActivity, WriteOptionsAddTagActivity::class.java).apply {
//                    startActivity(this)
//                }
//            }
//            val tagList = intent.getStringArrayListExtra("tags")
//            val tagBuilder = StringBuilder()
//            if (!tagList.isNullOrEmpty()) {
//                writeOptions["tags"] = tagList
//                for (tag in tagList) {
//                    tagBuilder.append("#")
//                    tagBuilder.append(tag)
//                    tagBuilder.append(" ")
//                }
//                writeOptionsItemBody.text = tagBuilder.toString()
//            }
//        }
//
//        viewBinding.writeCategory.apply {
//            writeOptionsItemBody.text = "카테고리 선택"
//            writeOptionsItemIcon.setImageResource(R.drawable.outline_folder_16)
//
//        }
    }

    private suspend fun postContents() = coroutineScope {
        val postDTO = LocationDTO.Post().apply {
            categoryId = currentCategoryId
            visitedAt = writeOptions["visitedAt"].toString()
            markerEmoji = writeOptions["emoji"].toString()

        }
        val result = runCatching {

            async {  }


        }
    }


    private fun postTags(value: String) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@WriteActivity)
        val postDTO = TagDTO.Post().apply {
            userId = user.id
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
