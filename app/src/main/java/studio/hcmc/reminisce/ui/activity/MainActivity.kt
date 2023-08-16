package studio.hcmc.reminisce.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import studio.hcmc.reminisce.databinding.ActivityGreetingBinding
import studio.hcmc.reminisce.ui.activity.sign_in.SignInActivity
import studio.hcmc.reminisce.ui.activity.sign_up.SignUpActivity

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityGreetingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityGreetingBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.greetingSignUpBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        viewBinding.greetingSignInBtn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

    }


}

// Application
// Activity: Interaction
//  - Fragment
//   - ViewGroup(Layout)
//    - View

// onCreate
// onStart
// onResume
// onPause
// onStop
// onDestroy


// Service: Background
// BroadcastReceiver

// Context

// layout은 view 다
// 모든 layout은 viewGroup을 상속 받고 viewGroup은 view를 상속 받다

// viewBinding을 하기 위해서 viewBinding.root 사용
// setContentView(R.layout.file_name) 으로 사용한다면 해당 레이아웃으로 화면 표시, not binding
//        setContentView(R.layout.activity_example_main)