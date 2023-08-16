package studio.hcmc.reminisce.ui.activity.sign_up

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import studio.hcmc.reminisce.databinding.ActivitySignUpBinding
import studio.hcmc.reminisce.ui.activity.home.HomeActivity

class SignUpActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.signUpAppbar.appbarTitle.text = "회원가입"

        viewBinding.signUpAppbar.appbarBack.setOnClickListener {
            finish()
        }

        viewBinding.signUpAppbar.appbarActionButton1.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}
