package studio.hcmc.reminisce.ui.activity.setting

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import studio.hcmc.reminisce.databinding.ActivitySettingAccountBinding

class AccountSettingActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySettingAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingAccountBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.settingAccountAppbar.appbarTitle.text = "계정 설정"
        viewBinding.settingAccountAppbar.appbarActionButton1.isVisible = false
        viewBinding.settingAccountAppbar.appbarBack.setOnClickListener { finish() }
        viewBinding.settingAccountEmailContainer.isClickable = false

//        val newNickname = intent.getStringExtra("newUserNickname")
//        if (newNickname!!.isNotEmpty()) {
//            viewBinding.settingAccountNicknameBody.text = newNickname
//        } else {
//            viewBinding.settingAccountNicknameBody.text = "original nickname"
//        }

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