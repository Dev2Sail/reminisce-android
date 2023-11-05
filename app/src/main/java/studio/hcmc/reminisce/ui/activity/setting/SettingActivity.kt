package studio.hcmc.reminisce.ui.activity.setting

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivitySettingBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.data_store.UserAuthVO
import studio.hcmc.reminisce.ui.activity.launcher.LauncherActivity
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.navigationController

class SettingActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        val menuId = intent.getIntExtra("menuId", -1)
        navigationController(viewBinding.settingNavView, menuId)

        viewBinding.settingHeader.commonHeaderTitle.text = getString(R.string.nav_main_setting)
        viewBinding.settingHeader.commonHeaderAction1.isVisible = false
        viewBinding.settingAccountIcon.setOnClickListener { launchAccountSetting() }
        viewBinding.settingFriendIcon.setOnClickListener { launchFriendSetting() }
        viewBinding.settingSignOutIcon.setOnClickListener { SignOutDialog(this@SettingActivity, signOutDelegate) }
    }

    private val signOutDelegate = object : SignOutDialog.Delegate {
        override fun onDoneClick() {
            signOut()
        }
    }

    private fun prepareSignOut() {


    }

    private fun signOut() = CoroutineScope(Dispatchers.IO).launch {
        // 1104 UserExtension. user 정보 삭제 안 됨
        val user = UserExtension.getUser(this@SettingActivity)
        runCatching { UserAuthVO(user.email, user.password).delete(this@SettingActivity) }
            .onSuccess {
                onSignOut()
                LocalLogger.v("result : ${UserExtension.getUserOrNull()?.id}")
            }
            .onFailure {
                withContext(Dispatchers.Main) { onErrorSignOut() }
                LocalLogger.e(it)
            }
    }

    private fun onSignOut() = CoroutineScope(Dispatchers.IO).launch {
        Intent(this@SettingActivity, LauncherActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }

    private fun onErrorSignOut() {
        Toast.makeText(this@SettingActivity, "로그아웃에 실패했어요", Toast.LENGTH_SHORT).show()
    }

    private fun launchAccountSetting() {
        Intent(this, AccountSettingActivity::class.java).apply {
            putExtra("menuId", viewBinding.settingNavView.navItems.selectedItemId)
            startActivity(this)
        }
    }

    private fun launchFriendSetting() {
        Intent(this, FriendsActivity::class.java).apply {
            putExtra("menuId", viewBinding.settingNavView.navItems.selectedItemId)
            startActivity(this)
        }
    }
}