package studio.hcmc.reminisce.ui.activity.sign_in

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivitySignInBinding
import studio.hcmc.reminisce.io.data_store.UserAuthVO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.ui.activity.home.HomeActivity
import studio.hcmc.reminisce.ui.activity.launcher.LauncherActivity
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
        viewBinding.signInAppbar.appbarBack.setOnClickListener { launchLauncher() }
        viewBinding.signInEmail.editText!!.addTextChangedListener { setNextEnabledState() }
        viewBinding.signInPassword.editText!!.addTextChangedListener { setNextEnabledState() }
        viewBinding.signInNext.setOnClickListener {
            val email = viewBinding.signInEmail.string
            val plainPassword = viewBinding.signInPassword.string
            signIn(email, plainPassword)
        }
    }

    private fun signIn(email: String, password: String) = CoroutineScope(Dispatchers.IO).launch {
        runCatching { UserIO.login(UserAuthVO(email, password)) }
            .onSuccess {
                UserAuthVO(email, password).save(this@SignInActivity)
                Intent(this@SignInActivity, HomeActivity::class.java).apply {
                    startActivity(this)
                    finish()
                }
            }.onFailure {
                withContext(Dispatchers.Main) { SignInErrorDialog(this@SignInActivity) }
                LocalLogger.e(it)
            }
    }

    private fun setNextEnabledState() {
        val inputtedEmail = viewBinding.signInEmail.string
        val checkedState = Patterns.EMAIL_ADDRESS.matcher(inputtedEmail).matches()
        val inputtedPassword = viewBinding.signInPassword.string
        viewBinding.signInNext.isEnabled = checkedState && (inputtedPassword.isNotEmpty() && inputtedPassword.length >= 5)
    }

    private fun launchLauncher() {
        Intent(this, LauncherActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }

    private fun exceptionHandler(): CoroutineExceptionHandler = CoroutineExceptionHandler {_, exception ->
        Log.v("handle", "exception handled: $exception")
    }
}