package studio.hcmc.reminisce.ui.activity.setting

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.settingHeader.commonHeaderTitle.text = "설정"
//        viewBinding.settingHeader.commonHeaderTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.round_settings_24, 0, 0, 0)
        viewBinding.settingHeader.commonHeaderAction1.isVisible = false
        viewBinding.settingAccount.writeOptionsCardTitle.text = "프로필 설정"
        viewBinding.settingFriend.writeOptionsCardTitle.text = "친구 목록"
        viewBinding.settingLogout.writeOptionsCardTitle.text = "로그아웃"
        viewBinding.settingLogout.writeOptionsCardIcon.setImageResource(R.drawable.round_exit_to_app_24)

        viewBinding.settingAccount.writeOptionsCardIcon.setOnClickListener {
            val intent = Intent(this, AccountSettingActivity::class.java)
            startActivity(intent)
        }

        viewBinding.settingFriend.writeOptionsCardIcon.setOnClickListener {
            Intent(this, FriendSettingActivity::class.java).apply {
                startActivity(this)
            }
        }

    }
}