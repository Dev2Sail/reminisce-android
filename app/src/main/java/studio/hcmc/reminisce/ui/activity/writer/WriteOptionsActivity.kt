package studio.hcmc.reminisce.ui.activity.writer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityWriteOptionsBinding

class WriteOptionsActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityWriteOptionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityWriteOptionsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.writeOptionsAppbar.appbarBack.setOnClickListener { finish() }
        // 글 보여주는 화면으로 이동

        viewBinding.writeOptionsVisitedAt.writeOptionsItemBody.text = intent.getStringExtra("visitedAt")

        val location = viewBinding.writeOptionsLocation
        location.writeOptionsItemIcon.setImageResource(R.drawable.round_location_on_16)

        val markerEmoji = viewBinding.writeOptionsMarkerEmoji
        markerEmoji.writeOptionsItemIcon.setImageResource(R.drawable.round_add_reaction_16)
        markerEmoji.writeOptionsItemBody.text = intent.getStringExtra("emoji")

        val friendTag = viewBinding.writeOptionsFriendTag
        friendTag.writeOptionsItemIcon.setImageResource(R.drawable.round_group_16)

        val hashtag = viewBinding.writeOptionsTag
        hashtag.writeOptionsItemIcon.setImageResource(R.drawable.round_tag_16)




    }
}