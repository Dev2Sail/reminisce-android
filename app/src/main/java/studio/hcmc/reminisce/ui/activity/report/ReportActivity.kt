package studio.hcmc.reminisce.ui.activity.report

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.hcmc.reminisce.databinding.ActivityReportBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.navigationController
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.user.UserVO

class ReportActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityReportBinding
    private lateinit var friends: List<FriendVO>

    private val users = HashMap<Int /* userId */, UserVO>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initView()
    }

    private fun initView() {
        val menuId = intent.getIntExtra("menuId", -1)
        navigationController(viewBinding.reportNavView, menuId)

    }

    private fun testLoad() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@ReportActivity)
        runCatching { FriendIO.addedListByUserId(user.id) }
            .onSuccess {
                friends = it

                for (friend in friends) {
                    if (friend.nickname == null) {
                        val opponent = UserIO.getById(friend.opponentId)
                        users[opponent.id] = opponent
                    }
                }

            }
            .onFailure { LocalLogger.e(it) }
    }
}