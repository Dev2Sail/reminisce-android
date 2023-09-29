package studio.hcmc.reminisce.ui.activity.setting

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import studio.hcmc.reminisce.ui.view.Navigation

class AccountSettingActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySettingAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingAccountBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initView()
        prepareUser()
    }

    private fun initView() {
        viewBinding.settingAccountAppbar.apply {
            appbarTitle.text = getText(R.string.setting_account)
            appbarActionButton1.isVisible = false
            appbarBack.setOnClickListener { finish() }
        }

        viewBinding.settingAccountNicknameIcon.setOnClickListener { launchNicknameSetting() }
        viewBinding.settingAccountPasswordIcon.setOnClickListener { launchPasswordSetting() }
        viewBinding.settingAccountWithdraw.setOnClickListener { WithdrawDialog(this, withdrawDelegate) }

        val menuId = intent.getIntExtra("settingMenuId", -1)
        viewBinding.settingAccountNavView.navItems.selectedItemId = menuId

        navController()
    }

    private fun prepareUser() = CoroutineScope(Dispatchers.Main).launch {
        val user = UserExtension.getUser(this@AccountSettingActivity)
        viewBinding.settingAccountEmailBody.text = user.email
        runCatching { UserIO.getByEmail(user.email) }
            .onSuccess { viewBinding.settingAccountNicknameBody.text = it.nickname }
            .onFailure {
                viewBinding.settingAccountNicknameBody.text = "닉네임을 불러올 수 없습니다."
                Log.v("reminisce Logger", "[reminisce > Account Setting > Prepare user] : msg - ${it.message} \n::  localMsg - ${it.localizedMessage} \n:: cause - ${it.cause} \n:: stackTree - ${it.stackTrace}")
            }
    }

    private val withdrawDelegate = object : WithdrawDialog.Delegate {
        override fun onDoneClick() {
            CoroutineScope(Dispatchers.IO).launch {
                val user = UserExtension.getUser(this@AccountSettingActivity)
                runCatching { UserIO.delete(user.id) }
                    .onSuccess {
                        UserAuthVO(user.email, user.password).delete(this@AccountSettingActivity)
                        // TODO Activity Result 회수
                        Intent(this@AccountSettingActivity, LauncherActivity::class.java).apply {
                            startActivity(this)
                        }
                    }
                    .onFailure {
                        CommonError.onDialog(this@AccountSettingActivity)
                        Log.v("reminisce Logger", "[reminisce > Account Setting > onDoneClick] : msg - ${it.message} \n::  localMsg - ${it.localizedMessage} \n:: cause - ${it.cause} \n:: stackTree - ${it.stackTrace}")
                    }
            }
        }
    }


    private fun navController() {
        viewBinding.settingAccountNavView.navItems.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.nav_main_home -> {
                    startActivity(Navigation.onNextHome(applicationContext, it.itemId))
                    finish()

                    true
                }
                R.id.nav_main_map -> {
                    true
                }
                R.id.nav_main_report -> {
                    startActivity(Navigation.onNextReport(applicationContext, it.itemId))
                    finish()

                    true
                }
                R.id.nav_main_setting -> {
                    true
                }

                else -> false
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