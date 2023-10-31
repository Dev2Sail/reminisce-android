package studio.hcmc.reminisce.ui.activity.sign_up

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.hcmc.kotlin.crypto.sha512
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivitySignUpBinding
import studio.hcmc.reminisce.dto.user.UserDTO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.ui.activity.home.HomeActivity
import studio.hcmc.reminisce.ui.activity.sign_in.SignInActivity
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.util.text

class SignUpActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initView()
    }

    private fun initView() {
        viewBinding.signUpAppbar.appbarTitle.text = getText(R.string.greeting_sign_up)
        viewBinding.signUpAppbar.appbarBack.setOnClickListener { finish() }
        viewBinding.signUpAppbar.appbarActionButton1.setOnClickListener {
            val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
            startActivity(intent)
        }
        viewBinding.signUpEmail.editText!!.addTextChangedListener { setNextEnabledState() }
        viewBinding.signUpPassword.editText!!.addTextChangedListener { setNextEnabledState() }
        viewBinding.signUpNickname.editText!!.addTextChangedListener { setNextEnabledState() }
        viewBinding.signUpNext.setOnClickListener {
            val dto = UserDTO.Post().apply {
                email = viewBinding.signUpEmail.string
                password = viewBinding.signUpPassword.string.sha512
                nickname = viewBinding.signUpNickname.string
            }
            signUp(dto)
        }
    }

    private fun signUp(dto: UserDTO.Post) = CoroutineScope(Dispatchers.IO).launch {
        runCatching { UserIO.getByEmail(dto.email) }
            .onSuccess {
                onSignUpError()
                LocalLogger.e(it.email)
            }
            .onFailure {
                UserIO.signUp(dto)
                onSignUpMessage()
                LocalLogger.v(it)
            }
    }

    private fun setNextEnabledState() {
        val inputtedEmail = viewBinding.signUpEmail.string
        val checkedState = Patterns.EMAIL_ADDRESS.matcher(inputtedEmail).matches()
        val inputtedPassword = viewBinding.signUpPassword.string
        val inputtedNickname = viewBinding.signUpNickname.string
        viewBinding.signUpNext.isEnabled = checkedState &&
                (inputtedPassword.isNotEmpty() && inputtedPassword.length >= 5) &&
                (inputtedNickname.isNotEmpty() && inputtedNickname.length <= 20)
    }

    private val signUpMessageDelegate = object : SignUpMessageDialog.Delegate {
        override fun onDoneClick() {
            Intent(this@SignUpActivity, HomeActivity::class.java).apply {
                startActivity(this)
                finish()
            }
        }
    }

    private val signUpErrorDelegate = object : SignUpErrorDialog.Delegate {
        override fun onDoneClick() {
            viewBinding.signUpEmail.text.clear()
            viewBinding.signUpPassword.text.clear()
            viewBinding.signUpNickname.text.clear()
        }
    }

    private fun onSignUpMessage() = CoroutineScope(Dispatchers.IO).launch {
        SignUpMessageDialog(this@SignUpActivity, signUpMessageDelegate)
    }

    private fun onSignUpError() = CoroutineScope(Dispatchers.IO).launch {
        SignUpErrorDialog(this@SignUpActivity, signUpErrorDelegate)
    }
}
