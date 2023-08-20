package studio.hcmc.reminisce.ui.activity.setting

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivitySettingAccountDetailPasswordBinding
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.util.text

class AccountSettingDetailPasswordActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySettingAccountDetailPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingAccountDetailPasswordBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val appBar = viewBinding.settingAccountDetailPasswordAppbar
        appBar.appbarTitle.text = getText(R.string.setting_account_detail_password_title)
        appBar.appbarActionButton1.text = getText(R.string.appbar_button_text)
        appBar.appbarActionButton1.isEnabled = false
        appBar.appbarBack.setOnClickListener { finish() }

        val inputField = viewBinding.settingAccountDetailPasswordField
        inputField.editText!!.addTextChangedListener {
            appBar.appbarActionButton1.isEnabled = (inputField.text.isNotEmpty() && inputField.text.length >= 5)
        }

        appBar.appbarActionButton1.setOnClickListener {
            val inputtedValue = inputField.string
            Intent(this, AccountSettingActivity::class.java).apply {
                putExtra("newPassword", inputtedValue)
                startActivity(this)
            }
        }
    }
}