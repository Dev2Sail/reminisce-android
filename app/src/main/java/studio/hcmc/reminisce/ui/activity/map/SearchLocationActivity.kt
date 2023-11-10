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

    private val contents = ArrayList<SearchLocationAdapter.Content>()
    private val placeInfo = HashMap<String /* placeId */, Place>()
    private val roadAddress = HashMap<String /* placeId */, String?>()

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
                contents.removeAll {it is SearchLocationAdapter.Content}
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
                for (place in places) {
                    placeInfo[place.id] = Place(place.place_name, place.x, place.y)
                }
            }.onFailure { LocalLogger.e(it) }
        if (result.isSuccess) {
            prepareGetRoadAddress()
//            prepareContents()
//                withContext(Dispatchers.Main) { onContentsReady() }

        }
    }

    private fun prepareGetRoadAddress() {
        LocalLogger.v("place size: ${places.size}")
        for (place in places) {
            val finalAddress = if (place.road_address_name == "") place.address_name else place.road_address_name
            getRoadAddress(place.id, finalAddress)
        }
        LocalLogger.v("roadAddress size: ${roadAddress.size}")
    }

    private fun getRoadAddress(placeId: String, address: String) = CoroutineScope(Dispatchers.IO).launch {
        runCatching { MoisIO.getRoadAddress(address) }
            .onSuccess {
                if (it.results.juso != null) {
                    for (info in it.results.juso.withIndex()) {
                        if (info.index == 0) {
//                            roadAddress[placeId] = info.value.roadAddr

                        }
                    }
                } else { roadAddress[placeId] = null }
            }.onFailure { LocalLogger.e(it) }
    }

    private fun test(size: Int,)

    private fun validate() {
//        for (place in places) {
//            LocalLogger.v("places -> ${place.id}: ${place.address_name}")
//        }
        for (info in roadAddress) {
            LocalLogger.v("roadAddress -> ${info.key}: ${info.value}")
        }
    }

    private fun prepareContents() {
        contents.addAll(places.map { SearchLocationAdapter.PlaceContent(
            it.id, it.place_name, categoryNameFormat(it.category_name), roadAddress[it.id] ?: ""
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
            fromWriteLauncher(placeId)
            launchWrite(placeId)
        }
    }

    private fun launchWrite(placeId: String) {
        Intent(this, WriteActivity::class.java).apply {
            putExtra("place", placeInfo[placeId]!!.placeName)
            putExtra("roadAddress", roadAddress[placeId])
            putExtra("longitude", placeInfo[placeId]!!.longitude.toDouble())
            putExtra("latitude", placeInfo[placeId]!!.latitude.toDouble())
            startActivity(this)
        }
    }

    private fun fromWriteLauncher(placeId: String) {
        Intent().apply {
            putExtra("place", placeInfo[placeId]!!.placeName)
            putExtra("roadAddress", roadAddress[placeId])
            putExtra("longitude", placeInfo[placeId]!!.longitude.toDouble())
            putExtra("latitude", placeInfo[placeId]!!.latitude.toDouble())
            setActivity(this@SearchLocationActivity, Activity.RESULT_OK)
        }
    }
}