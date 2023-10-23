package studio.hcmc.reminisce.ui.activity.setting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivitySettingBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.data_store.UserAuthVO
import studio.hcmc.reminisce.ui.activity.MainActivity
import studio.hcmc.reminisce.ui.view.Navigation

class SettingActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initView()
    }

    private fun initView() {
        val menuId = intent.getIntExtra("selectedMenuId", -1)

        viewBinding.apply {
            settingHeader.commonHeaderTitle.text = getText(R.string.setting_activity_header)
            settingHeader.commonHeaderAction1.isVisible = false
            settingAccountIcon.setOnClickListener { launchAccountSetting() }
            settingFriendIcon.setOnClickListener { launchFriendSetting() }
            settingSignOutIcon.setOnClickListener { SignOutDialog(this@SettingActivity, signOutDelegate) }
            settingNavView.navItems.selectedItemId = menuId
        }

        navController()
    }

    private fun signOut() = CoroutineScope(Dispatchers.IO).launch {
        runCatching { UserAuthVO(UserExtension.getUser(this@SettingActivity).email, UserExtension.getUser(this@SettingActivity).password)
            .delete(this@SettingActivity) }
            .onSuccess { onSignOut() }
            .onFailure {
                errorSignOut()
                Log.v("reminisce Logger", "[reminisce > Setting > signOut] : msg - ${it.message} \n::  localMsg - ${it.localizedMessage} \n:: cause - ${it.cause} \n:: stackTree - ${it.stackTrace}")
            }
    }

    private fun errorSignOut() = CoroutineScope(Dispatchers.Main).launch {
        Toast.makeText(this@SettingActivity, "로그아웃에 실패했어요", Toast.LENGTH_SHORT).show()
    }

    private fun onSignOut() = CoroutineScope(Dispatchers.IO).launch {
        Intent(this@SettingActivity, MainActivity::class.java).apply {
            startActivity(this)
        }
    }

    private fun navController() {
        viewBinding.settingNavView.navItems.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.nav_main_home -> {
                    startActivity(Navigation.onNextHome(applicationContext, it.itemId))
                    finish()

                    true
                }
                R.id.nav_main_map -> {
                    startActivity(Navigation.onNextMap(applicationContext, it.itemId))
                    finish()

                    true
                }
                R.id.nav_main_report -> {
                    startActivity(Navigation.onNextReport(applicationContext, it.itemId))
                    finish()

                    true
                }
                R.id.nav_main_setting -> { true }
                else -> false
            }
        }
    }

    private val signOutDelegate = object : SignOutDialog.Delegate {
        override fun onDoneClick() { signOut() }
    }

    private fun launchAccountSetting() {
        Intent(this, AccountSettingActivity::class.java).apply {
            putExtra("settingMenuId", viewBinding.settingNavView.navItems.selectedItemId)
            startActivity(this)
        }
    }

    private fun launchFriendSetting() {
        Intent(this, FriendListActivity::class.java).apply {
            putExtra("settingMenuId", viewBinding.settingNavView.navItems.selectedItemId)
            startActivity(this)
        }
    }
}