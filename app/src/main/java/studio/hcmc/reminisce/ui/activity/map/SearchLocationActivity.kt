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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.databinding.ActivitySearchLocationBinding
import studio.hcmc.reminisce.io.kakao.KKPlaceInfo
import studio.hcmc.reminisce.io.ktor_client.KakaoIO
import studio.hcmc.reminisce.io.ktor_client.MoisIO
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.setActivity
import studio.hcmc.reminisce.util.string

class SearchLocationActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySearchLocationBinding
    private lateinit var places: List<KKPlaceInfo>
    private lateinit var adapter: SearchLocationAdapter

    private val contents = ArrayList<SearchLocationAdapter.Content>()
    private val placeInfo = HashMap<String /* placeId */, Place>()
    private val roadAddress = HashMap<String /* placeId*/, String>()

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
                fetchSearchResult(input)

                return@setOnEditorActionListener true
            }

            false
        }
    }

    private fun fetchSearchResult(value: String) = CoroutineScope(Dispatchers.IO).launch {
        val result = runCatching { KakaoIO.listByKeyword(value) }
            .onSuccess {
                places = it.documents
                for (document in it.documents) {
                    placeInfo[document.id] = Place(document.placeName, document.longitude, document.latitude)
                    if (document.roadAddressName.isNullOrEmpty()) {
                        getRoadAddress(document.id, document.addressName)
                    } else {
                        roadAddress[document.id] = document.roadAddressName
                    }
                }
            }.onFailure { LocalLogger.e(it) }
        if (result.isSuccess) {
            prepareContents()
            withContext(Dispatchers.Main) { onContentsReady() }
        }
    }

    private fun getRoadAddress(placeId: String, address: String) = CoroutineScope(Dispatchers.IO).launch {
        runCatching { MoisIO.transformAddress(address) }
            .onSuccess {
                LocalLogger.v("${it.juso.roadAddr}")
                roadAddress[placeId] = it.juso.roadAddr
            } // 도로명 주소 통일 }
            .onFailure { LocalLogger.e(it) }
    }

    private fun prepareContents() {
        for (place in places) {
            contents.add(SearchLocationAdapter.PlaceContent(
                place.id,
                place.placeName,
                place.categoryName.split(">").last().trim(' '),
//                place.roadAddressName.ifEmpty { roadAddress[place.id]!! } // ifEmpty moisIO 요청 후 도로명주소 기재
                roadAddress[place.id]!!
            ))
        }
        //places.mapTo(contents) { SearchLocationAdapter.PlaceContent(it.place_name, it.category_name.split(">").last(), it.road_address_name) }
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
        override fun onClick(placeId: String, placeName: String, roadAddress: String) {
            Intent()
                .putExtra("place", placeName)
                .putExtra("roadAddress", roadAddress)
                .putExtra("longitude", placeInfo[placeId]!!.longitude.toDouble())
                .putExtra("latitude", placeInfo[placeId]!!.latitude.toDouble())
//                .putExtra("longitude", coords[placeId]!!.split(",")[0].toDouble())
//                .putExtra("latitude", coords[placeId]!!.split(",")[1].toDouble())
                .setActivity(this@SearchLocationActivity, Activity.RESULT_OK)
            finish()
        }
    }
}