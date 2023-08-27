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
    private val categoryId by lazy { intent.getIntExtra("categoryId", -1) }
    private val contents = HashMap<String, String>()

    // TODO write에서 작성한 내용을 write_options로 넘겨서 저장
    // MAP에 담아서 통으로 전달
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityWriteBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.writeAppbar.appbarBack.setOnClickListener { finish() }
        viewBinding.writeAppbar.appbarActionButton1.setOnClickListener { startActivity(nextIntent) }

        val visitedAt = viewBinding.writeVisitedAt
        visitedAt.writeOptionsItemBody.text = getText(R.string.write_visited_at)
        visitedAt.writeOptionsItemIcon.setOnClickListener {
            WriteSelectVisitedAtDialog(this, visitedAtDelegate)
        }

        val marker = viewBinding.writeMarkerEmoji
        marker.writeOptionsItemIcon.setImageResource(R.drawable.outline_add_reaction_16)
        marker.writeOptionsItemBody.text = getText(R.string.write_emoji)
        marker.writeOptionsItemIcon.setOnClickListener {
            WriteSelectEmojiDialog(this, emojiDelegate)
        }

        val location = viewBinding.writeLocation
        location.writeOptionsItemIcon.setImageResource(R.drawable.outline_add_location_alt_16)
        location.writeOptionsItemBody.text = getText(R.string.write_location)

        val friendTag = viewBinding.writeFriendTag
        friendTag.writeOptionsItemIcon.setImageResource(R.drawable.round_group_add_16)
        friendTag.writeOptionsItemBody.text = getText(R.string.write_friend)

        val hashtag = viewBinding.writeTag
        hashtag.writeOptionsItemIcon.setImageResource(R.drawable.round_tag_16)
        hashtag.writeOptionsItemBody.text = getText(R.string.write_hashtag)
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


