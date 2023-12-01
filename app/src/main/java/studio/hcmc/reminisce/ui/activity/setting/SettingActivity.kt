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
    private val context = this

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
        viewBinding.settingAccountIcon.setOnClickListener { moveToAccountSetting() }
        viewBinding.settingFriendIcon.setOnClickListener { moveToFriendSetting() }
        viewBinding.settingSignOutIcon.setOnClickListener { SignOutDialog(context, signOutDelegate) }
    }

    private val signOutDelegate = object : SignOutDialog.Delegate {
        override fun onDoneClick() { onSignOut() }
    }

    private fun onSignOut() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(context)
        runCatching { removeUserAuthVO(user.email, user.password) }
            .onSuccess { moveToLauncher() }
            .onFailure {
                LocalLogger.e(it)
                withContext(Dispatchers.Main) { onErrorSignOut() }
            }
    }

    private suspend fun removeUserAuthVO(email: String, password: String) {
        UserAuthVO(email, password).delete(this)
        UserExtension.setUser(null)
    }

    private fun moveToLauncher() {
        Intent(this@SettingActivity, LauncherActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(this)
            finish()
        }
    }

    private fun onErrorSignOut() {
        Toast.makeText(this, getString(R.string.setting_sign_out_error), Toast.LENGTH_SHORT).show()
    }

    private fun moveToAccountSetting() {
        Intent(this, AccountSettingActivity::class.java).apply {
            putExtra("menuId", viewBinding.settingNavView.navItems.selectedItemId)
            startActivity(this)
        }
    }

    private fun moveToFriendSetting() {
        Intent(this, FriendsActivity::class.java).apply {
            putExtra("menuId", viewBinding.settingNavView.navItems.selectedItemId)
            startActivity(this)
        }
    }
}