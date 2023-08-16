package studio.hcmc.reminisce.ui.activity.writer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import studio.hcmc.reminisce.databinding.ActivityWriteOptionsBinding
import studio.hcmc.reminisce.vo.tag.TagVO

class WriteSettingActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityWriteOptionsBinding

    private val savedTags = ArrayList<TagVO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityWriteOptionsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.writeOptionsAppbar.appbarTitle.text = "추억 설정"
        viewBinding.writeOptionsAppbar.appbarActionButton1.text = "완료"
        viewBinding.writeOptionsAppbar.appbarBack.setOnClickListener {
            finish()
        }

        val visitedAt = viewBinding.writeOptionsVisitedAt
        val address = viewBinding.writeOptionsAddress
        val marker = viewBinding.writeOptionsMarker

        visitedAt.text = intent.getStringExtra("visitedAt")
        visitedAt.isClickable = false

//        address.text = intent.getStringExtra("address")
        address.isClickable = false

        marker.text = intent.getStringExtra("markerEmoji")
        marker.isClickable = false

        viewBinding.writeOptionsCategory.writeOptionsCardTitle.text = "폴더"
        viewBinding.writeOptionsCategory.writeOptionsCardBody.text = "선택된 폴더"

        viewBinding.writeOptionsFriend.writeOptionsCardTitle.text = "함께 방문한 사람"
        viewBinding.writeOptionsFriend.writeOptionsCardBody.text = "사랑둥이"

        viewBinding.writeOptionsTag.writeOptionsCardTitle.text = "태그"
        viewBinding.writeOptionsTag.writeOptionsCardIcon.setOnClickListener {
            AddTagDialog(this, addTagDelegate)
        }

    }

    private val addTagDelegate = object : AddTagDialog.Delegate {
        override val dbTagList: List<TagVO>
            get() = this@WriteSettingActivity.savedTags

        override fun onSaveClick(content: ArrayList<String>) {
            val bodyContentBuilder = StringBuilder()

            for (item in content) {
                bodyContentBuilder.append("#")
                bodyContentBuilder.append(item)
                bodyContentBuilder.append(" ")
            }
            viewBinding.writeOptionsTag.writeOptionsCardBody.text = bodyContentBuilder

        }

    }
}



// writingSetting은 삭제
//