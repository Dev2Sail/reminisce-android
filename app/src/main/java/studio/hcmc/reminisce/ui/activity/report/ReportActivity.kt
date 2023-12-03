package studio.hcmc.reminisce.ui.activity.report

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityReportBinding
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.LocationIO
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.navigationController
import studio.hcmc.reminisce.vo.friend.FriendVO
import studio.hcmc.reminisce.vo.location.LocationVO
import studio.hcmc.reminisce.vo.user.UserVO
import java.sql.Date

class ReportActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityReportBinding
    private lateinit var user: UserVO
    private lateinit var friends: List<FriendVO>
    private lateinit var serviceAreas: List<LocationVO>
    private lateinit var beachList: List<LocationVO>

    private var todayFlag = false

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
        viewBinding.reportHeader.commonHeaderTitle.text = getString(R.string.nav_main_report)
        loadContents()

    }

    private suspend fun prepareUser(): UserVO {
        if (!this::user.isInitialized) {
            user = UserExtension.getUser(this)
        }

        return user
    }

    private fun loadContents() = CoroutineScope(Dispatchers.IO).launch {
        val result = runCatching {
            val user = prepareUser()
            // 개별적이니까 바로 await?
            friends = async { FriendIO.mostStoredInLocationByUserId(user.id) }.await()
            serviceAreas = async { LocationIO.serviceAreaListByUserId(user.id, Int.MAX_VALUE) }.await()
            beachList = async { LocationIO.beachListByUserId(user.id, Int.MAX_VALUE) }.await()
            val today = Date(System.currentTimeMillis()).toString()
            val aYearAgoToday = buildString {
                append(today.substring(0, 3))
                append(today[3].digitToInt() - 1)
                append(today.substring(4, 10))
            }
            val yearAgoDeferred = async { LocationIO.yearAgoTodayByUserIdAndDate(user.id, aYearAgoToday, Int.MAX_VALUE) }
            if (yearAgoDeferred.await().isNotEmpty()) {
                todayFlag = true
            }
        }.onFailure {
            LocalLogger.e(it)
        }
        if (result.isSuccess) {
            withContext(Dispatchers.Main) { prepareContents() }
        }
    }

    private fun prepareContents() {
        prepareInternal()
        prepareFriendTag()
        prepareYearAgoToday()
    }

    private fun prepareInternal() {
        // 해수욕장 : https://www.data.go.kr/data/15058519/openapi.do
        val beachCnt = beachList.distinctBy { it.title }.size
        viewBinding.reportOceanBody.text = getString(R.string.report_ocean_body, beachCnt)
        viewBinding.reportOceanContainer.setOnClickListener {
            moveToInternalDetail(beach = true, serviceArea = false)
        }

        // 휴게소 : https://www.data.go.kr/data/15025446/standard.do#tab_layer_grid
        val serviceAreaCnt = serviceAreas.distinctBy { it.title }.size
        viewBinding.reportServiceAreaBody.text = getString(R.string.report_service_area_body, serviceAreaCnt)
        viewBinding.reportServiceAreaContainer.setOnClickListener {
            moveToInternalDetail(beach = false, serviceArea = true)
        }
    }

    private fun prepareFriendTag() {
        val friendText = friends.joinToString { it.nickname!! }
        if (friendText.isEmpty()) {
            viewBinding.reportFriendContainer.isGone = true
            viewBinding.reportCategoryFavoriteFriend.isGone = true
        } else {
            viewBinding.reportFriendContainer.isVisible = true
            viewBinding.reportCategoryFavoriteFriend.isVisible = true
            viewBinding.reportFriendContainer.isClickable = false
            viewBinding.reportFriendBody.text = friendText
        }
    }

    private fun prepareYearAgoToday() {
        val today = Date(System.currentTimeMillis()).toString()
        val aYearAgoToday = buildString {
            append(today.substring(0, 3))
            append(today[3].digitToInt() - 1)
            append(today.substring(4, 10))
        }
        if (todayFlag) {
            viewBinding.reportTodayContainer.isVisible = true
            viewBinding.reportCategoryYearAgo.isVisible = true
            viewBinding.reportTodayBody.text = aYearAgoToday
            viewBinding.reportTodayContainer.setOnClickListener {
                moveToYearAgoTodayDetail(aYearAgoToday)
            }
        } else {
            viewBinding.reportTodayContainer.isGone = true
            viewBinding.reportCategoryYearAgo.isGone = true
        }
    }

    private fun moveToInternalDetail(beach: Boolean, serviceArea: Boolean) {
        Intent(this, InternalDetailActivity::class.java).apply {
            putExtra("beach", beach)
            putExtra("serviceArea", serviceArea)
            startActivity(this)
        }
    }

    private fun moveToYearAgoTodayDetail(date: String) {
        Intent(this, YearAgoTodayActivity::class.java).apply {
            putExtra("date", date)
            startActivity(this)
        }
    }
}