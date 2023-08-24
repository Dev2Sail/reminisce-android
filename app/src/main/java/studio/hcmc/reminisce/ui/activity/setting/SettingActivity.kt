package studio.hcmc.reminisce.ui.activity.setting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivitySettingBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.data_store.UserAuthVO

class SettingActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.settingHeader.commonHeaderTitle.text = getText(R.string.setting_activity_header)
        viewBinding.settingHeader.commonHeaderAction1.isVisible = false

        viewBinding.settingAccountIcon.setOnClickListener {
            Intent(this, AccountSettingActivity::class.java).apply {
                startActivity(this)
            }
        }
        viewBinding.settingFriendIcon.setOnClickListener {
            Intent(this, FriendSettingActivity::class.java).apply {
                startActivity(this)
            }
        }
        // signOut 클릭시 로그아웃 여부 묻는 dialog
        viewBinding.settingSignOutIcon.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                runCatching { UserAuthVO(
                    UserExtension.getUser(this@SettingActivity).email,
                    UserExtension.getUser(this@SettingActivity).password)
                    .delete(this@SettingActivity)
                }.onSuccess {

                }.onFailure {
                    onFailureDialog()
                }
            }

        }
    }
    private fun onFailureDialog() = CoroutineScope(Dispatchers.Main).launch {
        MaterialAlertDialogBuilder(this@SettingActivity)
            .setTitle("Failure")
            .setMessage("다시 로그인 해주세요")
            .setPositiveButton("메롱") { _, _ -> }
            .show()
    }
    private fun onSuccessDialog() = CoroutineScope(Dispatchers.Main).launch {
        // null 값 확인
        Log.v("userAuthModel", "===== User Auth Vo : ${UserAuthVO(this@SettingActivity)}")

        MaterialAlertDialogBuilder(this@SettingActivity)
            .setTitle("Success")
            .setMessage("성공")
            .setPositiveButton("메롱") { _, _ -> }
            .show()
    }
}