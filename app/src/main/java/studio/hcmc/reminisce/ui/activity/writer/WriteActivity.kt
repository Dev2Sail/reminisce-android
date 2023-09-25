package studio.hcmc.reminisce.ui.activity.writer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
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

                /*
                write : 사용자가 입력한 장소의 좌표 저장
                read : db의 좌표를 주소 변환 후 제공

                 */


                Intent(this@WriteActivity, WriteOptionsActivity::class.java).apply {
                    // locationId, currentCategoryId
                    putExtra("currentCategoryId", currentCategoryId)
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
    }

    private fun postContents() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@WriteActivity)
        val postDTO = LocationDTO.Post().apply {
            categoryId = currentCategoryId
            visitedAt = writeOptions["visitedAt"].toString()
            markerEmoji = writeOptions["emoji"].toString()
            body = viewBinding.writeTextContainer.editText!!.toString() // editText!!.toString()이 isEmpty하면 "" 삽입
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
                viewBinding.writeVisitedAt.text = value
            } else {
                writeOptions["visitedAt"] = currentDate
                viewBinding.writeVisitedAt.text = currentDate
            }
        }
    }

    private val emojiDelegate = object :WriteSelectEmojiDialog.Delegate {
        override fun onSaveClick(value: String) {
            writeOptions["emoji"] = value
            viewBinding.writeMarkerEmoji.text = value
        }
    }
}
