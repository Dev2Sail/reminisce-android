package studio.hcmc.reminisce.ui.activity.writer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import studio.hcmc.reminisce.databinding.ActivityWriteBinding
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class WriteActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityWriteBinding
    private val nextIntent by lazy { Intent(this, WriteSettingActivity::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityWriteBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.writeAppbar.appbarActionButton1.text = "저장"
//        viewBinding.writerAppbar.appbarTitle.text = intent.getStringExtra("selectedCategory")
        // appbarTitle = null이었다가 주소 검색 시 setTExt(주소)
        viewBinding.writeAppbar.appbarTitle.text = "selected location"
        viewBinding.writeVisitedAt.text = "<- 방문 날짜"
        viewBinding.writeAddress.text = "<- 주소"

        viewBinding.writeAppbar.appbarBack.setOnClickListener {
            StopWritingDialog(this)
        }
        viewBinding.writeVisitedAtIcon.setOnClickListener {
            InputVisitedAtDialog(this, visitedAtDelegate)
        }
        viewBinding.writeMarkerEmojiIcon.setOnClickListener {
            InputEmojiDialog(this, emojiDelegate)
        }
        viewBinding.writeAppbar.appbarActionButton1.setOnClickListener {
            nextIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(nextIntent)
        }
    }

    private val visitedAtDelegate = object : InputVisitedAtDialog.Delegate {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ko", "KR"))
        var currentDate: String = dateFormat.format(Date(System.currentTimeMillis()))
        override fun onSaveClick(value: String) {
            if (value.isNotEmpty()) {
                viewBinding.writeVisitedAt.text = value
                nextIntent.putExtra("visitedAt", value)
            } else {
                viewBinding.writeVisitedAt.text = currentDate
                nextIntent.putExtra("visitedAt", currentDate)
            }
        }
    }

    private val emojiDelegate = object : InputEmojiDialog.Delegate {
        override fun onSaveClick(value: String) {
            if (value != "") {
                viewBinding.writeMarkerEmoji.text = value
                nextIntent.putExtra("markerEmoji", value)
            } else {
                nextIntent.putExtra("markerEmoji", "")
            }
        }
    }
}


