package studio.hcmc.reminisce.ui.activity.sign_in

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivitySignInBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.data_store.UserAuthVO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.ui.activity.home.HomeActivity
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.string

class SignInActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        viewBinding.signInAppbar.appbarTitle.text = getText(R.string.sign_in_login)
        viewBinding.signInAppbar.appbarActionButton1.isVisible = false
        viewBinding.signInAppbar.appbarBack.setOnClickListener { finish() }
        viewBinding.signInEmail.editText!!.addTextChangedListener { setNextEnabledState() }
        viewBinding.signInPassword.editText!!.addTextChangedListener { setNextEnabledState() }
        viewBinding.signInNext.setOnClickListener {
            onSignIn(viewBinding.signInEmail.string, viewBinding.signInPassword.string)
        }
    }

    private fun onSignIn(email: String, plainPassword: String) = CoroutineScope(Dispatchers.IO).launch {
        val auth = UserAuthVO(email, plainPassword)
        runCatching { UserIO.login(auth) }
            .onSuccess {
                patchUserAuthVO(email, plainPassword)
                UserExtension.setUser(it)
                moveToHome()
            }.onFailure {
                withContext(Dispatchers.Main) { SignInErrorDialog(this@SignInActivity) }
                LocalLogger.e(it)
            }
    }

    private suspend fun patchUserAuthVO(email: String, plainPassword: String) {
        UserAuthVO(email, plainPassword).save(this)
    }

    private fun setNextEnabledState() {
        val inputtedEmail = viewBinding.signInEmail.string
        val inputtedPassword = viewBinding.signInPassword.string
        val checkedState = Patterns.EMAIL_ADDRESS.matcher(inputtedEmail).matches()
        viewBinding.signInNext.isEnabled = checkedState && (inputtedPassword.isNotEmpty() && inputtedPassword.length >= 5)
    }

    private fun moveToHome() {
        Intent(this, HomeActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }
}