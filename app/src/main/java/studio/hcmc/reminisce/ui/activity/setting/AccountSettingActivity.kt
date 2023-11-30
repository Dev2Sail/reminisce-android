package studio.hcmc.reminisce.ui.activity.setting

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivitySettingAccountBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.data_store.UserAuthVO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.ui.activity.launcher.LauncherActivity
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.navigationController
import studio.hcmc.reminisce.vo.user.UserVO

class AccountSettingActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySettingAccountBinding
    private lateinit var user: UserVO

    private val editNicknameLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), this::onEditNicknameResult)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingAccountBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        prepareUser()
    }

    private fun initView() {
        val menuId = intent.getIntExtra("menuId", -1)
        navigationController(viewBinding.settingAccountNavView, menuId)
        viewBinding.settingAccountAppbar.appbarTitle.text = getText(R.string.setting_account)
        viewBinding.settingAccountAppbar.appbarActionButton1.isVisible = false
        viewBinding.settingAccountAppbar.appbarBack.setOnClickListener { finish() }
        viewBinding.settingAccountEmailBody.text = user.email
        viewBinding.settingAccountNicknameBody.text = user.nickname
        viewBinding.settingAccountNicknameIcon.setOnClickListener { launchEditNickname() }
        viewBinding.settingAccountPasswordIcon.setOnClickListener { moveToEditPassword() }
        viewBinding.settingAccountWithdraw.setOnClickListener { WithdrawDialog(this, withdrawDelegate) }
    }

    private fun prepareUser() = CoroutineScope(Dispatchers.IO).launch {
        val userInfo = UserExtension.getUser(this@AccountSettingActivity)
        val result = runCatching { UserIO.getById(userInfo.id) }
            .onSuccess { user = it }
            .onFailure { LocalLogger.e(it) }
        if (result.isSuccess) {
            withContext(Dispatchers.Main) { initView() }
        }
    }

    private val withdrawDelegate = object : WithdrawDialog.Delegate {
        override fun onDoneClick() { onWithdrawn() }
    }

    private fun onWithdrawn() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@AccountSettingActivity)
        runCatching { UserIO.delete(user.id) }
            .onSuccess {
                removeUserAuthVO(user.email, user.password)
                moveToLauncher()
            }.onFailure {
                LocalLogger.e(it)
                CommonError.onDialog(this@AccountSettingActivity)
            }
    }

    private suspend fun removeUserAuthVO(email: String, password: String) {
        UserAuthVO(email, password).delete(this)
        UserExtension.setUser(null)
    }

    private fun moveToLauncher() {
        Intent(this, LauncherActivity::class.java).apply {
            // Intent.FLAG_ACTIVITY_NO_HISTORY -> 해당 Activity는 task stack에 쌓이지 않음
            // Intent.FLAG_ACTIVITY_CLEAR_TASK -> Intent.ACTIVITY_NEW_TASK와 함께 사용, task 내 다른 activity 모두 삭제
            // Intent.FLAG_ACTIVITY_CLEAR_TOP -> 기존 activity 제거 (상위 포함)하고 새로운 activity 생성
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(this)
            finish()
        }
    }

    private fun launchEditNickname() {
        val intent = Intent(this, AccountSettingEditNicknameActivity::class.java)
        editNicknameLauncher.launch(intent)
    }

    private fun onEditNicknameResult(activityResult: ActivityResult) {
        if (activityResult.data?.getBooleanExtra("isEdited", false) == true) {
            val nickname = activityResult.data?.getStringExtra("nickname")
            viewBinding.settingAccountNicknameBody.text = nickname!!.trim()
        }
    }

    private fun moveToEditPassword() {
        Intent(this, AccountSettingEditPasswordActivity::class.java).apply {
            startActivity(this)
        }
    }
}
