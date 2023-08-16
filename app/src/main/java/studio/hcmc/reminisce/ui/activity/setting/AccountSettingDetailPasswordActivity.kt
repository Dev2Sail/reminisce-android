package studio.hcmc.reminisce.ui.activity.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import studio.hcmc.reminisce.databinding.ActivitySettingAccountDetailPasswordBinding

class AccountSettingDetailPasswordActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySettingAccountDetailPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingAccountDetailPasswordBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.settingAccountDetailPasswordAppbar.appbarTitle.text = "비밀번호 수정"
        viewBinding.settingAccountDetailPasswordAppbar.appbarActionButton1.isVisible = false
        viewBinding.settingAccountDetailPasswordAppbar.appbarBack.setOnClickListener {
            finish()
        }

        viewBinding.settingAccountDetailPassword.placeholderText = "새로운 비밀번호"
        viewBinding.settingAccountDetailPasswordAction1.text = "완료"
        viewBinding.settingAccountDetailPasswordAction1.setOnClickListener {
            finish()
        }

    }
}