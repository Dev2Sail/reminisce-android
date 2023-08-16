package studio.hcmc.reminisce.ui.activity.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import studio.hcmc.reminisce.databinding.ActivityAddFriendBinding

class AddFriendActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityAddFriendBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityAddFriendBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.addFriendAppbar.appbarTitle.text = "사용자 검색"
        viewBinding.addFriendAppbar.appbarActionButton1.isVisible = false
        viewBinding.addFriendAppbar.appbarBack.setOnClickListener {
            finish()
        }

        viewBinding.addFriendSearch.placeholderText = "오늘추억 사용자 이메일을 검색해 보세요"
    }
}

// 리사이클러뷰이니 adapter와 viewHolder가 필요함
// item의 root.setOnclick -> AddFriendDialog()