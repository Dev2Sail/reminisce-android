package studio.hcmc.reminisce.ui.activity.setting

import android.app.Activity
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
import studio.hcmc.reminisce.io.data_store.UserAuthVO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.ui.activity.launcher.LauncherActivity
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.navigationController
import studio.hcmc.reminisce.util.setActivity

class AccountSettingActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySettingAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingAccountBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initView()
    }

    private fun initView() {
        prepareUser()
        val menuId = intent.getIntExtra("menuId", -1)
        navigationController(viewBinding.settingAccountNavView, menuId)
        viewBinding.settingAccountAppbar.appbarTitle.text = getText(R.string.setting_account)
        viewBinding.settingAccountAppbar.appbarActionButton1.isVisible = false
        viewBinding.settingAccountAppbar.appbarBack.setOnClickListener { finish() }
        viewBinding.settingAccountNicknameIcon.setOnClickListener { launchNicknameSetting() }
        viewBinding.settingAccountPasswordIcon.setOnClickListener { launchPasswordSetting() }
        viewBinding.settingAccountWithdraw.setOnClickListener { WithdrawDialog(this, withdrawDelegate) }
    }

    private fun prepareUser() = CoroutineScope(Dispatchers.Main).launch {
        val user = UserExtension.getUser(this@AccountSettingActivity)
        viewBinding.settingAccountEmailBody.text = user.email
        runCatching { UserIO.getByEmail(user.email) }
            .onSuccess { viewBinding.settingAccountNicknameBody.text = it.nickname }
            .onFailure {
                viewBinding.settingAccountNicknameBody.text = getString(R.string.error_get_nickname)
                LocalLogger.e(it)
            }
    }

    private val withdrawDelegate = object : WithdrawDialog.Delegate {
        override fun onDoneClick() {
            CoroutineScope(Dispatchers.IO).launch {
                val user = UserExtension.getUser(this@AccountSettingActivity)
                runCatching { UserIO.delete(user.id) }
                    .onSuccess {
                        UserAuthVO(user.email, user.password).delete(this@AccountSettingActivity)
                        launchLauncher()
                    }
                    .onFailure {
                        CommonError.onDialog(this@AccountSettingActivity)
                        LocalLogger.e(it)
                    }
            }
        }
    }

    private fun launchLauncher() {
        //TODO launcher에서 activityResult
        val intent = Intent(this, LauncherActivity::class.java).setActivity(this, Activity.RESULT_OK)

    }

    private fun launchNicknameSetting() {
        Intent(this, AccountSettingEditNicknameActivity::class.java).apply {
            startActivity(this)
        }
    }

    private fun launchPasswordSetting() {
        Intent(this, AccountSettingEditPasswordActivity::class.java).apply {
            startActivity(this)
        }
    }
}
