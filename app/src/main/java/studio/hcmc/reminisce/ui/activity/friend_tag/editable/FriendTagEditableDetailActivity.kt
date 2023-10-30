package studio.hcmc.reminisce.ui.activity.friend_tag.editable

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import studio.hcmc.reminisce.databinding.ActivityFriendTagEditableDetailBinding

class FriendTagEditableDetailActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityFriendTagEditableDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityFriendTagEditableDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)


    }

    private fun initView() {

    }
}