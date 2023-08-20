package studio.hcmc.reminisce.ui.activity.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityAddFriendBinding

class AddFriendActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityAddFriendBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityAddFriendBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.addFriendAppbar.appbarTitle.text = getText(R.string.add_friend_title)
        viewBinding.addFriendAppbar.appbarActionButton1.isVisible = false
        viewBinding.addFriendAppbar.appbarBack.setOnClickListener {
            finish()
        }


    }
}

// 리사이클러뷰이니 adapter와 viewHolder가 필요함
// item의 root.setOnclick -> AddFriendDialog()