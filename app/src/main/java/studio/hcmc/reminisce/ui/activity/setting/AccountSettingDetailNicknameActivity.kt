package studio.hcmc.reminisce.ui.activity.setting

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout.END_ICON_CLEAR_TEXT
import studio.hcmc.reminisce.databinding.ActivitySettingAccountDetailNicknameBinding
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.util.text

class AccountSettingDetailNicknameActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySettingAccountDetailNicknameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingAccountDetailNicknameBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val appBar = viewBinding.settingAccountDetailNicknameAppbar
        appBar.appbarTitle.text = "닉네임 설정"
        appBar.appbarActionButton1.text = "완료"
        appBar.appbarActionButton1.isEnabled = false
        appBar.appbarBack.setOnClickListener { finish() }

        val inputField = viewBinding.settingAccountDetailNicknameField
        inputField.endIconMode = END_ICON_CLEAR_TEXT
        inputField.placeholderText = intent.getStringExtra("originalNickname")
        inputField.editText!!.addTextChangedListener {
            appBar.appbarActionButton1.isEnabled = inputField.text.isNotEmpty()
        }

        appBar.appbarActionButton1.setOnClickListener {
            val inputtedValue = inputField.string
            if (inputtedValue.length <= 20) {
                Intent(this, AccountSettingActivity::class.java).apply {
                    putExtra("newUserNickname", inputtedValue)
                    startActivity(this)
                }
            }
        }
    }
}