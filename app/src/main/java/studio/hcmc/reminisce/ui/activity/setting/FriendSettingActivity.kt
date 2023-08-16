package studio.hcmc.reminisce.ui.activity.setting

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import studio.hcmc.reminisce.databinding.ActivitySettingFriendBinding

class FriendSettingActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySettingFriendBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingFriendBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.settingFriendAppbar.appbarTitle.text = "친구 목록"
        viewBinding.settingFriendAppbar.appbarActionButton1.isVisible = false
        viewBinding.settingFriendAppbar.appbarBack.setOnClickListener {
            finish()
        }

        viewBinding.settingFriendAdd.setOnClickListener {
            Intent(this, AddFriendActivity::class.java).apply {
                startActivity(this)
            }
        }
    }
}