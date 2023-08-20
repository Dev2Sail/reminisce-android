package studio.hcmc.reminisce.ui.activity.writer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityWriteBinding
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class WriteActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityWriteBinding
    private val nextIntent by lazy { Intent(this, WriteOptionsActivity::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityWriteBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val appBar = viewBinding.writeAppbar
        appBar.appbarBack.setOnClickListener { finish() }
        appBar.appbarActionButton1.setOnClickListener {
            startActivity(nextIntent)
        }

        val visitedAt = viewBinding.writeVisitedAt
        visitedAt.writeOptionsItemBody.text = "방문 날짜"
        visitedAt.writeOptionsItemIcon.setOnClickListener {
            WriteSelectVisitedAtDialog(this, visitedAtDelegate)
        }

        val marker = viewBinding.writeMarkerEmoji
        marker.writeOptionsItemIcon.setImageResource(R.drawable.outline_add_reaction_16)
        marker.writeOptionsItemBody.text = "이모지 추가"
        marker.writeOptionsItemIcon.setOnClickListener {
            WriteSelectEmojiDialog(this, emojiDelegate)
        }

        val location = viewBinding.writeLocation
        location.writeOptionsItemIcon.setImageResource(R.drawable.outline_add_location_alt_16)
        location.writeOptionsItemBody.text = "방문한 장소 추가"

        val friendTag = viewBinding.writeFriendTag
        friendTag.writeOptionsItemIcon.setImageResource(R.drawable.round_group_add_16)
        friendTag.writeOptionsItemBody.text = "함께 다녀온 친구 추가"

        val hashtag = viewBinding.writeTag
        hashtag.writeOptionsItemIcon.setImageResource(R.drawable.round_tag_16)
        hashtag.writeOptionsItemBody.text = "해시태그 추가"
        hashtag.writeOptionsItemIcon.setOnClickListener {
            Intent(this, WriteOptionsAddTagActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    private val visitedAtDelegate = object : WriteSelectVisitedAtDialog.Delegate {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ko", "KR"))
        var currentDate: String = dateFormat.format(Date(System.currentTimeMillis()))

        override fun onSaveClick(value: String) {
            if (value.isNotEmpty()) {
                viewBinding.writeVisitedAt.writeOptionsItemBody.text = value
                nextIntent.putExtra("visitedAt", value)
            } else {
                viewBinding.writeVisitedAt.writeOptionsItemBody.text = currentDate
                nextIntent.putExtra("visitedAt", currentDate)
            }
        }
    }

    private val emojiDelegate = object :WriteSelectEmojiDialog.Delegate {
        override fun onSaveClick(value: String) {
            viewBinding.writeMarkerEmoji.writeOptionsItemBody.text = value
            nextIntent.putExtra("emoji", value)
        }
    }
}


