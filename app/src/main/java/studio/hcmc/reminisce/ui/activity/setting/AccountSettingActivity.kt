package studio.hcmc.reminisce.ui.activity.setting

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivitySettingAccountBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.ui.activity.launcher.LauncherActivity

class AccountSettingActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySettingAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingAccountBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.settingAccountAppbar.appbarTitle.text = getText(R.string.setting_account)
        viewBinding.settingAccountAppbar.appbarActionButton1.isVisible = false
        viewBinding.settingAccountAppbar.appbarBack.setOnClickListener { finish() }
        viewBinding.settingAccountNicknameIcon.setOnClickListener { launchNicknameSetting() }
        viewBinding.settingAccountPasswordIcon.setOnClickListener { launchPasswordSetting() }
        viewBinding.settingAccountWithdraw.setOnClickListener {
            WithdrawDialog(this, withdrawDelegate)
        }

        prepareUser()
    }
    // TODO 서비스 탈퇴 && 서비스 탈퇴 시 activityResult 회수
    // TODO password 바뀌었을 때
    // TODO 닉네임 업데이트
    // TODO 비밀번호 업데이트
    private fun prepareUser() = CoroutineScope(Dispatchers.Main).launch {
        val user = UserExtension.getUser(this@AccountSettingActivity)
        viewBinding.settingAccountEmailBody.text = user.email
        runCatching { UserIO.getByEmail(user.email) }
            .onSuccess {
                viewBinding.settingAccountNicknameBody.text = it.nickname
            }
            .onFailure {
                viewBinding.settingAccountNicknameBody.text = "닉네임을 로드할 수 없습니다."
                it.cause
                it.message
                it.stackTrace
            }
    }

    private val withdrawDelegate = object : WithdrawDialog.Delegate {
        override fun onDoneClick() {
            CoroutineScope(Dispatchers.IO).launch {
                val user = UserExtension.getUser(this@AccountSettingActivity)
                runCatching { UserIO.delete(UserIO.getByEmail(user.email).id) }
                    .onSuccess {
                        Intent(this@AccountSettingActivity, LauncherActivity::class.java).apply {
                            startActivity(this)
                        }
                    }
                    .onFailure {
                        it.cause
                        it.message
                        it.stackTrace
                    }
            }
        }
    }

    private fun launchNicknameSetting() {
        Intent(this, AccountSettingDetailNicknameActivity::class.java).apply {
            startActivity(this)
        }
    }

    private fun launchPasswordSetting() {
        Intent(this, AccountSettingDetailPasswordActivity::class.java).apply {
            startActivity(this)
        }
    }
}