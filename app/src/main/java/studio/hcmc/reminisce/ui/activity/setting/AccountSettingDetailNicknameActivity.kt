package studio.hcmc.reminisce.ui.activity.setting

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import studio.hcmc.reminisce.R
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
        appBar.appbarTitle.text = getText(R.string.setting_account_detail_nickname_title)
        appBar.appbarActionButton1.text = getText(R.string.appbar_button_text)
        appBar.appbarActionButton1.isEnabled = false
        appBar.appbarBack.setOnClickListener { finish() }

        val inputField = viewBinding.settingAccountDetailNicknameField

        inputField.placeholderText = intent.getStringExtra("originalNickname")
        inputField.editText!!.addTextChangedListener {
            appBar.appbarActionButton1.isEnabled = inputField.text.isNotEmpty()
        }

        appBar.appbarActionButton1.setOnClickListener {
            if (inputField.string.length <= 20) {
                Intent(this, AccountSettingActivity::class.java).apply {
                    putExtra("newUserNickname", inputField.string)
                    startActivity(this)
                }
            }
        }
    }
}