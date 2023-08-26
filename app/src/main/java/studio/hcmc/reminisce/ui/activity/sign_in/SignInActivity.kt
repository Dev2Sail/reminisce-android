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
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivitySignInBinding
import studio.hcmc.reminisce.io.data_store.UserAuthVO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.ui.activity.home.HomeActivity
import studio.hcmc.reminisce.ui.activity.launcher.LauncherActivity
import studio.hcmc.reminisce.util.string

class SignInActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.signInAppbar.appbarTitle.text = getText(R.string.sign_in_login)
        viewBinding.signInAppbar.appbarActionButton1.isVisible = false

        viewBinding.signInAppbar.appbarBack.setOnClickListener {
            Intent(this, LauncherActivity::class.java).apply {
                startActivity(this)
            }
        }

        viewBinding.signInGoHome.setOnClickListener {
            Intent(this, HomeActivity::class.java).apply {
                startActivity(this)
            }
        }

        viewBinding.signInEmail.editText!!.addTextChangedListener {
            setNextEnabledState()
        }
        viewBinding.signInPassword.editText!!.addTextChangedListener {
            setNextEnabledState()
        }
        viewBinding.signInNext.setOnClickListener {
            val email = viewBinding.signInEmail.string
            val plainPassword = viewBinding.signInPassword.string

            CoroutineScope(Dispatchers.IO).launch {
                runCatching { UserIO.login(UserAuthVO(email, plainPassword)) }
                    .onSuccess {
                        UserAuthVO(email, plainPassword).save(this@SignInActivity)
                        Intent(this@SignInActivity, HomeActivity::class.java).apply {
                            startActivity(this)
                        }
                    }
                 .onFailure { onSignInError() }
            }
        }
    }
    private fun setNextEnabledState() {
        val inputtedEmail = viewBinding.signInEmail.string
        val checkedState = Patterns.EMAIL_ADDRESS.matcher(inputtedEmail).matches()
        val inputtedPassword = viewBinding.signInPassword.string
        viewBinding.signInNext.isEnabled = checkedState && (inputtedPassword.isNotEmpty() && inputtedPassword.length >= 5)
    }

    private fun onSignInError() = CoroutineScope(Dispatchers.Main).launch {
        SignInErrorDialog(this@SignInActivity)
    }

    private fun exceptionHandler(): CoroutineExceptionHandler = CoroutineExceptionHandler {_, exception ->
        Log.v("handle", "exception handled: $exception")
    }
}