package studio.hcmc.reminisce.ui.activity.report

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityReportBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.LocationIO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.navigationController
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.location.LocationVO
import studio.hcmc.reminisce.vo.user.UserVO
import java.sql.Date

class ReportActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityReportBinding
    private lateinit var friends: List<FriendVO>
    private lateinit var serviceAreas: List<LocationVO>
    private lateinit var beachList: List<LocationVO>
    private lateinit var yearAgoToday: LocationVO

//    private val beach = ArrayList<LocationVO>()
//    private val serviceAreas = ArrayList<LocationVO>()

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
        viewBinding.reportHeader.commonHeaderAction1.isGone = true

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

            }.onFailure { LocalLogger.e(it) }
    }

    private fun loadFriend() = CoroutineScope(Dispatchers.IO).launch {
        // 가장 많이 태그된 친구 vo

    }

    private fun loadBeachLocations() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@ReportActivity)
        runCatching {  }
    }

    private suspend fun loadContents() = coroutineScope {
        val today = Date(System.currentTimeMillis()).toString()
//        val yearAgoToday = today.
            //val now = Date(System.currentTimeMillis())
        val result = runCatching {
            val user = UserExtension.getUser(this@ReportActivity)
            listOf(
//                launch { yearAgoToday = LocationIO.getYearAgoTodayByUserIdAndToday(user.id, ) },
                launch { beachList = LocationIO.beachListByUserId(user.id).sortedByDescending { it.id } },
                launch { serviceAreas = LocationIO.serviceAreaListByUserId(user.id).sortedByDescending { it.id } }

            ).joinAll()


        }

    }

    private fun prepareContents() {
        val beachCnt = beachList.distinctBy { it.title }.size
        val serviceAreaCnt = serviceAreas.distinctBy { it.title }.size
        viewBinding.reportOceanBody.text = getString(R.string.report_ocean_body, beachCnt)
        viewBinding.reportServiceAreaBody.text = getString(R.string.report_service_area_body, serviceAreaCnt)

    }


    // TODO 가장 많이 함께한 친구

    // TODO 해수욕장 방문 횟수
    // TODO 휴게소 방문 횟수
    // TODO layout card 통일
}