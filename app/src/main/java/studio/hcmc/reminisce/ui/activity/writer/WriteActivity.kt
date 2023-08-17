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

        viewBinding.writeAppbar.appbarBack.setOnClickListener {
            finish()
        }
        viewBinding.writeAppbar.appbarActionButton1.text = "저장"
        viewBinding.writeAppbar.appbarActionButton1.setOnClickListener {
            Intent(this, WriteOptionsActivity::class.java).apply {
                startActivity(this)
            }
        }

        viewBinding.writeVisitedAt.writeOptionsItemBody.text = "방문 날짜"
        viewBinding.writeVisitedAt.writeOptionsItemIcon.setOnClickListener {
            WriteSelectVisitedAtDialog(this, visitedAtDelegate)
        }

        viewBinding.writeMarkerEmoji.writeOptionsItemIcon.setImageResource(R.drawable.outline_add_reaction_16)
        viewBinding.writeMarkerEmoji.writeOptionsItemBody.text = ""
        viewBinding.writeMarkerEmoji.writeOptionsItemIcon.setOnClickListener {
            WriteSelectEmojiDialog(this, emojiDelegate)
        }


        viewBinding.writeLocation.writeOptionsItemIcon.setImageResource(R.drawable.outline_add_location_alt_16)
        viewBinding.writeLocation.writeOptionsItemBody.text = ""


        viewBinding.writeFriendTag.writeOptionsItemIcon.setImageResource(R.drawable.round_group_add_16)
        viewBinding.writeFriendTag.writeOptionsItemBody.text = ""


        viewBinding.writeTag.writeOptionsItemIcon.setImageResource(R.drawable.round_tag_16)
        viewBinding.writeTag.writeOptionsItemBody.text = ""
        viewBinding.writeTag.writeOptionsItemIcon.setOnClickListener {
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
            if (value != "") {
                viewBinding.writeMarkerEmoji.writeOptionsItemBody.text = value
                nextIntent.putExtra("emoji", value)
            } else {
                nextIntent.putExtra("emoji", "")
            }
        }
    }
}


