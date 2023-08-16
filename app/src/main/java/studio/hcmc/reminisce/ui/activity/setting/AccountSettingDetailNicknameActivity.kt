package studio.hcmc.reminisce.ui.activity.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputLayout.END_ICON_CLEAR_TEXT
import studio.hcmc.reminisce.databinding.ActivitySettingAccountDetailNicknameBinding

class AccountSettingDetailNicknameActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySettingAccountDetailNicknameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingAccountDetailNicknameBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.settingAccountDetailNicknameAppbar.appbarTitle.text = "닉네임"
        viewBinding.settingAccountDetailNicknameAppbar.appbarActionButton1.isVisible = false
        viewBinding.settingAccountDetailNicknameAppbar.appbarBack.setOnClickListener {
            finish()
        }

        val inputContainer = viewBinding.settingAccountDetailNickname.commonEditableHeaderInput
        val saveButton = viewBinding.settingAccountDetailNickname.commonEditableHeaderAction1
        inputContainer.placeholderText = "현재 사용중인 닉네임"
        inputContainer.isCounterEnabled = true
        inputContainer.counterMaxLength = 20
        inputContainer.endIconMode = END_ICON_CLEAR_TEXT
        saveButton.text = "완료"
        saveButton.setOnClickListener {
            finish()
        }
    }
}