package studio.hcmc.reminisce.ui.activity.map

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.databinding.ActivitySearchLocationBinding
import studio.hcmc.reminisce.io.kakao.KKPlaceInfo
import studio.hcmc.reminisce.io.ktor_client.KakaoIO
import studio.hcmc.reminisce.io.ktor_client.MoisIO
import studio.hcmc.reminisce.ui.activity.writer.WriteActivity
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.setActivity
import studio.hcmc.reminisce.util.string

class SearchLocationActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySearchLocationBinding
    private lateinit var places: List<KKPlaceInfo>
    private lateinit var adapter: SearchLocationAdapter

    private val categoryId by lazy { intent.getIntExtra("categoryId", -1) }

    private val contents = ArrayList<SearchLocationAdapter.Content>()
    private val placeInfo = HashMap<String /* placeId */, Place>()
    private val roadAddress = HashMap<String /* placeId */, String>()

    private data class Place(
        val placeName: String,
        val longitude: String,
        val latitude: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySearchLocationBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        viewBinding.searchLocationBackIcon.setOnClickListener { finish() }
        viewBinding.searchLocationField.editText!!.setOnEditorActionListener { _, actionId, event ->
            val input = viewBinding.searchLocationField.string
            if (actionId == EditorInfo.IME_ACTION_SEARCH || event.keyCode == KeyEvent.KEYCODE_SEARCH && event.action == KeyEvent.ACTION_DOWN) {
                contents.clear()
                loadContentsByKakao(input)
                return@setOnEditorActionListener true
            }

            false
        }
    }

    private fun loadContentsByKakao(value: String) = CoroutineScope(Dispatchers.IO).launch {
        val result = runCatching {
            val kakaoResultDeferred = async { KakaoIO.listByKeyword(value) }
            val kakaoResult = kakaoResultDeferred.await()
            places = kakaoResult.documents
            for (place in kakaoResult.documents) {
                placeInfo[place.id] = Place(place.place_name, place.x, place.y)
                roadAddress[place.id] = if (place.road_address_name == "") place.address_name else place.road_address_name
            }
        }.onFailure { LocalLogger.e(it) }
        if (result.isSuccess) {
            prepareContents()
            withContext(Dispatchers.Main) { onContentsReady() }
        }
    }

    private fun prepareContents() {
        contents.addAll(places.map { SearchLocationAdapter.PlaceContent(
            it.id,
            it.place_name,
            categoryNameFormat(it.category_name),
            roadAddress[it.id]!!
        ) })
    }

    private fun categoryNameFormat(value: String): String {
        return value.split(">").last().trim(' ')
    }

    private fun onContentsReady() {
        viewBinding.searchLocationItems.layoutManager = LinearLayoutManager(this)
        adapter = SearchLocationAdapter(adapterDelegate, itemDelegate)
        viewBinding.searchLocationItems.adapter = adapter
    }

    private val adapterDelegate = object : SearchLocationAdapter.Delegate {
        override fun getItemCount() = contents.size
        override fun getItem(position: Int) = contents[position]
    }

    private val itemDelegate = object : SearchLocationItemViewHolder.Delegate {
        override fun onClick(placeId: String) {
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    LocalLogger.v("now : ${onTransformAddressByMois(placeId)}")
                    if (categoryId == -1) {
                        moveToWrite(placeId, onTransformAddressByMois(placeId))
                    } else {
                        launchWrite(placeId, onTransformAddressByMois(placeId))
                    }
                }
            } catch (e: Throwable) {
                LocalLogger.e(e)
            }
        }
    }

    private suspend fun onTransformAddressByMois(placeId: String): String = coroutineScope {
        val keyword = roadAddress[placeId]
        val moisDeferred = async { MoisIO.getRoadAddress(keyword!!) }
        val moisResponse = moisDeferred.await()
        val moisFlag = moisResponse.results.common
        val finalAddress: String
        if (moisFlag.totalCount != "0" && moisResponse.results.juso != null) {
            finalAddress = moisResponse.results.juso[0].roadAddr
        } else {
            finalAddress = buildAddress(keyword!!)
        }

        return@coroutineScope finalAddress
    }.toString()


    private enum class InternalIdentifier {
        서울특별시, 인천광역시, 부산광역시, 대전광역시,
        대구광역시, 광주광역시, 울산광역시, 경기도,
        충청북도, 충청남도, 전라북도, 전라남도, 경상북도, 경상남도;
    }

    private fun buildAddress(addressByKakao: String): String {
        val kakaoAddr = addressByKakao.split(" ")
        return buildString {
            when(kakaoAddr[0]) {
                "서울" -> append(InternalIdentifier.서울특별시)
                "인천" -> append(InternalIdentifier.인천광역시)
                "부산" -> append(InternalIdentifier.부산광역시)
                "대전" -> append(InternalIdentifier.대전광역시) // 구/군
                "대구" -> append(InternalIdentifier.대구광역시) // 구/군
                "광주" -> append(InternalIdentifier.광주광역시) // 구/군
                "울산" -> append(InternalIdentifier.울산광역시) // 구/군
                "경기" -> append(InternalIdentifier.경기도)
                "충북" -> append(InternalIdentifier.충청북도) // -> 시 -> 구/동/읍/면
                "충남" -> append(InternalIdentifier.충청남도) // -> 시 -> 구/동/읍/면
                "전북" -> append(InternalIdentifier.전라북도) // -> 시 -> 구/동/읍/면
                "전남" -> append(InternalIdentifier.전라남도) // -> 시 -> 구/동/읍/면
                "경북" -> append(InternalIdentifier.경상북도) // -> 시 -> 구/동/읍/면
                "경남" -> append(InternalIdentifier.경상남도) // -> 시 -> 구/동/읍/면
                else -> append(kakaoAddr[0])
            }
            append(" ")
            append(kakaoAddr[1])
            append(" ")
            if (this.contains("특별시") || this.contains("광역시")) {
                append(kakaoAddr[2])
            } else if (this.contains("도")) {
                append(kakaoAddr[2])
            }
        }
    }

    private fun moveToWrite(placeId: String, roadAddress: String) {
        Intent(this, WriteActivity::class.java).apply {
            putExtra("place", placeInfo[placeId]!!.placeName)
            putExtra("roadAddress", roadAddress)
            putExtra("longitude", placeInfo[placeId]!!.longitude.toDouble())
            putExtra("latitude", placeInfo[placeId]!!.latitude.toDouble())
            startActivity(this)
            finish()
        }
    }

    private fun launchWrite(placeId: String, roadAddress: String) {
        Intent().apply {
            putExtra("place", placeInfo[placeId]!!.placeName)
            putExtra("roadAddress", roadAddress)
            putExtra("longitude", placeInfo[placeId]!!.longitude.toDouble())
            putExtra("latitude", placeInfo[placeId]!!.latitude.toDouble())
            setActivity(this@SearchLocationActivity, Activity.RESULT_OK)
            finish()
        }
    }
}
/*
경로 1. MapActivity -> SearchLocationActivity (defaultCategoryId) -> WriteActivity
경로 2. HomeActivity -> CategoryDetailActivity (selectedCategoryId) -> WriteActivity
 */