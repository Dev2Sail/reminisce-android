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

        viewBinding.settingHeader.commonHeaderTitle.setText(R.string.setting_activity_header)
        viewBinding.settingHeader.commonHeaderAction1.isVisible = false

        viewBinding.settingAccountIcon.setOnClickListener {
            Intent(this, AccountSettingActivity::class.java).apply {
                startActivity(this)
            }
        }
        viewBinding.settingFriendIcon.setOnClickListener {
            Intent(this, FriendSettingActivity::class.java).apply {
                startActivity(this)
            }
        }
    }
}

//        viewBinding.settingHeader.commonHeaderTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.round_settings_24, 0, 0, 0)