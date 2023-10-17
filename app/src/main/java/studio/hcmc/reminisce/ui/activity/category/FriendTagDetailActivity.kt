package studio.hcmc.reminisce.ui.activity.category

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.hcmc.reminisce.databinding.ActivityFriendTagDetailBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.LocationIO

class FriendTagDetailActivity : AppCompatActivity() {
    lateinit var viewBinding: ActivityFriendTagDetailBinding
    private val opponentId by lazy { intent.getIntExtra("opponentId", -1) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityFriendTagDetailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

    }

    private fun initView() {

        prepareContents()
    }

    private fun prepareContents() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@FriendTagDetailActivity)
        runCatching { LocationIO.listByUserIdAndOpponentId(user.id, opponentId) }
    }
}