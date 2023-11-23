package studio.hcmc.reminisce.ui.activity.report

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
import java.sql.Date

class ReportActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityReportBinding
    private lateinit var friends: List<FriendVO>
    private lateinit var serviceAreas: List<LocationVO>
    private lateinit var beachList: List<LocationVO>

    private val yearAgoToday = ArrayList<LocationVO>()

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

//        try {
//            CoroutineScope(Dispatchers.IO).launch { test3() }
//        } catch (e: Throwable) {
//            LocalLogger.e(e)
//        }
//        prepareContents()
        test2()
    }

    // 모든 변수는 서로에게 영향 끼치지 않음
    private fun test() = CoroutineScope(Dispatchers.IO).async {
        val user = UserExtension.getUser(this@ReportActivity)
        runCatching { FriendIO.mostStoredInLocationByUserId(user.id) }
    }

    private suspend fun test1() = coroutineScope {
        val user = UserExtension.getUser(this@ReportActivity)
        // 개별적이니까 바로 await?
        friends = async { FriendIO.mostStoredInLocationByUserId(user.id) }.await()
        serviceAreas = async { LocationIO.serviceAreaListByUserId(user.id) }.await()
        beachList = async { LocationIO.beachListByUserId(user.id) }.await()
        val today = Date(System.currentTimeMillis()).toString()
        val aYearAgoToday = buildString {
            append(today.substring(0, 3))
            append(today[3].digitToInt() - 1)
            append(today.substring(4, 10))
        }
        val yearAgoDeferred = async { LocationIO.yearAgoTodayByUserIdAndDate(user.id, aYearAgoToday) }.await()
        for (vo in yearAgoDeferred) {
            yearAgoToday.add(vo)
        }
    }

    private fun test2() = CoroutineScope(Dispatchers.IO).launch {
        val result = runCatching {
            val user = UserExtension.getUser(this@ReportActivity)
            // 개별적이니까 바로 await?
            friends = async { FriendIO.mostStoredInLocationByUserId(user.id) }.await()
            serviceAreas = async { LocationIO.serviceAreaListByUserId(user.id) }.await()
            beachList = async { LocationIO.beachListByUserId(user.id) }.await()
            val today = Date(System.currentTimeMillis()).toString()
            val aYearAgoToday = buildString {
                append(today.substring(0, 3))
                append(today[3].digitToInt() - 1)
                append(today.substring(4, 10))
            }
            val yearAgoDeferred = async { LocationIO.yearAgoTodayByUserIdAndDate(user.id, aYearAgoToday) }.await()
            if (yearAgoDeferred.isNotEmpty()) {
                for (item in yearAgoDeferred) {
                    yearAgoToday.add(item)
                }
            }
        }.onFailure { LocalLogger.e(it) }
        if (result.isSuccess) {
            withContext(Dispatchers.Main) { prepareContents() }
        }
    }

    private fun prepareContents() {
        val beachCnt = beachList.distinctBy { it.title }.size
        val serviceAreaCnt = serviceAreas.distinctBy { it.title }.size
        // 해수욕장 : https://www.data.go.kr/data/15058519/openapi.do
        viewBinding.reportOceanBody.text = getString(R.string.report_ocean_body, beachCnt)
        viewBinding.reportOceanContainer.setOnClickListener {
            Intent(this, InternalDetailActivity::class.java).apply {
                putExtra("beach", true)
                putExtra("serviceArea", false)
                startActivity(this)
            }
        }

        // 휴게소 : https://www.data.go.kr/data/15025446/standard.do#tab_layer_grid
        viewBinding.reportServiceAreaBody.text = getString(R.string.report_service_area_body, serviceAreaCnt)
        viewBinding.reportServiceAreaContainer.setOnClickListener {
            Intent(this, InternalDetailActivity::class.java).apply {
                putExtra("beach", false)
                putExtra("serviceArea", true)
                startActivity(this)
            }
        }

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

        val today = Date(System.currentTimeMillis()).toString()
        val aYearAgoToday = buildString {
            append(today.substring(0, 3))
            append(today[3].digitToInt() - 1)
            append(today.substring(4, 10))
        }
        if (yearAgoToday.isEmpty()) {
            viewBinding.reportTodayContainer.isGone = true
            viewBinding.reportCategoryYearAgo.isGone = true
        } else {
            viewBinding.reportTodayContainer.isVisible = true
            viewBinding.reportCategoryYearAgo.isVisible = true
            viewBinding.reportTodayBody.text = aYearAgoToday
        }
    }
}