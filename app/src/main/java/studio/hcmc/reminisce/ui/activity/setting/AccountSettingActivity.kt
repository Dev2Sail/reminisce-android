package studio.hcmc.reminisce.ui.activity.setting

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivitySettingAccountBinding

class AccountSettingActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySettingAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingAccountBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.settingAccountAppbar.appbarTitle.text = getText(R.string.setting_account)
        viewBinding.settingAccountAppbar.appbarActionButton1.isVisible = false
        viewBinding.settingAccountAppbar.appbarBack.setOnClickListener { finish() }
        viewBinding.settingAccountEmailContainer.isClickable = false


        viewBinding.settingAccountNicknameIcon.setOnClickListener {
            val intent = Intent(this, AccountSettingDetailNicknameActivity::class.java)
            intent.putExtra("originalNickname", "originalNickname")
            startActivity(intent)
        }

        viewBinding.settingAccountPasswordIcon.setOnClickListener {
            Intent(this, AccountSettingDetailPasswordActivity::class.java).apply {
                startActivity(this)
            }
        }
        viewBinding.settingAccountWithdraw.setOnClickListener {
            WithdrawDialog(this)
        }
    }
}