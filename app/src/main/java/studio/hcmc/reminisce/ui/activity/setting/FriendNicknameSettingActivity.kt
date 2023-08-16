package studio.hcmc.reminisce.ui.activity.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import studio.hcmc.reminisce.databinding.ActivitySettingFriendNicknameBinding

class FriendNicknameSettingActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySettingFriendNicknameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingFriendNicknameBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.settingFriendNicknameAppbar.appbarTitle.text = "닉네임 수정"
        viewBinding.settingFriendNicknameAppbar.appbarActionButton1.isVisible = false
        viewBinding.settingFriendNicknameAppbar.appbarBack.setOnClickListener {
            finish()
        }

        viewBinding.settingFriendNickname.placeholderText = "친구가 설정한 이름"
        viewBinding.settingFriendNicknameAction1.text = "완료"
        viewBinding.settingFriendNicknameAction1.setOnClickListener {
            finish()
        }



    }
}
