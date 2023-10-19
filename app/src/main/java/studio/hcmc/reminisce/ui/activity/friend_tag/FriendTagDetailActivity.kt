package studio.hcmc.reminisce.ui.activity.friend_tag

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import studio.hcmc.reminisce.databinding.ActivityFriendTagDetailBinding

class FriendTagDetailActivity : AppCompatActivity() {
    lateinit var viewBinding: ActivityFriendTagDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityFriendTagDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)


    }

    private fun initView() {

    }
}